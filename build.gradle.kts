import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension

plugins {
    id("org.springframework.boot") version "3.3.5" apply false
}

val releaseVersion = providers.environmentVariable("RELEASE_VERSION").orElse("0.1.0-SNAPSHOT").get()

allprojects {
    group = "ru.tardyon.botframework"
    version = releaseVersion

    repositories {
        mavenCentral()
    }
}

subprojects {
    plugins.withId("java") {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }
}

val publishableModules = setOf(
    "telegram-bot-framework-core",
    "telegram-bot-framework-spring-boot-starter"
)

configure(subprojects.filter { it.name in publishableModules }) {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    plugins.withId("java") {
        extensions.configure<JavaPluginExtension> {
            withSourcesJar()
            withJavadocJar()
        }
    }

    extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])

                pom {
                    name.set(project.name)
                    description.set("Telegram Bot Framework module: ${project.name}")
                    url.set("https://github.com/tardyon-soft/telegalib")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }

                    developers {
                        developer {
                            id.set("tardyon")
                            name.set("Sergej Gerasimov")
                            email.set("tardyon@yandex.ru")
                        }
                    }

                    scm {
                        connection.set("scm:git:https://github.com/tardyon-soft/telegalib.git")
                        developerConnection.set("scm:git:ssh://git@github.com:tardyon-soft/telegalib.git")
                        url.set("https://github.com/tardyon-soft/telegalib")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "sonatype"
                val isSnapshot = version.toString().endsWith("SNAPSHOT")
                url = uri(
                    if (isSnapshot) {
                        "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                    } else {
                        "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                    }
                )
                credentials {
                    username = providers.environmentVariable("OSSRH_USERNAME").orNull
                    password = providers.environmentVariable("OSSRH_PASSWORD").orNull
                }
            }
        }
    }

    extensions.configure<SigningExtension> {
        val signingKey = providers.environmentVariable("SIGNING_KEY").orNull
        val signingPassword = providers.environmentVariable("SIGNING_PASSWORD").orNull
        if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(extensions.getByType(PublishingExtension::class.java).publications)
        }
    }
}
