plugins {
//    kotlin("jvm") version "2.1.0"
    kotlin("jvm") version "1.9.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
//    jvmToolchain(23)
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}