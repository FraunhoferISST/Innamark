---
title: CLI
description: Command Line Interface build with Kotlin on Java build target.
---

<!--
 Copyright (c) 2024-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.

 This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 that can be found in the LICENSE file.
-->

# CLI

## Structure
The CLI tool uses the `kotlinx.cli` package for parsing arguments and calling the appropriate 
functions or printing usage information if parsing fails. Additionally, functions are included for 
unwrapping and handling Innamark's custom error handling classes 
(see [Concepts](../../../development/watermarker/concepts/#error-handling-1) for more information on error handling).

## Expansion
To Expand upon the CLI tool's current functionality you will have to:
- Create a new `kotlinx.cli.Subcommand` class for argument parsing within the `cli` function.
- Create a new function for the new actions that can be called by the Subcommand class's `execute` override function.
- For Top-Level commands (like `Add`, `Text`, `Remove`):
  - add the new Subcommand class to the `parser.subcommands` list within the `cli` function.
- For Lower-Level commands (like `TextAdd`, `TextList`, `TextRemove`):
  - Register the new Subcommand to the parent Subcommand class using the `init` block and `this.subcommands`.
