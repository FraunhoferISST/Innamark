---
title: Building Block View
sidebar_position: 5
---

<!--
 Copyright (c) 2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.

 This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 that can be found in the LICENSE file.
-->

# Building Block View

## Level 1: Innamark (Whitebox)

Innamark is not one single component or application. It is a full ecosystem of various building blocks,
offering different functionalities.

![Level 1: Innamark](images/05_level-1.svg)

### Contained Blackboxes

| Building Block                                                   | Description                                                                                         |
|------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|
| [**Watermarker Library**](#level-2-watermarker-library-whitebox) | A Kotlin multiplatform libray, offering watermark embedding and extraction functionalities.         |
| [**CLI**](#level-2-cli-whitebox)                                 | A command line interface (CLI) as a terminal solution for watermark embedding and extraction.       |
| [**Web Interface**](#level-2-web-interface-whitebox)             | A graphical user interface (GUI) to embed and extract watermarks.                                   |
| [**REST API**](#level-2-rest-api-blackbox)                       | A closed-source implementation of an API, offering watermarking embedding and extraction endpoints. |


## Level 2: CLI (Whitebox)

![Level 2: CLI](images/05_level-2-cli.svg)

### Purpose / Responsibility

The command line interface (CLI) is based on 
the [Kotlin CLI parser kotlinx-cli](https://github.com/Kotlin/kotlinx-cli), aiming to bring 
watermarking functionalities into the terminal. It is able to embed and extract watermarks from 
Strings directly in the terminal and files. The CLI tool is easy to extand with custom 
commands and arguments. Under the hood, it uses 
the [Innamark watermark library](#level-2-watermarker-library) with the Java build target as 
Gradle dependency.

### Contained Blackboxes

| Building Block                                                                               | Description                                                                                                                                                     |
|----------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [**Main**](https://github.com/FraunhoferISST/Innamark/blob/main/cli/src/main/kotlin/Main.kt) | The Main file contains the full logic of the CLI tool, like parsing CLI arguments and executing them.                                                           |
| [**Watermarker Library**](#level-2-watermarker-library-whitebox)                             | A Kotlin multiplatform libray, offering watermark embedding and extraction functionalities, integrated as external Gradle dependency with the JVM build target. |
| [**kotlinx-cli**]((https://github.com/Kotlin/kotlinx-cli))                                   | A generic command-line parser for Kotlin, integrated as external Gradle dependency.                                                                             |


## Level 2: Web Interface (Whitebox)

![Level 2: Web Interface](images/05_level-2-webinterface.svg)

### Purpose / Responsibility

The Web Interface is based on the [KVision web framework for Kotlin/JS](https://kvision.io/), 
aiming to create a GUI running in the browser to easily embed and extract watermarks. It 
consists of a single-page design with two tabs: One for watermark embeeding and one for 
watermark extraction. It is only able to embed text-watermarks into existing cover texts and 
offers configuration possibilities for different watermark types (e.g., compress the watermark, 
add a hash to verify the watermark, etc.). Under the hood, it uses the [Innamark watermark 
library](#level-2-watermarker-library) with the JavaScript (JS) build target as
Gradle dependency and can be seen as a 
frontend for it.

### Contained Blackboxes

| Building Block                                                                                                                    | Description                                                                                                                                                    |
|-----------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [**App**](https://github.com/FraunhoferISST/Innamark/blob/main/webinterface/src/jsMain/kotlin/App.kt)                             | The main container component of the UI, creating the overall page with a tab panel, including the two tabs for embedding and extraction.                       |
| [**Embed Tab**](https://github.com/FraunhoferISST/Innamark/blob/main/webinterface/src/jsMain/kotlin/WatermarkTextEmbedTab.kt)     | The embed tab offers a form for adding watermarks in cover text by using the the watermarker library.                                                          |
| [**Extract Tab**](https://github.com/FraunhoferISST/Innamark/blob/main/webinterface/src/jsMain/kotlin/WatermarkTextExtractTab.kt) | The extract tab offers a form for getting watermarks in a text by using the the watermarker library.                                                           |
| [**Watermarker Library**](#level-2-watermarker-library-whitebox)                                                                  | A Kotlin multiplatform libray, offering watermark embedding and extraction functionalities, integrated as external Gradle dependency with the JS build target. |


## Level 2: REST API (Blackbox)

### Purpose / Responsibility

The REST API is a closed source implementation, offering various API endpoints to watermark existing
text and files and thus not further described in this documentation.


## Level 2: Watermarker Library (Whitebox)

![Level 2: Watermarker Library](images/05_level-2-watermarker.svg)

### Purpose / Responsibility

The watermarker library is the hearth of Innamark, containing the main watermark logic and
functionalities. It is written as a [Kotlin multiplatform](https://kotlinlang.org/multiplatform/)
library and thus able to be built for Java (JVM) and JavaScript (JS) applications to integrate
watermarking into backend and frontend applications. The library is highly flexible and
extensible and comes with various utilities, interfaces, and builders. It aims to increase the
developer experience to easily integrate Innamarks watermarking mechanisms into other applications.

### Contained Blackboxes

| Building Block | Description |
|----------------|-------------|
|                |             |
