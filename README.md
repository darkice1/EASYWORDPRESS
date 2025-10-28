# EASYWORDPRESS

_A high-performance Kotlin SDK that streamlines WordPress REST API integrations with caching-first helpers._

English | [简体中文](README.zh-CN.md)

## Overview
EASYWORDPRESS wraps the Afrozaar WordPress v2 client with Kotlin-first utilities so you can manage tags, categories, and media uploads using concise, type-safe APIs. Smart caching keeps repeated calls fast while duplicate media detection avoids unnecessary uploads.

## Features
- **Caffeine-backed caching** for tag and category lookups, refreshing automatically after one hour.
- **Automatic term management** that creates missing tags or categories transparently.
- **Content-aware media uploads** that calculate SHA-256 hashes to skip files already stored remotely.
- **Kotlin/JVM friendly design** with Java 21 toolchain support and Gradle publishing tasks for OSSRH.

## Installation
Add the dependency from Maven Central (or your local Maven cache when developing):

```kotlin
dependencies {
    implementation("com.github.darkice1:easy-wordpress:0.0.3")
}
```

## Configuration
Create a `config.properties` in the project root (never commit real credentials):

```properties
WPBASEURL=https://your-wordpress-site.example
WPUSERNAME=your-username
WPUSERPASSWD=your-application-password
```

## Usage
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

`uploadFile` accepts local paths or HTTP(S) URLs, reusing existing media when filenames or file content match.

## Build & Test
- `./gradlew clean build` &mdash; compile against Kotlin 2.1, run tests, and assemble the JAR.
- `./gradlew test` &mdash; execute the `kotlin.test` suite; run after changing caching or REST logic.
- `./gradlew publiclocal` &mdash; publish artifacts to your local Maven repository for downstream testing.
- `./gradlew publishAndCloseSonatype` &mdash; stage a release on OSSRH; requires `centralUsername` and `centralPassword` Gradle properties.

## Contributing
Please follow the coding guidelines in `AGENTS.md`, keep English and Chinese documentation in sync, and include test evidence (for example `./gradlew test`) in pull requests. Whenever features change, update both `README.md` and `README.zh-CN.md`, and append a reverse-chronological entry to `CHANGELOG.md`. Contributions are welcome!

## License
This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
