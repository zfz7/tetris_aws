import com.github.gradle.node.NodeExtension
import com.github.gradle.node.yarn.task.YarnTask

plugins {
    id("com.github.node-gradle.node")
}

configure<NodeExtension> {
    version.set(rootProject.extra["nodeVersion"] as String)
    yarnVersion.set(rootProject.extra["yarnVersion"] as String)
    download.set(true)
}

val install = tasks.register<YarnTask>("install") {
    args.set(listOf("install"))
}

tasks.register<YarnTask>("test") {
    environment.set(mapOf("CI" to "true"))
    dependsOn(install)
    args.set(listOf("test"))
}

tasks.register<YarnTask>("build") {
    dependsOn(install)
    dependsOn(":tsclient:build")
    mustRunAfter("test")
    args.set(listOf("build"))
}


task<Delete>("clean") {
    delete(project(":frontend").buildDir)
}