---
title: Commands
---

<!--
 Copyright (c) 2024-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.

 This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 that can be found in the LICENSE file.
-->

# Commands

The following page gives an overview of possible commands that can be used.
All commands assume the `Innamark` alias has been set according to the 
[Installation](01-installation.md) instructions. If you did not set an alias you will need to 
replace `Innamark` with `java -jar build/libs/cli-<version>-all.jar` in all commands, inserting 
the appropriate value for `<version>` (e.g. `0.1.0-SNAPSHOT`) and executing within the `cli` 
Innamark project folder. Similarly, if you set an alias different from `Innamark` you will need 
to replace `Innamark` with your chosen alias.

## Embedding Watermarks
- `Innamark [-t <file type>] add <watermark> <source path> [<target path>]`

The `<watermark>` String will be embedded into the file at `<target path>` if specified, 
otherwise the file at `<source path>` will be overwritten.

- `Innamark text add <cover> <watermark>`

The `<watermark>` String will be embedded directly into the `<cover>` String and the result 
printed to the console.

## Extracting Watermarks
- `Innamark [-v] [-t <file type>] list <source path>`

All watermarks found in the file at `<source path>` are printed to the console, with duplicates 
being squashed unless using the verbose option `--verbose / -v`.

- `Innamark [-v] text list <watermarked String>`

All watermarks found in the `<watermarked String>` are printed to the console, with duplicates 
being squashed unless using the verbose option `--verbose / -v`.

## Removing Watermarks
- `Innamark [-t <file type>] remove <source path> [<target path>] `

Cleans the file at `<source path>` of any watermark characters and writes the result to the file at 
`<target path>` if specified, otherwise the file at `<source path>` will be overwritten.
- `Innamark text remove <watermarked String>`

Cleans the `<watermarked String>` of any watermark characters and prints the result to the console.

## Options

- `--file-type / -t`

For specifying the file type, if omitted the watermarker will try to infer the type based on 
file ending (e.g. *.txt). Currently `text` (case-insensitive) supports .md and .txt files while 
`zip` (case-insensitive) supports .zip files 
- `--verbose / -v`

For turning off squashing on the `list` commands, watermarks with the same content will be printed 
individually instead.
- `--help / -h`

For displaying usage information. This option is automatically applied on the `Innamark` command 
without arguments.

## Usage Example

- *Create a new file from the source file:*
  - Add a watermark to a text file:\
    `Innamark add "<watermark>" example.txt example.watermarked.txt`
  - Remove all watermarks contained in a file:\
    `Innamark remove example.watermarked.txt example.txt`
- *Modify the source file:*
  - Add a watermark to a text file:\
    `Innamark add "<watermark>" example.txt`
  - Remove all watermarks contained in a file:\
    `Innamark remove example.watermarked.txt`
- List all unique watermarks contained in a file:\
  `Innamark list example.watermarked.txt`
- *Work on Strings directly:*
  - Add a watermark to a cover String:\
    `Innamark text add "<cover>" "<watermark>"`
  - List all unique watermarks contained in a String:\
    `Innamark text list "<watermarked String>"`
  - Remove all watermarks contained in a String:\
    `Innamark text remove "<watermarked String>"`
