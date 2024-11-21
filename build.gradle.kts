extra["smithyVersion"] = "1.52.1"
extra["yarnVersion"] = "1.22.19"
extra["nodeVersion"] = "22.11.0"
extra["smithyKotlinVersion"] = "1.3.24"
extra["smithyKotlinCodegenVersion"] = "0.33.23"
extra["smithyTypeScriptVersion"] = "0.25.0"

plugins {
    val kotlinVersion = "2.0.21"
    val nodePluginVersion = "7.1.0"

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