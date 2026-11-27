---
title: Installation
---

<!--
SPDX-FileCopyrightText: 2024-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e. V.

SPDX-License-Identifier: LicenseRef-Fraunhofer-Gesellschaft
-->

# Installation

## System Prerequisites

The following things are needed to run this application:

- For manual builds:
    - A Java Runtime Environment (JRE)
    - The watermarker library, published in maven local(*)
- For containerized builds:
    - docker & docker-compose

(*) To publish the watermarker library to your maven local repository (if not already done), execute
the following commands from the root directory of the project:

1. `cd watermarker`
2. `./gradlew publishToMavenLocal`

:::info
The source code comes with enabled source maps to allow frontend debugging. They should be disabled
in production to prevent the exposure of source code. To disable it, add `sourceMaps = false` inside
the `build.gradle.kts`.
:::

## Building from Source (Manual Build)

Use Gradle to manually build and run the webinterface:

1. `cd webinterface` (if not already there)
2. `./gradlew -t run`
    - In case of a production build, `./gradlew clean zip` should be used instead
3. Visit http://localhost:3000

## Containerized Build

Run 
the [`docker-compose.yml`](https://github.com/FraunhoferISST/Innamark/blob/main/docker-compose.yml)
file in the root directory of the project:

```
docker-compose up
```

After the startup finished, try to visit the webinterface at http://localhost:8080
