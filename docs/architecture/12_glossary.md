---
title: Glossary
sidebar_position: 12
description: Definitions of most important terms
---

<!--
 Copyright (c) 2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.

 This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 that can be found in the LICENSE file.
-->

# Glossary

A collection of different terms used in this project and documentation with their definition, 
sorted by alphabet.

| Term                                   | Definition                                                                                                                                                                                                                                                                  |
|----------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Alphabet**                           | A list of characters (Kotlin datatype: `List<Char>`), currently invisible Unicode space characters, used to encode a watermark. Thus, a text- or byte-encoded watermark is transformed into a list of those alphabet characters.                                            |
| **Architecture Decision Record (ADR)** | A specific format to document relevant architectural decisions with further information about their context, considered options and consequences (see this [famous blog post by Michael Nygard](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions)). |
| **CLI**                                | Command-line Interface. A tool that brings watermarking functionalities in the terminal.                                                                                                                                                                                    |
| **Capacity**                           | The amount of whitespaces needed in the cover text to be able to embed a watermark.                                                                                                                                                                                         |
| **Cover Text**                         | A text that should be enhanced/secured by a watermark.                                                                                                                                                                                                                      |
| **Innamark**                           | Name of the project as a short form of "Invisible Watermarking".                                                                                                                                                                                                            |
| **Insert Positions**                   | The number of positions in the cover text in which a part of a watermark can be embedded. Since Innamark works with spaces, the insert positions of a cover text are the total sum of whitespaces in the cover text.                                                        |
| **Separator**                          | A **Separator Character** is the character used to split multiple watermark occurrences in a cover text.                                                                                                                                                                    |
| **SeparatorStrategy**                  | A strategy that defines how multiple watermarks are separated (e.g. add a separator character before each watermark).                                                                                                                                                       |
| **Transcoding**                        | The combination of the alphabet and the watermark encoding and decoding functions.                                                                                                                                                                                          |
| **Unicode**                            | A widely used and [published standard](https://home.unicode.org/) for character encoding, published by the [Unicode Consortium](https://www.unicode.org/consortium/).                                                                                                       |
| **Watermark**                          | Will be embedded by Innamark inside the cover text. This can be a text (e.g., "Copyright X"), key, encoded-data (including image), or any other byte-encoded sequence.                                                                                                      |

