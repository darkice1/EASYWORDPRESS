@file:Suppress("unused")

package easy.wordpress

import com.afrozaar.wordpress.wpapi.v2.Wordpress
import com.afrozaar.wordpress.wpapi.v2.model.Term
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit

class EWordpress(private val wp: Wordpress) {
    // 使用Caffeine缓存，设置过期时间为1小时
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
            this.description = ""
        }
        val createdTerm = createTerm(newTerm)
        cache.put(name, createdTerm)
        return createdTerm
    }

    /**
     * 获取或创建标签
     *
     * @param name 标签名称
     * @return 标签对应的Term
     */
    fun getOrCreateTag(name: String): Term {
        return getOrCreateTerm(name, tagCache, wp.tags) { wp.createTag(it) }
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
}