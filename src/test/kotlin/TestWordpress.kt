import com.afrozaar.wordpress.wpapi.v2.config.ClientConfig
import com.afrozaar.wordpress.wpapi.v2.config.ClientFactory
import easy.wordpress.EWordpress
import java.io.FileInputStream
import java.util.*


object TestWordpress {
	@JvmStatic
	fun main(args: Array<String>) {
		val prop = Properties().apply {
			load(FileInputStream("config.properties"))
		}
		println(prop)

		val baseUrl = prop["WPBASEURL"].toString()
		val username = prop["WPUSERNAME"].toString()
		val appPassword = prop["WPUSERPASSWD"].toString()

		val wp = ClientFactory.builder(
			ClientConfig.of(baseUrl, username, appPassword, false, true)
		).build()



		val ewp = EWordpress(wp)
//		println(ewp.getOrCreateTag("最新热销产品"))
		println(ewp.getOrCreateCategory("快手菜C"))
		println(ewp.getOrCreateTag("快手菜T"))

//		println(ewp.getOrCreateCategory("快手菜"))

//		println(ewp.getPostAsJson(1085))
//		println(ewp.getOrCreateCategory("最新热销产品"))
	}
}