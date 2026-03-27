plugins {
    id("java-library")
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")

    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.register<JavaExec>("generateBotApiSubset") {
    group = "code generation"
    description = "Generate subset DTO/request scaffolding from botapi/subset-schema.json"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("ru.tardyon.botframework.telegram.generator.BotApiCodegenMain")
    args("--output=${layout.buildDirectory.dir("generated/sources/botapi/java").get().asFile.absolutePath}")
}
