plugins {
    id("java")
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":telegram-bot-framework-spring-boot-starter"))
    implementation("org.springframework.boot:spring-boot-starter:3.3.5")

    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
