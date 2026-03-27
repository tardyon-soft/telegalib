plugins {
    id("java-library")
}

dependencies {
    api(project(":telegram-bot-framework-core"))

    implementation("org.springframework.boot:spring-boot-starter:3.3.5")
    implementation("org.springframework.boot:spring-boot-autoconfigure:3.3.5")
    implementation("org.springframework:spring-web:6.1.13")

    compileOnly("org.springframework.boot:spring-boot-configuration-processor:3.3.5")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.3.5")

    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.5")
    testImplementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
