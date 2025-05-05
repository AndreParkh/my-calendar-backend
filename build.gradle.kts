plugins {
    kotlin("jvm") version "2.1.20"
    jacoco
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
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

tasks.jar.configure {
    manifest {
        attributes(mapOf("Main-Class" to "io.github.andreparkh.MainKt"))
    }
}