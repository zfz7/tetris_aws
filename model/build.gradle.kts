plugins {
    kotlin("jvm")
    id("software.amazon.smithy.gradle.smithy-jar").version("1.1.0")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("software.amazon.smithy.typescript:smithy-typescript-codegen:${rootProject.extra["smithyTypeScriptVersion"]}")
    implementation("software.amazon.smithy.kotlin:smithy-kotlin-codegen:${rootProject.extra["smithyKotlinCodegenVersion"]}")
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

java.sourceSets["main"].java {
    srcDirs("model", "src/main/smithy")
}
