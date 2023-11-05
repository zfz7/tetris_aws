plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("aws.smithy.kotlin:smithy-client:${rootProject.extra["smithyKotlinVersion"]}")
}

tasks.register<Copy>("copy_kt_client_from_model") {
    dependsOn(":model:build")
    from("${project(":model").projectDir}/build/smithyprojections/model/source/kotlin-codegen/src")
    //These files cause build errors
    exclude("**/*Client.kt")
    exclude("**/*ApiError.kt")
    into("$projectDir/src")
}

tasks.named("compileKotlin") {
    dependsOn("copy_kt_client_from_model")
}

tasks.register<Delete>("cleanSrc") {
    delete(file("$projectDir/src/main"))
    delete(file("$projectDir/build"))
}
