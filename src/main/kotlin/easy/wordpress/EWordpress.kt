@file:Suppress("unused")

package easy.wordpress

import com.afrozaar.wordpress.wpapi.v2.Wordpress
import com.afrozaar.wordpress.wpapi.v2.model.Term
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.net.URI
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

class EWordpress(private val wp: Wordpress) {
    private val tagCache: Cache<String, Term> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build()

    private val categoryCache: Cache<String, Term> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build()

    /** Cache that maps SHA‑256 hashes of file content to the media item's URL (to avoid re‑uploading identical content). */
    private val contentCache: Cache<String, String> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build()

    /**
     * 通用的获取或创建Term逻辑
     *
     * @param name 要获取或创建的Term名称
     * @param cache 缓存对象（标签或分类的缓存）
     * @param existingTerms 从WordPress获取的已有Term集合
     * @param createTerm 创建新Term的逻辑（标签或分类的创建方法）
     * @return 获取或创建后的Term
     */
    private fun getOrCreateTerm(
        name: String,
        cache: Cache<String, Term>,
        existingTerms: List<Term>,
        createTerm: (Term) -> Term
    ): Term {
        // 先尝试从缓存中获取
        var term = cache.getIfPresent(name)
        if (term == null) {
            // 缓存为空或没有该Term，则加载所有已有的Term到缓存中
//            println("term == null")
            existingTerms.forEach { t ->
                cache.put(t.name, t)
            }
            // 再次尝试从缓存中获取
            term = cache.getIfPresent(name)
        }
        if (term != null) {
            return term
        }
        // 如果仍未找到，则创建新的Term
        val newTerm = Term().apply {
            this.name = name
//            this.description = ""
//            this.slug = java.net.URLEncoder.encode(name, "UTF-8")
        }
//        println(newTerm)
        val crnewTerm = createTerm(newTerm)
        cache.put(name, crnewTerm)
        return crnewTerm
    }

    // private fun restTemplate(): RestTemplate { ... }  // no longer needed

    /**
     * 通过 JSON 请求体创建标签，使用 Wordpress.doCustomExchange 发送请求，
     * 免去直接操作 RestTemplate 的反射复杂度。
     */
    private fun createTagJson(tag: Term): Term {
        // Afrozaar Wordpress 的 doCustomExchange 需要显式传入各参数
        val response: ResponseEntity<Term> = wp.doCustomExchange(
            "/tags",            // context (相对路径)
            HttpMethod.POST,          // HTTP 方法
            Term::class.java,         // 预期返回类型
            emptyArray(),             // URI 模板占位符
            emptyMap<String, Any>(),  // 查询参数
            tag,                      // 请求体
            MediaType.APPLICATION_JSON
        )

        return response.body ?: throw IllegalStateException("Tag creation response body is null")
    }

    /**
     * 获取或创建标签
     *
     * @param name 标签名称
     * @return 标签对应的Term
     */
    fun getOrCreateTag(name: String): Term {
        return getOrCreateTerm(name, tagCache, wp.tags) { createTagJson(it) }
    }

    /**
     * 获取或创建分类
     *
     * @param name 分类名称
     * @return 分类对应的Term
     */
    fun getOrCreateCategory(name: String): Term {
        return getOrCreateTerm(name, categoryCache, wp.categories) { wp.createCategory(it) }
    }
    /**
     * 获取当前的 {@link Wordpress} 实例。
     *
     * @return 当前 Wordpress 对象。
     */
    fun getWp(): Wordpress = wp

    /**
     * Compute SHA‑256 hash of the given byte array and return it as a lowercase hex string.
     */
    private fun sha256(data: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(data)
        val sb = StringBuilder(digest.size * 2)
        for (b in digest) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }

    /**
     * Upload a local file or a remote URL to the WordPress media library.
     * If a file with the same filename **or identical content** already exists, the existing file's URL is returned and the upload is skipped.
     *
     * @param source Path to the local file or an HTTP(S) URL.
     * @param filename Optional file name to use on the server; if null, the name is derived from the source.
     * @return The absolute URL of the uploaded file on the WordPress server.
     */
    fun uploadFile(source: String, filename: String? = null): String {
        // 1) Load bytes from local file or remote URL
        val data: ByteArray
        val derivedName: String? // name inferred from path/URL if any

        if (source.startsWith("http://") || source.startsWith("https://")) {
            val uri = URI.create(source)
            val url = uri.toURL()
            data = url.openStream().use { it.readBytes() }
            derivedName = java.io.File(url.path).name.takeIf { it.isNotBlank() }
        } else {
            val file = java.io.File(source)
            require(file.exists()) { "File \$source does not exist" }
            data = file.readBytes()
            derivedName = file.name
        }

        // 2) Compute content hash once; both for duplicate detection and as fallback filename
        val contentHash = sha256(data)

        // 3) Determine final filename:
        //    If caller supplied `filename` use it directly.
        //    Otherwise use the contentHash while preserving the original extension (if any).
        val finalName = if (!filename.isNullOrBlank()) {
            filename
        } else {
            val ext = derivedName
                ?.substringAfterLast('.', missingDelimiterValue = "")
                ?.takeIf { it.isNotBlank() }
            if (ext != null) "$contentHash.$ext" else contentHash
        }

        // 4) If we've already uploaded identical bytes in this JVM process, return cached URL.
        contentCache.getIfPresent(contentHash)?.let { return it }

        // Avoid uploading duplicates: if a media item with the same filename already exists, return its URL.
        findExistingMediaUrl(finalName)?.let { return it }

        // Wrap the bytes in a Resource that exposes the desired filename
        val resource = object : ByteArrayResource(data) {
            override fun getFilename(): String = finalName
        }

        // Construct multipart/form-data request with a single part named "file"
        val parts: MultiValueMap<String, Any> = LinkedMultiValueMap()
        parts.add("file", resource)

        val response: ResponseEntity<Map<*, *>> = wp.doCustomExchange(
            "/media",
            HttpMethod.POST,
            Map::class.java,
            emptyArray(),
            emptyMap<String, Any>(),
            parts,
            MediaType.MULTIPART_FORM_DATA
        )

        val body = response.body ?: throw IllegalStateException("Upload response body is null")
        val uploadedUrl = body["source_url"]?.toString()
            ?: throw IllegalStateException("source_url missing in upload response")

        // Cache the hash so future identical uploads are skipped.
        contentCache.put(contentHash, uploadedUrl)
        return uploadedUrl
    }

    /**
     * Check whether a media item with the given filename already exists on the WordPress server.
     *
     * @param filename the exact filename to look for (case‑sensitive).
     * @return the existing media item's public URL, or {@code null} if no match is found.
     */
    private fun findExistingMediaUrl(filename: String): String? {
        // Query the WordPress REST API for media items matching the filename.
        val response: ResponseEntity<Array<Map<*, *>>> = wp.doCustomExchange(
            "/media?search=$filename&per_page=100",
            HttpMethod.GET,
            arrayOf<Map<*, *>>().javaClass,
            emptyArray(),
            emptyMap<String, Any>(),
            null,
            MediaType.APPLICATION_JSON
        )

        val items = response.body ?: return null
        return items.firstOrNull { (it["source_url"] as? String)?.endsWith("/$filename") == true }
            ?.get("source_url") as? String
    }
}