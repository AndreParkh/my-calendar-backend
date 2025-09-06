plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "1.9.25"            // TODO: уточинить последнии версии
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
    jacoco
}

group = "io.github"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot dependencies
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // PostgreSQL driver for production
    runtimeOnly("org.postgresql:postgresql")

    // env
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")

    // Testing dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(kotlin("test"))

    // H2 database for testing
    testImplementation("com.h2database:h2")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // SpringDoc OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")

}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
kotlin {
    jvmToolchain(23)
}

//tasks.jar.configure {
//    manifest {
//        attributes(mapOf("Main-Class" to "io.github.andreparkh.MainKt"))
//    }
//}