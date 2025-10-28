# EASYWORDPRESS

_一个高性能的 Kotlin SDK，用缓存优先的方式简化 WordPress REST API 集成。_

[English](README.md) | 简体中文

## 概述
EASYWORDPRESS 基于 Afrozaar 的 WordPress v2 客户端封装出更易用的 Kotlin API，可快速管理标签、分类和媒体上传。智能缓存让重复调用更迅速，而内容去重机制能够跳过已存在的媒体文件。

## 核心特性
- **内置 Caffeine 缓存**：标签与分类查询自动缓存 1 小时，减少重复请求。
- **自动 Term 管理**：缺失的标签或分类会按需创建，无需手动处理。
- **内容感知的媒体上传**：通过 SHA-256 摘要判断文件是否已存在，避免重复上传。
- **Kotlin/JVM 友好**：默认启用 Java 21 工具链，并提供完备的 Gradle 发布任务以推送至 OSSRH。

## 安装
从 Maven Central（或本地 Maven 仓库）引入依赖：

```kotlin
dependencies {
    implementation("com.github.darkice1:easy-wordpress:0.0.3")
}
```

## 配置
在项目根目录创建 `config.properties`（真实凭据请勿提交）：

```properties
WPBASEURL=https://your-wordpress-site.example
WPUSERNAME=your-username
WPUSERPASSWD=your-application-password
```

## 使用示例
```kotlin
import com.afrozaar.wordpress.wpapi.v2.config.ClientConfig
import com.afrozaar.wordpress.wpapi.v2.config.ClientFactory
import easy.wordpress.EWordpress

val config = ClientConfig.of(baseUrl, username, appPassword, false, false)
val wp = ClientFactory.fromConfig(config)
val easyWp = EWordpress(wp)

val tag = easyWp.getOrCreateTag("hot-deals")
val category = easyWp.getOrCreateCategory("featured")
val mediaUrl = easyWp.uploadFile("/path/to/banner.png")
```

`uploadFile` 支持本地路径或 HTTP(S) 链接，并在发现文件名或内容相同的情况下复用现有媒体。

## 构建与测试
- `./gradlew clean build`：编译 Kotlin 2.1 代码，运行测试并打包 JAR。
- `./gradlew test`：执行 `kotlin.test` 测试套件，修改缓存或 REST 逻辑后务必运行。
- `./gradlew publiclocal`：将制品安装到本地 Maven 仓库，方便下游项目联调。
- `./gradlew publishAndCloseSonatype`：推送至 OSSRH 暂存库，需在 `~/.gradle/gradle.properties` 配置 `centralUsername` 与 `centralPassword`。

## 贡献指南
请遵循 `AGENTS.md` 中的约定，保持英文与中文文档同步更新，并在 PR 描述中附上测试结果（例如 `./gradlew test`）。若功能有所调整，请同步更新 `README.md` 与 `README.zh-CN.md` 的描述，并在 `CHANGELOG.md` 中按日期倒序记录本次变更。欢迎提交改进！

## 许可证
项目基于 MIT License 开源，详见 [LICENSE](LICENSE)。
