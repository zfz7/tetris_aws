plugins {
    kotlin("jvm")
    alias(libs.plugins.smithyJar)
    alias(libs.plugins.openapiGenerator)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.smithyTypescriptCodegen)
}

smithy {
    smithyBuildConfigs = files("smithy-build.json")
    dependencies {
        implementation(libs.smithyAwsTypescriptCodegen)
        implementation(libs.smithyOpenapi)
        implementation(libs.smithyModel)
        implementation(libs.smithyAwsTraits)
        implementation(libs.smithyAwsApigatewayOpenapi)
        implementation(libs.smithyCli)
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
            "Instant" to "kotlin.time.Instant",
            "File" to "OctetByteArray",
        )
    )
    configOptions.set(
        mapOf(
            "artifactId" to "ktclient",
            "omitGradleWrapper" to "true"
        )
    )
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
