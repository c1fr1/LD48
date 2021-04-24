import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

dependencies {
    implementation(files("lib/Enignets.jar"))
}

plugins {
    kotlin("jvm") version "1.4.31"
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "c1fr1"
version = "0.0.1"

repositories {
    mavenCentral()
    jcenter()
}

application {
    mainClass.set("MainKt")
    mainClassName = "MainKt"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.useIR = true
}