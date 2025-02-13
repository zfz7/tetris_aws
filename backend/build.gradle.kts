plugins {
    id("org.jetbrains.kotlin.jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.amazonaws:aws-lambda-java-events:3.14.0")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.3")

    //kotlinx-serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    implementation(project(":ktclient"))

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.3")
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("uk.org.webcompere:system-stubs-jupiter:2.1.7")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("none")//Need to generate jar
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.named("build") {
    dependsOn(":model:build")
}
