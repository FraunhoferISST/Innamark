---
title: Risks and Technical Debts
sidebar_position: 11
description: List of identified risks and technical debts
---

<!--
 Copyright (c) 2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.

 This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 that can be found in the LICENSE file.
-->

# Risks and Technical Debts

In the following, *risks* (possible future problems) and *technical debts* (already existing) are 
presented.

## Risks

| Risk                                                         | Description                                                                                                                                                                                                                                                                                                                                               |
|--------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Unannounced breaking changes**                             | As discussed in the [Architecture Constraints](../architecture_constraints), the whole Innamark project does not have consistent versioning or releases. Thus, breaking changes can occur and are not noticed by the users.                                                                                                                               |
| **Watermarker library changes breaks other building blocks** | Since Innamark has various building blocks which depend on each other (e.g., the CLI tool or web interface working with the watermarker library), it can lead to problems if breaking changes occur in such a building block. If the watermarker library changes, it might also break the building blocks using it.                                       |
| **Documentation drift**                                      | The current documentation is hand-written and not generated or checked automatically. If the documentation is not updated when the code is changed, it can lead to outdated documentation. While first mitigations are implemented (such as a hint for agents in the `AGENTS.md`), it can still not guaranteed, especially for images like plantUML SVGs. |
| **Challenging diagnostic due to lack of log persistence**    | The watermarker library uses a special error handling with Status, Events, and Results (see [Error Handling Concept](../concepts/error-handling/)), it does not have a centralized logging inside a log file. If the user does not store or handle logs, debugging problems can get challenging.                                                          |
| **Breaking watermarks on normalization**                     | More and more watermark normalization tools are published, trying to clean a watermarked cover text. The usage of such tools can break or remove an Innamark watermark                                                                                                                                                                                    |
| **Watermark visibility**                                     | While the watermark is almost invisible in various tested applications due to the specific whitespace characters of the alphabet, they could get visible in edge-cases, like applications showing those control characters or when using custom fonts.                                                                                                    |
| **Capacity limit on short texts**                            | If a watermark can fit in a cover text at least one time depends on the length of the watermark, the length of the cover text and the used alphabet. There are cases were a cover text cann't be watermarked since it has not enough whitespaces.                                                                                                         | 

## Technical Debts
| Technical Debt                          | Description                                                                                                                                                                                                                                                                                                                                                                                                    |
|-----------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Missing changelog / migration guide** | Since there is no versioning or releases, no changelog or migration guide exist.                                                                                                                                                                                                                                                                                                                               |
| **Divergent dependency versions**       | Since Innamark has various building blocks, each building block (like the watermarker library, CLI tool, or web interface) has its own dependencies. Since there is no uniform version catalog, multiple versions of the dependencies could be used inside the overall Innamark project. This happens, for example, with dependencies such as the gradle-wrapper or the Kotlin version.                        |
| **Test coverage**                       | While the watermarker library is comparably well-tested and aims for a full test coverage, the other building blocks (CLI tool and web interface) aren't. Further, current tests mainly focus on Unit test levels of functions/methods, but misses deep end-to-end and integration tests such as verifying that all build targets (JVM and JS) work the same.                                                  |
| **Dead / deprecated code**              | The code has different leftovers currently not used (anymore), such as a `ZipFileWatermarker` in the watermarker library or a `WatermarkFileTab` in the web interface.                                                                                                                                                                                                                                         |
| **Long InnamarkTag implementation**     | While InnamarkTags can be configured with different options (like compression, hashing, etc.), each combination represents its own InnamarkTag and, therefore, its own class, leading to a very long [`InnamarkTag.kt`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/watermarks/InnamarkTag.kt) file. Adding another option would increase the file even more. |
| **Possibility of exceptions**           | Even if the watermark library aims for not using exceptions (see [Error Handling Concept](../concepts/error-handling/)), there might be edge-cases where exceptions are raised due to the usage of `!!` and `check`/`require` in the watermarker library.                                                                                                                                                      |
