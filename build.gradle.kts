plugins {
    val kotlinVersion = "1.9.0"
    val nodePluginVersion = "5.0.0"

    kotlin("jvm") version kotlinVersion apply false
    kotlin("kapt") version kotlinVersion apply false

    id("com.github.node-gradle.node") version nodePluginVersion apply false
}

group = "com.daniel-eichman"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.register("build") {
    dependsOn("model:build")
    dependsOn("cdk:build")
    dependsOn("backend:build")
    dependsOn("frontend:build")
}