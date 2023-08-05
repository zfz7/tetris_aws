plugins {
    id("software.amazon.smithy").version("0.7.0")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("software.amazon.smithy:smithy-aws-traits:1.35.0")
    implementation("software.amazon.smithy:smithy-aws-apigateway-traits:1.35.0")
}

buildscript {
    dependencies {
        classpath("software.amazon.smithy:smithy-openapi:1.35.0")
        classpath("software.amazon.smithy:smithy-model:1.35.0")
        classpath("software.amazon.smithy:smithy-aws-traits:1.35.0")
        classpath("software.amazon.smithy:smithy-aws-apigateway-openapi:1.35.0")
    }
}

java.sourceSets["main"].java {
    srcDirs("model", "src/main/smithy")
}
