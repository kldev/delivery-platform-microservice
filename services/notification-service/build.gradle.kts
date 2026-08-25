plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId = project.property("quarkusPlatformGroupId")
val quarkusPlatformArtifactId = project.property("quarkusPlatformArtifactId")
val quarkusPlatformVersion = project.property("quarkusPlatformVersion")

dependencies {
    implementation(project(":libraries:common"))
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-reactive-pg-client")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-flyway")
    implementation("io.quarkus:quarkus-smallrye-health")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-messaging-kafka")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.quarkus:quarkus-rest-client")
    implementation("io.quarkus:quarkus-rest-client-jackson")


    testImplementation("io.quarkus:quarkus-junit")
    testImplementation("io.rest-assured:rest-assured")

    testImplementation("org.testcontainers:testcontainers:2.0.5")
    testImplementation("org.testcontainers:postgresql:1.21.4")
    testImplementation("org.wiremock:wiremock:3.13.1")
}

group = "com.pl.platform.svc"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25
        javaParameters = true
    }
}
