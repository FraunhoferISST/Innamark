---
title: Solution Strategy
sidebar_position: 4
description: Strategic goals to create a solution
---

<!--
 Copyright (c) 2024-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.

 This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 that can be found in the LICENSE file.
-->

# Solution Strategy

The most fundamental goals regarding the solution strategy are described in the following. They 
mostly focus on the main watermarker library component.

| Goal              | Description                                                                                                                                                                                                                                                                                                                     | Details / Example                                                                                                                                                                                                   |
|-------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Extensibility** | The library is designed to allow easy extensibility for devs working on the core library as well as users of the library (but it has not been tested to see if this works as intended yet). Most functions only require *interfaces* instead of specialized classes to allow creating your own classes with different behavior. | Interface [`FileWatermarker`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/jvmMain/kotlin/watermarkers/file/FileWatermarker.kt) which allows adding support for other file types. |
| **Flexibility** | For most of the functionalities we created an interface and then implemented our *default* implementation. If this implementation does not meet the requirements, it can be exchanged with another class implementing the interface.  | [`Transcoding`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/watermarkers/text/PlainTextWatermarker.kt#L43) Interface allows specifying your own alphabet.  |
| **Usability** | Offering extensibility and flexibility often introduces a lot of complexity which makes it hard to understand how the library can and should be used. To make the usage easier we created convenient classes and functions which reduce the flexibility but should make it easy to use the library for general use-cases. Only users with special requirements have to look into the more flexible and complex API's. | A dedicated [usage documentation](../../docs/usage/) shows various examples. |
| **No Performance** | The library **does not** focus on performance. When ever we had to make a decision between performance and usability we chose the option for **better usability**. | - |
| **Code Quality** | The code quality is very important for us. We want to ensure that every dev is able to work on the code base by providing documentation and enforcing a good code style by using linters. | The [Contributing](https://github.com/FraunhoferISST/Innamark/blob/main/CONTRIBUTING.md) guide defines several aspect for good code quality and processes around it.  |
| **Testing** | For every new code added to the library, tests have to be added to validate the functionality. This includes *unit tests* as well as *integration tests*. Furthermore, extensive tests also provide a good point of reference for new devs on how the code should be used. | Existing tests for all builds ([`commonTest`](https://github.com/FraunhoferISST/Innamark/tree/main/watermarker/src/commonTest/kotlin)), Java ([`jvmTest`](https://github.com/FraunhoferISST/Innamark/tree/main/watermarker/src/jvmTest)) and JavaScript ([`jsTest`](https://github.com/FraunhoferISST/Innamark/tree/main/watermarker/src/jsTest/kotlin)) as well as [GitHub testing workflows](https://github.com/FraunhoferISST/Innamark/tree/main/.github/workflows). |
| **Error Handling** | We handle errors (and warnings) as value, not as exceptions. Our library should not throw any exceptions, instead functions which can fail should return a [`Status`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Status.kt) or a [`Result`](https://github.com/FraunhoferISST/Innamark/blob/main/watermarker/src/commonMain/kotlin/types/responses/Result.kt) if they return a value. | The [cross-cutting concept for error handling](../concepts/error-handling) describes all details. |

**Devs working on the project need to understand these goals and implement them in their daily
practice!**
