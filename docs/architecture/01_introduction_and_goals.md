---
title: Introduction and Goals
sidebar_position: 1
---

<!--
 Copyright (c) 2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.

 This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 that can be found in the LICENSE file.
-->

# Introduction and Goals

## What is Innamark?

The **Invisible Watermarking (Innamark)** project offers steganography/watermarking solutions to 
hide data invisibly and robustly within text assets (such as documents, emails, chat messages, etc.).

Innamark offers different products:
- A **Kotlin multiplatform library** that can easily be built and integrated into Kotlin, Java,
  and JavaScript projects, offering watermark embedding and extraction functionalities
- A **webinterface** as a GUI component to embed and extract watermarks
- A **command line interface (CLI)** as a terminal solution for watermark embedding and extraction
- Various closed-source components (e.g., **REST APIs**)

The core Innamark watermarking algorithm is developed by German researchers from the [Fraunhofer
Institute for Software and Systems Engineering](https://www.isst.fraunhofer.de/en.html),
published as a [scientific journal paper](https://doi.org/10.1109/ACCESS.2025.3583591), and
applied for a [German](https://patents.google.com/patent/DE102023125012A1) and [international
patent](https://patents.google.com/patent/WO2025056772A1/en).


## Quality Goals

The key quality goals and characteristics of the Innamark watermarking engine are:

| Quality Goal          | Description                                                                                                                                                  |
|-----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Inivsible**         | A human should not be able to see a difference between the original text without watermark and the watermarked text.                                         |
| **Robust**            | The watermark should stay intact as long as possible (e.g., when copied between different applications or performing modifications on the watermarked text). |
| **Length-preserving** | The number of characters should stay the same (no length increase after the watermark operation is performed).                                               |
