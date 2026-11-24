---
title: Error Handling
description: Errors are handled via specific Status, Events, and Results
---

<!--
 Copyright (c) 2024-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.

 This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 that can be found in the LICENSE file.
-->

# Error Handling

We handle errors (and warnings) as values, not as exceptions. Our library
should not throw any exceptions, instead functions which can fail should return a
[`Status`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt)
or a
[`Result`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Result.kt)
if they return a value. This allows us to provide detailed reports about what happened during the
execution of a function, including warnings and custom success messages holding additional
information, like the exact source of the message. Furthermore, this approach enforces explicit
error handling, even if the dev decides to ignore the error, before accessing the return value of a
function (because the type is different). To see an example how you can handle such return values
see the
[`CLI`](https://github.com/FraunhoferISST/Innamark/blob/main/cli/src/main/kotlin/Main.kt#L281) tool.
For more advanced usages see
[`InnamarkTag`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/watermarks/InnamarkTag.kt#L585).


## Status
A
[`Status`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt)
represents the outcome of a function, **without** a return value, that is **not guaranteed to
succeed**. To be able to report any issues (or just additional information) that occurred during the
execution of a function, the class
[`Status`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt)
contains a list of [`Event`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt#L14)s that occurred during the execution. A
[`Status`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt)
that represents a success might not contain an
[`Event`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt#L14).
Multiple [`Status`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt) objects can be combined using
[`appendStatus`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt#L169) and
[`prependStatus`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt#L185) to propagate the reports.

## Event
An
[`Event`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt#L14)
represents different kinds of events that can occur during the execution of a function. There are 3
base variants:
- [`Success`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt#L51)
- [`Warning`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt#L62)
- [`Error`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt#L70)

The variants
[`Warning`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt#L62)
and
[`Error`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt#L70)
are abstract: For each event that can occur, a unique class must be created that overrides the
function `getMessage(): String` to explain the event. If a success message with additional
information is required, a new class inheriting from
[`Success`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt#L51)
can be created. All variants, but the default
[`Success`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt#L51)
variant, require a `source` string which should explain the origin of the event (e.g.,
`className.functionName`).

## Result
A
[`Result`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Result.kt)
represents the outcome of a function, **with** a return value, that is **not guaranteed to
succeed**. It functions like a
[`Status`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt)
(it uses a
[`Status`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt)
internally) but also has a generic member `value` that **can** contain the return value. When the
[`Status`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt)
is in **success** or **warning** state it **must contain a value**, otherwise it **should not**
contain a value.
