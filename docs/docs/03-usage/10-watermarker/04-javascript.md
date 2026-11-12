---
title: JavaScript
---

<!--
 Copyright (c) 2024-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.

 This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 that can be found in the LICENSE file.
-->

import PatentHint from './../../patent-hint.mdx'

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
3. This generates a variable `watermarker` that provides access to the library functionalities.
   However, the actual watermarker object is behind the package path and you might want to reassign
   the watermarker variable to get rid of the long path like this:
   ```js
   watermarker = watermarker.de.fraunhofer.isst.innamark.watermarker;
   ```
4. Now you are ready to use the watermarker in plain JS.

An [example HTML file](https://github.com/FraunhoferISST/Innamark/blob/main/samples/plain_js/plain_js_minimal_example.html)
can be found [in the repository's examples](https://github.com/FraunhoferISST/Innamark/blob/main/samples/).
You can open the file in the browser, no web server required.

## JavaScript Modules
In order to use this library in JavaScript modules, follow these steps:

1. Build the library ([see above](#compile-the-library))
2. Import the generated JS library according to your module system:

   ```js
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
   In CommonJS, you can reassign this variable to shorten the package path:
   ```js
   watermarker = watermarker.de.fraunhofer.isst.innamark.watermarker;
   ```
   In ESM, import bindings are read-only and cannot be reassigned. Create a separate variable instead:
   ```js
   const innamark = watermarker.de.fraunhofer.isst.innamark.watermarker;
   ```
4. Now you are ready to use the watermarker in your module.

An [example Node.js script](https://github.com/FraunhoferISST/Innamark/blob/main/samples/js_modules/js_modules_minimal_example.js)
can be found [in the repository's examples](https://github.com/FraunhoferISST/Innamark/blob/main/samples/).
Run it from the `samples/js_modules` directory using `node js_modules_minimal_example.js`.
