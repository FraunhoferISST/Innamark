/*
 * Copyright (c) 2023-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig


plugins {
    kotlin("plugin.serialization") version "2.3.21"
    kotlin("multiplatform") version "2.3.21"
    id("io.kvision") version "9.7.0"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    id("com.github.node-gradle.node") version "7.1.0"
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

version = "0.1.0-SNAPSHOT"
group = "de.fraunhofer.isst.innamark"

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    js {
        browser {
            useEsModules()
            commonWebpackConfig {
                outputFileName = "main.bundle.js"
            }
            runTask {
                mainOutputFileName = "main.bundle.js"
                devServerProperty =
                    KotlinWebpackConfig.DevServer(
                        open = false,
                        port = 3000,
                        static =
                            mutableListOf(
                                "${layout.buildDirectory.asFile.get()}/processedResources/js/main",
                            ),
                    )
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
        compilerOptions {
            target.set("es2015")
        }
    }

    sourceSets["jsMain"].dependencies {
        implementation("io.kvision:kvision:9.7.0")
        implementation("io.kvision:kvision-bootstrap:9.7.0")
        implementation("io.kvision:kvision-bootstrap-upload:9.7.0")
        implementation("io.kvision:kvision-fontawesome:9.7.0")
        implementation("io.kvision:kvision-state:9.7.0")
        implementation("io.kvision:kvision-state-flow:9.7.0")
        implementation("io.kvision:kvision-toastify:9.7.0")
        implementation("de.fraunhofer.isst.innamark:watermarker:0.1.0-SNAPSHOT")
    }
    sourceSets["jsTest"].dependencies {
        implementation(kotlin("test-js"))
        implementation("io.kvision:kvision-testutils:9.7.0")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.register("test") {
    description = "run all tests"
    dependsOn("jsTest")
}
