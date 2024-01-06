plugins {
    kotlin("jvm")
    id("software.amazon.smithy.gradle.smithy-jar").version("0.9.0")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("software.amazon.smithy:smithy-aws-traits:${rootProject.extra["smithyVersion"]}")
    implementation("software.amazon.smithy:smithy-aws-apigateway-traits:${rootProject.extra["smithyVersion"]}")
    implementation("software.amazon.smithy:smithy-openapi:${rootProject.extra["smithyVersion"]}")
    implementation("software.amazon.smithy.typescript:smithy-typescript-codegen:${rootProject.extra["smithyTypeScriptVersion"]}")
    implementation("software.amazon.smithy.kotlin:smithy-kotlin-codegen:${rootProject.extra["smithyKotlinCodegenVersion"]}")
}

java.sourceSets["main"].java {
    srcDirs("model", "src/main/smithy")
}
