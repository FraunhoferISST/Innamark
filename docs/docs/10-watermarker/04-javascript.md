---
title: JavaScript
---

<!--
 Copyright (c) 2024 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.

 This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 that can be found in the LICENSE file.
-->

<!-- This file was developed with AI assistance. -->

import PatentHint from './../patent-hint.mdx'

<PatentHint components={props.components} />

# JavaScript
If you want to use watermarking inside your JavaScript project, this page gives you the necessary
information.

## Compile the Library
See [Installation](../installation).


## Plain JavaScript
In order to use this library in plain JavaScript, follow these steps:

1. Build the library ([see above](#compile-the-library))
2. Import the generated JS library:
   ```html
   <script src='[relative-path-to]/Innamark/watermarker/build/dist/js/productionExecutable/watermarker.js'></script>
   ```
3. This generates a variable `watermarker`s that provides access to the library functionalities.
   However, the actual watermarker object is behind the package path and you might want to reassign
   the watermarker variable to get rid of the long path like this:
   ```js
   watermarker = watermarker.de.fraunhofer.isst.innamark.watermarker;
   ```
4. Now you are ready to use the watermarker in plain JS. An example HTML file can be found
[in the repositories' examples](https://github.com/FraunhoferISST/Innamark/blob/main/samples/plain_js/plain_js_minimal_example.html).

You can open the file in the browser, no web server required.

## JavaScript Modules
In order to use this library in JavaScript modules, follow these steps:

1. Build the library ([see above](#compile-the-library))
2. Import the generated JS library according to your module system:

   ```Javascript
   // CommonJS (CJS)
   let watermarker = require('[relative-path-to]/Innamark/watermarker/build/dist/js/productionExecutable/watermarker.js'); // built as executable
   // or
   let watermarker = require('[relative-path-to]/Innamark/watermarker/build/js/packages/watermarker/kotlin/watermarker.js'); // built as module

   // ECMAScript Modules (ESM)
   import watermarker from '[relative-path-to]/Innamark/watermarker/build/dist/js/productionExecutable/watermarker.js'; // built as executable
   // or
   import watermarker from '[relative-path-to]/Innamark/watermarker/build/js/packages/watermarker/kotlin/watermarker.js'; // built as module
   ```

3. This generates a variable `watermarker` that provides access to the library functionalities.
   However, the actual watermarker object is behind the package path and you might want to reassign
   the watermarker variable to get rid of the long path like this:
   ```Javascript
   watermarker = watermarker.de.fraunhofer.isst.innamark.watermarker;
   ```
   *Note that imports in ESM are implicitly constant.*
4. Now you are ready to use the watermarker in your module. An example node script can be found
[in the repositories' examples](https://github.com/FraunhoferISST/Innamark/blob/main/samples/js_modules/js_modules_minimal_example.js).

Run it using `node npm_minimal_example.js`.

## Working With Collections

`Status.getEvents()` returns a JavaScript array of events. The returned array is a snapshot;
modifying it does not change the status.

`Watermark.Companion.mostFrequent(watermarks)` accepts a JavaScript array of watermarks and
returns a `Result` whose `value` is an array of the most frequent watermarks. An empty input
produces an empty array, and ties produce a warning.

## Unsigned Size And Checksum Values

The Kotlin API retains `UByte` tag identifiers and unsigned 32-bit `UInt` size/checksum values.
Since unsigned integer types are not directly exportable with the project's Kotlin compiler, use
the JavaScript-only `InnamarkTagJs` facade for numeric access:

```js
const tagApi = watermarker.types.watermarks.InnamarkTagJs;
const tagId = tagApi.getTag(tag);
const sizeResult = tagApi.extractSize(tag);
const builderResult = watermarker.types.watermarks.InnamarkTagBuilder.Companion.fromInnamarkTag(tag);
```

The facade accepts and leaves the original tag instance unchanged. Tag IDs are returned as `Int`
values in the range 0–255. Size and checksum results are JavaScript `number` values, which exactly
represent the full unsigned 32-bit range. Use `expectedTag`, `actualTag`, `unknownTag`,
`expectedSize`, `actualSize`, `expectedChecksum`, and `actualChecksum` with warning/error events to
read their numeric values. The underlying unsigned methods and event fields are not exported.

## Tag Metadata

Tag identifiers are numbers from 0 to 255. Size and CRC32 values returned by the JavaScript facade
are numbers from `0` to `4294967295`. Size, checksum, and hash range methods are not exported to
JavaScript; use the facade's extraction methods and the validation methods instead.
