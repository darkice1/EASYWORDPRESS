# Repository Guidelines

## 项目结构与模块组织
EASYWORDPRESS 是基于 Kotlin JVM 的 WordPress REST API SDK。核心实现位于 `src/main/kotlin/easy/wordpress/EWordpress.kt`，负责封装标签、分类与媒体上传的缓存策略，并与 Afrozaar Wordpress Client 集成，实现 caching layer 与 media deduplication logic。示例与集成测试放在 `src/test/kotlin/TestWordpress.kt`，包含 basic usage main 函数，可作为连接真实 WordPress 环境的参考。Gradle 配置与发布逻辑集中在 `build.gradle.kts` 与 `gradle/` 目录，定义 Java 21 toolchain、signing setup、Sonatype 发布任务及 Nexus configuration。构建产物写入 `build/`，若运行发布流程还会在 `target/` 下生成 Maven artifact。运行时凭据存放于根目录 `config.properties`，请结合 `.gitignore` 与本地密钥管理避免提交真实账号。

## 构建、测试与开发命令
- `./gradlew clean build`：清理旧产物、编译 Kotlin 2.1 源码、执行测试并打包 JAR，适合作为 CI pipeline 的默认入口。
- `./gradlew test`：运行 `kotlin.test` 套件，用于验证缓存失效策略或 REST 扩展方法，推荐结合 `--info` 排查失败原因。
- `./gradlew publiclocal`：安装工件到本地 Maven repository，便于 downstream project 在 `build.gradle` 中通过 `implementation("com.github.darkice1:easy-wordpress:local")` 进行验证。
- `./gradlew publishAndCloseSonatype`：推送到 Sonatype OSSRH 并关闭暂存库，需在 `~/.gradle/gradle.properties` 中配置 `centralUsername`、`centralPassword`，并确保网络代理允许访问。
- `./gradlew release`：一站式执行 publish、close、release，全流程依赖 `signingKey`、`signingPassword` 或 gpg agent；执行前确认 staging repository 无遗留版本。

## 代码风格与命名惯例
统一使用tab缩进、UTF-8 编码与 Unix 换行。公开类型采用 PascalCase（如 `EWordpress`），函数与属性使用 camelCase；缓存实例、配置常量建议以名词短语命名，例如 `tagCache`、`contentCache`。优先使用 `val` 声明不可变引用，必要时再改用 `var`，保持 thread safety。复杂流程（例如 multipart upload 或 custom exchange）应通过行内注释或 KDoc 简述 reason 与 side effect，确保 reviewer 能快速理解。

## 测试指南
项目使用 `kotlin.test` 断言库并依赖 JUnit runner。新增单元或集成测试文件请放在 `src/test/kotlin`，遵循 `*Test.kt` 命名；测试方法以 `@Test` 注解并采用 Given-When-Then 风格命名。涉及真实 WordPress 的测试需通过配置开关控制，例如读取 `config.properties` 的布尔字段，以确保 `./gradlew test` 在无凭据环境仍能稳定通过。当前未强制 coverage threshold，但推荐借助 `./gradlew test jacocoTestReport`（若启用插件）检测缓存命中与错误路径，测试结束后清理由测试创建的远程资源与临时媒体。

## 提交与拉取请求
提交信息遵循简短祈使句风格，示例：`Update .gitignore to include .kotlin directory`。一次提交聚焦单一问题，如同时修改缓存策略与发布配置，请拆分 commit。任何代码变更都需评估 README.md 与 README.zh-CN.md 是否需要同步更新；如需更新文档，务必保持两种语言内容一致。若功能行为发生变化，必须同步更新 README.md、README.zh-CN.md 的功能描述，并在 `CHANGELOG.md` 中按日期倒序追加条目。拉取请求需包含变更摘要、测试证明（例如执行 `./gradlew test`、截图或 curl response）、关联 issue 链接；若更改 Public API，请在描述中列出 breaking change checklist，并更新示例代码。提交前请确认未泄露任何凭据或令牌，必要时运行 `git status --short` 与 `git diff --stat` 自检。

## 沟通与安全提示
- 所有协作者与本助手的交流必须使用中文（包括问题、评论与回复），并在讨论中保留上下文，避免出现 only English message。
- `config.properties`、`signingKey`、`signingPassword`、Sonatype 凭据等属于敏感信息，应通过环境变量或加密的 Gradle properties 注入，严禁提交到仓库。推荐在本地使用 `gpg --list-secret-keys` 验证密钥是否可用。
- 访问 WordPress 站点时默认使用 HTTPS，避免在日志中记录明文密码或完整请求体；如需调试，请将调试日志写入本地文件并在会话结束后安全删除。
