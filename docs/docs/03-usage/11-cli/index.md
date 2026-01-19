---
title: CLI
---

<!--
 Copyright (c) 2024-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.

 This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 that can be found in the LICENSE file.
-->

# CLI

## Overview
The CLI tool provides an easy, somewhat limited, first step into watermarking with Innamark. 
After following the [Installation](01-installation.md) instruction you will be able to insert and 
extract watermarks directly into and out of Strings, .txt and .md files as well as .zip files 
using the commands listed at [Commands](commands.md).

## Limitations
The CLI tool will only insert `RawInnamarkTags`  to avoid overly long and complex commands (more 
info on InnamarkTags in the corresponding
[Development Documentation](../../04-development/10-watermarker/InnamarkTag.md)). For full 
customization check out the other methods of [Usage](../), namely 
[Watermarker](../10-watermarker) for direct library usage and 
[Webinterface](../12-webinterface) for deploying a watermarking Webinterface.