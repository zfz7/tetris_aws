import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.shadowJar)
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

    sourceSets {
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
    }
}

tasks.named("build") {
    dependsOn(":model:openApiGenerate")
    dependsOn(":ktclient:build")
}
