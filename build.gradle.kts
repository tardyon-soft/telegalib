import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

plugins {
    base
    id("org.springframework.boot") version "3.3.5" apply false
    id("org.jreleaser") version "1.23.0"
}

allprojects {
    group = "ru.tardyon.botframework"
    version = providers.environmentVariable("RELEASE_VERSION").orElse("0.1.0-SNAPSHOT").get()

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
            withSourcesJar()
            withJavadocJar()
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }

    val publishableModules = setOf(
        "telegram-bot-framework-core",
        "telegram-bot-framework-spring-boot-starter"
    )

    if (name in publishableModules) {
        apply(plugin = "maven-publish")

        plugins.withId("java") {
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
                        name = "staging"
                        url = uri(rootProject.layout.buildDirectory.dir("staging-deploy"))
                    }
                }
            }
        }
    }
}

jreleaser {
    signing {
        pgp {
            active = org.jreleaser.model.Active.ALWAYS
            armored = true
        }
    }

    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    active = org.jreleaser.model.Active.RELEASE
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")
                }
            }
        }
    }
}
