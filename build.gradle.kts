extra["smithyVersion"] = "1.54.0"
extra["yarnVersion"] = "1.22.19"
extra["nodeVersion"] = "22.11.0"
extra["smithyTypeScriptVersion"] = "0.26.0"

plugins {
    val kotlinVersion = "2.1.10"
    val nodePluginVersion = "7.1.0"

    kotlin("jvm") version kotlinVersion apply false
    kotlin("kapt") version kotlinVersion apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinxSerialization) apply false

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
tasks.register("deploy-jvm") {
    dependsOn("build")
    dependsOn("backend:jvmShadowJar")
    dependsOn("cdk:deploy-jvm")
}
tasks.register("deploy-native") {
    dependsOn("build")
    dependsOn("backend:buildLambdaRelease")
    dependsOn("cdk:deploy-native")
}