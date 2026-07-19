@file:Suppress("VulnerableLibrariesLocal")


plugins {
	kotlin("jvm") version "2.4.10"
	`java-library`
	id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
	`maven-publish`
	signing
}

group = "com.github.darkice1"
version = "0.0.4"

val projectName = "easy-wordpress"
val projectDesc = "Neo easy wordpress SDK."
val repoName = projectName

// -------- Java toolchain & JAR 附件 --------
java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(25))
	}
	withSourcesJar()
	withJavadocJar()
}

repositories {
	mavenCentral()
	mavenLocal()
}

dependencies {
	api("com.afrozaar.wordpress:wp-api-v2-client-java:4.8.3")
	api("org.json:json:20250107")

	api("com.github.ben-manes.caffeine:caffeine:3.2.0")

	api(kotlin("stdlib"))
	testImplementation(kotlin("test"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
	compilerOptions {
		jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
	}
}

// TestWordpress.kt 是可运行的集成示例，并非自动发现的测试类。
tasks.withType<AbstractTestTask>().configureEach {
	failOnNoDiscoveredTests = false
}

tasks.withType<Javadoc>().configureEach {
	(options as StandardJavadocDocletOptions).apply {
		encoding = "UTF-8"
		docEncoding = "UTF-8"
		addStringOption("Xdoclint:none", "-quiet")
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
			artifactId = projectName

			pom {
				name.set(projectName)
				description.set(projectDesc)
				url.set("https://github.com/darkice1/$repoName")

				licenses {
					license {
						name.set("MIT License")
						url.set("https://opensource.org/licenses/MIT")
					}
				}
				developers {
					developer {
						id.set("neo")
						name.set("neo")
						email.set("starneo@gmail.com")
					}
				}
				scm {
					connection.set("scm:git:https://github.com/darkice1/$repoName.git")
					url.set("https://github.com/darkice1/$repoName")
				}
			}
		}
	}
}

val coords = "${project.group}:$projectName:$version"
tasks.register("publishAndCloseSonatype") {
	group = "mypublishing"
	description =
		"Publish artifacts to Sonatype OSSRH, then close the staging repository."
	dependsOn("publishToSonatype", "closeSonatypeStagingRepository")
//	finalizedBy("closeSonatypeStagingRepository")// 上传完成后再执行 close
	doLast {
		println("close:[$coords]")
	}
}

tasks.register("publiclocal") {
	group = "mypublishing"
	description = "Close & release Sonatype staging repo, then print coordinates."
	dependsOn("publishMavenJavaPublicationToMavenLocal")
	doLast {
		println("public local:[$coords]")
	}
}

tasks.register("release") {
	group = "mypublishing"
	description = "Close & release Sonatype staging repo, then print coordinates."
	dependsOn("publishToSonatype", "closeAndReleaseSonatypeStagingRepository")
	doLast {
		println("release:[$coords]")
	}
}

nexusPublishing {
	repositories {
		sonatype {
			nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
			snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
			username.set(providers.gradleProperty("centralUsername"))
			password.set(providers.gradleProperty("centralPassword"))
		}
//		repositoryDescription = "$group:$projectName:$version"
//		description = "$group:$projectName:$version"
	}
}

signing {
	// 1) CI 推荐：gradle.properties / 环境变量注入 ASCII 私钥
	val inMemKey: String? = providers.gradleProperty("signingKey").orNull
	val inMemPwd: String? = providers.gradleProperty("signingPassword").orNull

	when {
		inMemKey != null && inMemPwd != null -> useInMemoryPgpKeys(inMemKey, inMemPwd)
		else -> useGpgCmd()
	}

	sign(publishing.publications["mavenJava"])
}
