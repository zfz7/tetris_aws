import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.shadowJar)
    id("io.github.trueangle.plugin.lambda") version "0.0.1"
}

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        withJava()
        tasks.register<ShadowJar>("jvmShadowJar") { // create fat jar task
            val mainCompilation = compilations["main"]
            val jvmRuntimeConfiguration = mainCompilation
                .runtimeDependencyConfigurationName
                .let { project.configurations[it] }

            from(mainCompilation.output.allOutputs)
            configurations = listOf(jvmRuntimeConfiguration)
            archiveClassifier.set("all")
            manifest.attributes("Main-Class" to "none")
        }
    }

    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64()
        hostOs == "Mac OS X" && !isArm64 -> macosX64()
        hostOs == "Linux" && isArm64 -> linuxArm64()
        hostOs == "Linux" && !isArm64 -> linuxX64()
        isMingwX64 -> mingwX64()
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    setOf(
        nativeTarget,
        linuxX64(),
    ).forEach {
        it.binaries {
            executable {
                entryPoint = "com.backend.main"
            }
        }
    }

    sourceSets {
        configurations.all {
            resolutionStrategy {
                force("io.ktor:ktor-client-curl:3.1.0") // Check if io.github.trueangle:lambda-runtime:0.0.5 is ready
            }
        }
        jvmMain {
            dependencies {
                implementation(project(":ktclient"))
                implementation("com.amazonaws:aws-lambda-java-events:3.14.0")
                implementation("com.amazonaws:aws-lambda-java-core:1.2.3")
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        nativeMain {
            dependencies {
                implementation(project(":ktclient"))
                implementation(libs.kotlin.serialization.json)
                implementation("io.github.trueangle:lambda-runtime:0.0.4")
                implementation("io.github.trueangle:lambda-events:0.0.4")
                implementation(libs.kotlin.coroutines.test)
            }
        }
    }
}

tasks.named("build") {
    dependsOn(":model:openApiGenerate")
    dependsOn(":ktclient:build")
}

tasks.withType<Wrapper> {
    gradleVersion = "8.12"
    distributionType = Wrapper.DistributionType.BIN
}
