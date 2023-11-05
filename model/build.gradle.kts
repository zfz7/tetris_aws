plugins {
    id("software.amazon.smithy").version("0.7.0")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("software.amazon.smithy:smithy-aws-traits:${rootProject.extra["smithyVersion"]}")
    implementation("software.amazon.smithy:smithy-aws-apigateway-traits:${rootProject.extra["smithyVersion"]}")
    implementation("software.amazon.smithy.typescript:smithy-typescript-codegen:${rootProject.extra["smithyTypeScriptVersion"]}")
    implementation("software.amazon.smithy.kotlin:smithy-kotlin-codegen:${rootProject.extra["smithyKotlinVersion"]}")
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("software.amazon.smithy:smithy-openapi:${rootProject.extra["smithyVersion"]}")
        classpath("software.amazon.smithy:smithy-model:${rootProject.extra["smithyVersion"]}")
        classpath("software.amazon.smithy:smithy-aws-traits:${rootProject.extra["smithyVersion"]}")
        classpath("software.amazon.smithy:smithy-aws-apigateway-openapi:${rootProject.extra["smithyVersion"]}")
        classpath("software.amazon.smithy:smithy-cli:${rootProject.extra["smithyVersion"]}")
    }
}

java.sourceSets["main"].java {
    srcDirs("model", "src/main/smithy")
}
