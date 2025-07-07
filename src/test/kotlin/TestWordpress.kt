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
//		println(ewp.getOrCreateCategory("快手菜C"))
//		println(ewp.getOrCreateTag("快手菜T"))
		println(ewp.uploadFile("https://prod-files-secure.s3.us-west-2.amazonaws.com/317544f3-4dac-493f-a708-03cb1380bc02/a583c76f-6ce8-45eb-ade8-4bd4ffc20805/image.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=ASIAZI2LB4666U3KIOC3%2F20250707%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20250707T062617Z&X-Amz-Expires=3600&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEGUaCXVzLXdlc3QtMiJHMEUCIHorL%2FW83oiIQKSUCUBz7nappObQp1YjnLNcEh80ZA%2B3AiEA8t5xvVR%2B7vsQuY1hFgjm2wQ1FkzbfsCDK2AqJVPTAN8q%2FwMIbhAAGgw2Mzc0MjMxODM4MDUiDEDwYZsUdOr2sXZH6SrcA3xg9MDiEkMfNbvODzT2L4XgdV4UqlcB9%2BYBMPiIpIxaYlYRxod6OOmGjN4PtF5Oonxy1b%2F%2FELEZUVF1eiSrb36SngcEnGLPAIsRIvzEJljsbTQyXzrYR11BDVnZKS0JFpyFjr3cMmw2XqVQzcPkgEXqNUlvz5HDGGeTuAolylHTAzImwqAHXr07M5%2FWs%2FsM0yaeiNUbqe8rOowhqXjEf%2BkiSsv8OspJB1GU2qmnYwPG1tR1zJDGZDJx%2FV%2F1f5GikmKhcKae3px3UOgTsITHqlPytvHzU9qjP%2BALniZERQXb6udSimRHvfZIpfmpcPWaXIobErw5TvYPF%2FtSCxFmke0L9xRZt6lbSH0S%2FFW4Zz8Zzu%2Fkr%2FqpuXwlAHKR1nMzWk1P%2F8xG5xpBBl4fhVOOjHC2P0WANMPAgC3dQpa9OEbXZ6TYORgFEcqOuj45LXdwQqsuUpTJQpbGq3bt59Gx7Q%2BO0i%2B3KSqsFcwdiFoTZrlQgwrJAddXs0yRgqyEawuHOALJgb7WiJnnva33YfeVGwUrOhWDw4knDvLv8IWPA%2FLCMeIkYTva0SpqTEtEvG9Zj7rwzVjY70GrvopSSgG0KcpptLQ%2BQfickVhMVmZRBI%2BZMsQQur0oFCvaidw4MMCarcMGOqUBg3BcqU5Ge5kEg%2F02OJDAqF3IWYfY4RlVCvhqQnnD%2B%2FlSeErUwx%2B0nqMW39os5PEyxKxAFK0dr9MD7oDHZ0oEpzSqThaCupP9k%2BsM5JhJYsRITe3tiOamVXHCMZvaR0RyvE%2BsHrQqD6UadbYlfWn1WsNexprbv5SQ07omLF6rl3xCqHEb3b%2FYxzoPcnj5AempyCYcewDYkJmaIr2xoOqdWrpe%2FGpc&X-Amz-Signature=38e0a1f0221e5fe3bd56666c8443a13a1c890b657718025ef8d8334bb68532b1&X-Amz-SignedHeaders=host&x-amz-checksum-mode=ENABLED&x-id=GetObject"))
		println(ewp.uploadFile("https://prod-files-secure.s3.us-west-2.amazonaws.com/317544f3-4dac-493f-a708-03cb1380bc02/a583c76f-6ce8-45eb-ade8-4bd4ffc20805/image.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=ASIAZI2LB4666U3KIOC3%2F20250707%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20250707T062617Z&X-Amz-Expires=3600&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEGUaCXVzLXdlc3QtMiJHMEUCIHorL%2FW83oiIQKSUCUBz7nappObQp1YjnLNcEh80ZA%2B3AiEA8t5xvVR%2B7vsQuY1hFgjm2wQ1FkzbfsCDK2AqJVPTAN8q%2FwMIbhAAGgw2Mzc0MjMxODM4MDUiDEDwYZsUdOr2sXZH6SrcA3xg9MDiEkMfNbvODzT2L4XgdV4UqlcB9%2BYBMPiIpIxaYlYRxod6OOmGjN4PtF5Oonxy1b%2F%2FELEZUVF1eiSrb36SngcEnGLPAIsRIvzEJljsbTQyXzrYR11BDVnZKS0JFpyFjr3cMmw2XqVQzcPkgEXqNUlvz5HDGGeTuAolylHTAzImwqAHXr07M5%2FWs%2FsM0yaeiNUbqe8rOowhqXjEf%2BkiSsv8OspJB1GU2qmnYwPG1tR1zJDGZDJx%2FV%2F1f5GikmKhcKae3px3UOgTsITHqlPytvHzU9qjP%2BALniZERQXb6udSimRHvfZIpfmpcPWaXIobErw5TvYPF%2FtSCxFmke0L9xRZt6lbSH0S%2FFW4Zz8Zzu%2Fkr%2FqpuXwlAHKR1nMzWk1P%2F8xG5xpBBl4fhVOOjHC2P0WANMPAgC3dQpa9OEbXZ6TYORgFEcqOuj45LXdwQqsuUpTJQpbGq3bt59Gx7Q%2BO0i%2B3KSqsFcwdiFoTZrlQgwrJAddXs0yRgqyEawuHOALJgb7WiJnnva33YfeVGwUrOhWDw4knDvLv8IWPA%2FLCMeIkYTva0SpqTEtEvG9Zj7rwzVjY70GrvopSSgG0KcpptLQ%2BQfickVhMVmZRBI%2BZMsQQur0oFCvaidw4MMCarcMGOqUBg3BcqU5Ge5kEg%2F02OJDAqF3IWYfY4RlVCvhqQnnD%2B%2FlSeErUwx%2B0nqMW39os5PEyxKxAFK0dr9MD7oDHZ0oEpzSqThaCupP9k%2BsM5JhJYsRITe3tiOamVXHCMZvaR0RyvE%2BsHrQqD6UadbYlfWn1WsNexprbv5SQ07omLF6rl3xCqHEb3b%2FYxzoPcnj5AempyCYcewDYkJmaIr2xoOqdWrpe%2FGpc&X-Amz-Signature=38e0a1f0221e5fe3bd56666c8443a13a1c890b657718025ef8d8334bb68532b1&X-Amz-SignedHeaders=host&x-amz-checksum-mode=ENABLED&x-id=GetObject"))



//		println(ewp.getOrCreateCategory("快手菜"))

//		println(ewp.getPostAsJson(1085))
//		println(ewp.getOrCreateCategory("最新热销产品"))
	}
}