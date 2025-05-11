@file:Suppress("unused")

package easy.wordpress

import com.afrozaar.wordpress.wpapi.v2.Wordpress
import com.afrozaar.wordpress.wpapi.v2.model.Term
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.util.concurrent.TimeUnit

class EWordpress(private val wp: Wordpress) {
    private val tagCache: Cache<String, Term> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build()

    private val categoryCache: Cache<String, Term> = Caffeine.newBuilder()
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
}