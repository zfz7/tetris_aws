plugins {
    kotlin("jvm")
    id("software.amazon.smithy.gradle.smithy-jar").version("1.1.0")
    id("org.openapi.generator") version "7.11.0"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("software.amazon.smithy.typescript:smithy-typescript-codegen:${rootProject.extra["smithyTypeScriptVersion"]}")
}

smithy {
    smithyBuildConfigs = files("smithy-build.json")
    dependencies {
        implementation("software.amazon.smithy.typescript:smithy-aws-typescript-codegen:${rootProject.extra["smithyTypeScriptVersion"]}")
        implementation("software.amazon.smithy:smithy-openapi:${rootProject.extra["smithyVersion"]}")
        implementation("software.amazon.smithy:smithy-model:${rootProject.extra["smithyVersion"]}")
        implementation("software.amazon.smithy:smithy-aws-traits:${rootProject.extra["smithyVersion"]}")
        implementation("software.amazon.smithy:smithy-aws-apigateway-openapi:${rootProject.extra["smithyVersion"]}")
        implementation("software.amazon.smithy:smithy-cli:${rootProject.extra["smithyVersion"]}")
    }
}

openApiGenerate {
    generatorName.set("kotlin")
    packageName.set("com.tetris.model")
    inputSpec.set("${project(":model").projectDir}/build/smithyprojections/model/source/openapi/Tetris.openapi.json") // Path to your OpenAPI file
    outputDir.set("${project(":ktclient").projectDir}")
    typeMappings.put("string+date-time", "Instant")
    importMappings.putAll(
        mapOf(
            "Instant" to "kotlinx.datetime.Instant",
            "File" to "OctetByteArray",
        )
    )
    configOptions.set(mapOf(
        "artifactId" to "ktclient",
        "omitGradleWrapper" to "true"
    ))
    additionalProperties.set(
        mapOf(
            "library" to "multiplatform",
            "dateLibrary" to "kotlinx-datetime",
            "enumPropertyNaming" to "UPPERCASE",
            "useCoroutines" to "true",
        )
    )
}

tasks.named("openApiGenerate") {
    dependsOn(":model:build")
}

java.sourceSets["main"].java {
    srcDirs("model", "src/main/smithy")
}
