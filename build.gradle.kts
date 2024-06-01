extra["smithyVersion"] = "1.49.0"
extra["yarnVersion"] = "1.22.19"
extra["nodeVersion"] = "20.14.0"
extra["smithyKotlinVersion"] = "1.2.5"
extra["smithyKotlinCodegenVersion"] = "0.32.5"
extra["smithyTypeScriptVersion"] = "0.21.0"

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
    dependsOn("tsclient:build")
    dependsOn("ktclient:build")
    dependsOn("backend:build")
    dependsOn("frontend:build")
}
tasks.register("clean") {
    dependsOn("model:clean")
    dependsOn("cdk:clean")
    dependsOn("tsclient:clean")
    dependsOn("ktclient:clean")
    dependsOn("ktclient:cleanSrc")
    dependsOn("backend:clean")
    dependsOn("frontend:clean")
}
tasks.register("deploy") {
    dependsOn("build")
    dependsOn("cdk:deploy")
}