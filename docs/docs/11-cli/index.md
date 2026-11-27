---
title: CLI
---

<!--
SPDX-FileCopyrightText: 2024-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e. V.

SPDX-License-Identifier: LicenseRef-Fraunhofer-Gesellschaft
-->

# CLI

## Overview
The CLI tool provides an easy first step into watermarking with Innamark. 
After following the [Installation](./installation) instruction you will be able to insert and 
extract watermarks directly into and out of Strings, `.txt` and `.md` files as well as `.zip` files 
using the commands listed at [Commands](./commands).

## Limitations
The CLI tool will currently only insert `RawInnamarkTags`  to avoid overly long and complex 
commands (more info on InnamarkTags in the corresponding
[Architecture Concept Documentation](./../../architecture/concepts/innamarktag)). For full 
customization check out the other methods of usage, namely 
[Watermarker](../watermarker) for direct library usage and 
[Webinterface](../webinterface) for deploying a watermarking Webinterface.
