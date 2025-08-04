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
    ).forEach { target ->
        target.binaries {
            executable {
                entryPoint = "com.backend.main"
                println(target.name)
                linkerOpts =
                    if (target.name == "linuxX64")
                        mutableListOf("-Wl,--as-needed") //Drops libcrypt.so.1 which isn't needed
                    else mutableListOf()
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":ktclient"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        jvmMain {
            dependencies {
                implementation(project(":ktclient"))
                implementation(libs.awsLambdaEvents)
                implementation(libs.awsLambdaCore)
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
                implementation(libs.trueangleLambdaRuntime)
                implementation(libs.trueangleLambdaEvents)
                implementation(libs.kotlin.coroutines.test)
            }
        }
        sourceSets.all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }
    }
}

tasks.named("build") {
    dependsOn(":model:openApiGenerate")
    dependsOn(":ktclient:build")
}

tasks.named("generateProjectStructureMetadata") {
    dependsOn(":model:openApiGenerate")
    dependsOn(":ktclient:build")
}

tasks.withType<Wrapper> {
    distributionType = Wrapper.DistributionType.BIN
}
