---
title: Installation
---

<!--
SPDX-FileCopyrightText: 2024-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e. V.

SPDX-License-Identifier: LicenseRef-Fraunhofer-Gesellschaft
-->

import PatentHint from './../patent-hint.mdx'

<PatentHint components={props.components} />

# Installation

## System Requirements

**Java** is required to build the library with [Gradle](https://gradle.org/).

## Building from Source
1. Clone the Innamark repository
2. Go into the directory of the watermarker: `cd Innamark/watermarker`
3. Build the library: `./gradlew build`
4. Publish the artifacts locally: `./gradlew publishToMavenLocal`
