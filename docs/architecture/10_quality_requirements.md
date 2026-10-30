---
title: Quality Requirements
sidebar_position: 10
description: Detailed quality requirements based on quality goals
---

<!--
 Copyright (c) 2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.

 This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 that can be found in the LICENSE file.
-->

<!-- This file was developed with AI assistance. -->

# Quality Requirements

The three top quality goals listed in [Introduction and Goals](../introduction_and_goals) are
refined here into measurable quality scenarios. The requirements are classified using the quality
characteristics of [ISO/IEC 25010:2023](https://www.iso.org/standard/78176.html). They focus on the
watermarker library, which is the canonical implementation behind the CLI and the webinterface.

## Quality Requirements Overview

Each row maps one quality requirement to a quality characteristic of ISO/IEC 25010:2023 and to the
quality scenarios that make it verifiable. Requirements marked as *top goal* are taken from
[Introduction and Goals](../introduction_and_goals), all others are derived requirements.

| ISO/IEC 25010:2023 Characteristic | Sub-Characteristic     | Quality Requirement                                                                                 | Scenarios  |
|-----------------------------------|------------------------|-----------------------------------------------------------------------------------------------------|------------|
| Functional suitability            | Functional correctness | **Invisible** (top goal): a human reader does not notice a watermarked cover text                   | QS-2       |
| Functional suitability            | Functional correctness | **Length-preserving** (top goal): watermarking does not change the cover text length                | QS-3       |
| Functional suitability            | Functional correctness | Embedding and extraction are correct and behave predictably at the limits of the available capacity | QS-1, QS-4 |
| Reliability                       | Fault tolerance        | **Robust** (top goal): the watermark survives modifications as long as possible                     | QS-5       |
| Compatibility                     | Interoperability       | All platform builds of the library (JVM, JS) produce and read the same watermarks                   | QS-6       |
| Security                          | Confidentiality        | Data sovereignty: The user has full control over its cover texts and watermarks                     | QS-7       |
| Maintainability                   | Analyzability          | Errors and warnings are reported as values with a description and an origin, not as exceptions      | QS-8       |
| Maintainability                   | Modifiability          | New file types and transcodings can be added without modifying existing implementations             | QS-9       |

### Non-Goals

The library explicitly does **not** aim for performance efficiency. Where a decision had to be made
between performance and usability, usability was chosen (see
[Solution Strategy](../solution_strategy)).

## Quality Scenarios

The scenarios below make the requirements above concrete and serve as acceptance criteria.

| #    | Scenario                 | Context and Stimulus                                                                                      | Response Measure                                                                                                                                                                    |
|------|--------------------------|-----------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| QS-1 | Round-trip correctness   | An application embeds an arbitrary watermark into a cover text and extracts it again afterward            | The extracted watermark equals the originally embedded watermark                                                                                                                    |
| QS-2 | Invisibility             | A human compares the original and the watermarked text in a typical text viewer                           | No difference in glyphs, spacing, or line breaks is perceivable                                                                                                                     |
| QS-3 | Length preservation      | An application compares the original and the watermarked text programmatically                            | Both texts have the same number of characters and the same sequence of visible characters                                                                                           |
| QS-4 | Capacity limit           | An application tries to embed a watermark into a very short cover text (watermark does not fit in)        | The operation reports a warning and returns the cover text unchanged, so no corrupted output is produced                                                                            |
| QS-5 | Robustness               | Parts of a watermarked text are removed or the text is copied into another application and modified       | Extraction returns the original watermark while at least one complete occurrence remains, otherwise a warning is returned                                                           |
| QS-6 | Interoperability         | A watermark is embedded by one platform build of the library and extracted by another platform build      | Both builds extract the identical watermark content                                                                                                                                 |
| QS-7 | Data sovereignty         | An application performs embedding or extraction while network traffic to and from the process is observed | No cover text or watermark is transferred to an external party                                                                                                                      |
| QS-8 | Error handling as values | An application passes invalid input, such as empty data, unsupported content, or malformed encodings      | A result value describes the problem and its origin, so no exception is raised from the core watermarker library (see [Error Handling Concept](../concepts/error-handling/))        |
| QS-9 | Extensibility (change)   | A contributor adds support for a new file type or a new watermark transcoding                             | Only new implementations and their registration are required, while existing implementations remain unchanged (see [Watermarker Library Concept](../concepts/watermarker-library/)) |
