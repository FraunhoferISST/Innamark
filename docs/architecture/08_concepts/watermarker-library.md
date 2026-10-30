---
title: Watermarker Library
description: Various important concepts of the Kotlin library
---

<!--
 Copyright (c) 2024-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.

 This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 that can be found in the LICENSE file.
-->

# Watermarker Library

The following page describes cross-cutting concepts regarding the watermarker library component.

## Overview
### Kotlin Multiplatform
Our library is developed as
[Kotlin Multiplatform Library](https://kotlinlang.org/docs/multiplatform.html) with support for
Kotlin, Java, and JavaScript. This allows the library to be used in any JVM compatible language
(e.g., Java, Kotlin), Node.js, and in plain JavaScript (e.g., browsers).

In some cases, different code is required for different platforms. In such cases the [`expect` and
`actual`](https://kotlinlang.org/docs/multiplatform-expect-actual.html)
feature of Kotlin is used, or some features (like operating on files) are only available on some
platforms. Because of this, code in the same package can be in different paths, depending on the
compilation target. For this reason the following refers to packages instead of paths.
- *Example*: when compiling for JVM, the package `de.fraunhofer.isst.innamark.watermarker.types`
  contains `*.kt` files from `src/commonMain/kotlin/types` and `src/jvmMain/kotlin/types`.

### Code Base
The watermarker library offers two main functionalities:
1. [Insert and extract watermarks](#insert-and-extract-watermarks):
   insert and extract additional information into/from files/data.
2. [Generate and parse watermarks](#generate-and-parse-watermarks):
   generate and parse information that is used in watermarks.

These tasks seem similar but are very independent of each other. The only requirement they share is
the data format: the library inserts and extracts watermarks as bytes, therefore the *generate and
parse watermarks* task must be able to generate and parse bytes.

### Error Handling
We handle errors (and warnings) as values, not as exceptions. Our library
should not throw any exceptions, instead functions which can fail should return a `Status` or a
`Result` if they return a value. See [Error Handling](../error-handling) for details.

---

## Insert and Extract Watermarks
The code in the package `de.fraunhofer.isst.innamark.watermarker.types.files` is responsible for parsing and
generating supported file types in memory:
- all classes adding support for a file type must implement the interface
  [`WatermarkableFile`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/jvmMain/kotlin/types/files/WatermarkableFile.kt)
- the classes should offer functions to modify the contents of the file when possible
  - *Example*:
    [`ZipFile`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/jvmMain/kotlin/types/files/ZipFile.kt#L258)
    offers functions to add/remove extra fields into the header section of zip files

The code in the package `de.fraunhofer.isst.innamark.watermarker.watermarkers.file` is responsible for
inserting and extracting watermarks into
[`WatermarkableFile`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/jvmMain/kotlin/types/files/WatermarkableFile.kt)s:
- for each instance of
  [`WatermarkableFile`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/jvmMain/kotlin/types/files/WatermarkableFile.kt),
  a class implementing
  [`FileWatermarker`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/jvmMain/kotlin/watermarkers/file/FileWatermarker.kt)
  for that specific
  instance should exist (e.g.,
  [`ZipFileWatermarker`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/jvmMain/kotlin/watermarkers/file/ZipFileWatermarker.kt)
  for
  [`ZipFile`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/jvmMain/kotlin/types/files/ZipFile.kt)
  which is responsible for inserting,
  extracting and removing watermarks into/from that
  [`WatermarkableFile`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/jvmMain/kotlin/types/files/WatermarkableFile.kt)
  while maintaining a valid file.

## Generate and Parse Watermarks
The code in the package `de.fraunhofer.isst.innamark.watermarker.types.watermarks` is responsible for
generating watermarks and parsing extracted watermarks:
- The class
  [`Watermark`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/watermarks/Watermark.kt)
  is the most basic watermark that offers very limited functionality but is used as a base class for
  many functions that expect a watermark as parameter as well as for more sophisticated watermarks. All
  watermarks should directly or indirectly inherit from this class.
- The class
  [`InnamarkTag`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/watermarks/InnamarkTag.kt)
  implements a watermark scheme that uses the first byte of the watermark as a
  tag to identify the format of the watermark. The format defines if additional information about the
  watermark (e.g., size, checksums, or hashes) is included in the watermark. The format of the
  content itself is not defined in detail (e.g., text, certificate, image, ...), but general
  information about the content is defined (currently only whether the content is compressed).
  For more details see the [InnamarkTag](../Innamarktag) documentation section.
  This implies that a user extracting an `InnamarkTag` must know the type of content but not the format
  used to store the content.
  - To offer convenient functions to generate, modify, and parse
    [`InnamarkTag`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/watermarks/InnamarkTag.kt)s
    with specific content (e.g., text, certificate, ...) the interface
    [`InnamarkTagBuilder`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/watermarks/InnamarkTagBuilder.kt)
    can be used to implement a class with a builder-like pattern.
