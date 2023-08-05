plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.amazonaws:aws-lambda-java-events:3.11.2")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.2")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
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
