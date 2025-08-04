import org.jetbrains.kotlin.gradle.plugin.mpp.MetadataDependencyTransformationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

plugins {
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.kotlinMultiplatform)
}

repositories {
    mavenCentral()
}

kotlin {
    macosArm64()
    macosX64()
    linuxArm64()
    linuxX64()
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.coroutines.core)
                implementation(libs.kotlin.serialization.core)

                api(libs.ktor.client.core)
                api(libs.ktor.client.serialization)
                api(libs.ktor.client.content.negotiation)
                api(libs.ktor.serialization.kotlinx.json)

                api(libs.kotlin.date.time)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.ktor.client.mock)
            }
        }

        jvmMain {
            dependencies {
                implementation(kotlin("stdlib-jdk7"))
                implementation(libs.ktor.client.cio.jvm)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        all {
            languageSettings.apply {
                optIn("kotlin.time.ExperimentalTime")
            }
        }
    }
}

tasks.withType(KotlinCompile::class.java).configureEach {
    dependsOn(":model:openApiGenerate")
}
tasks.withType(Jar::class.java).configureEach {
    dependsOn(":model:openApiGenerate")
}
tasks.withType(KotlinNativeCompile::class.java).configureEach {
    dependsOn(":model:openApiGenerate")
}
tasks.withType(MetadataDependencyTransformationTask::class.java).configureEach {
    dependsOn(":model:openApiGenerate")
}

tasks {
    register("test") {
        dependsOn("allTests")
    }
}

task<Delete>("cleanSrc") {
    delete(file("$projectDir/build"))
    delete(file("$projectDir/src"))
    delete(file("$projectDir/docs"))
    delete(file("$projectDir/.openapi-generator"))
    delete(fileTree("$projectDir") {
        exclude("build.gradle.kts", "settings.gradle.kts", ".gitignore", ".openapi-generator-ignore")
    })
}