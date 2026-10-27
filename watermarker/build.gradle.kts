/*
 * Copyright (c) 2023-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("multiplatform") version "2.3.21"
    id("maven-publish")
    id("org.jetbrains.dokka") version "2.2.0"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.8"
}

group = "de.fraunhofer.isst.innamark"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.js.ExperimentalJsExport")
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    jvmToolchain(21)
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        browser { }
        binaries.executable()
    }

    sourceSets {
        val commonMain =
            sourceSets.getByName("commonMain") {
                dependencies {
                    implementation("org.kotlincrypto.hash:sha3:0.8.0")
                }
            }
        val commonTest =
            sourceSets.getByName("commonTest") {
                dependencies {
                    implementation(kotlin("test"))
                }
            }
        val jvmMain = sourceSets.getByName("jvmMain")
        val jvmTest =
            sourceSets.getByName("jvmTest") {
                dependencies {
                    implementation(kotlin("reflect"))
                }
            }
        val jsMain =
            sourceSets.getByName("jsMain") {
                dependencies {
                    implementation(npm("pako", "2.1.0"))
                }
            }
        val jsTest = sourceSets.getByName("jsTest")
    }
}

tasks.withType<Test> {
    testLogging {
        showStandardStreams = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(false)
    filter {
        exclude("**/generated/**")
    }
}
