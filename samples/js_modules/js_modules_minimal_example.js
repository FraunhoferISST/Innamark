/*
 * Copyright (c) 2024-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */
// This file was developed with AI assistance.
import innamark from "../../watermarker/build/dist/js/productionExecutable/watermarker.js";

const pkg = innamark.de.fraunhofer.isst.innamark.watermarker;
const watermarker = new pkg.watermarkers.text.PlainTextWatermarker();
const watermark = pkg.types.watermarks.Watermark.Companion.fromString("Hello World!");;

let text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor \
invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et \
justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum \
dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod \
tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam \
et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem \
ipsum.";

let result = watermarker.addWatermark(text, watermark);

if (result.isSuccess) {
  console.log(`Embedding successful. Result:\n\n${result.value}\n`);

  const extracted = watermarker.getWatermarkAsString(result.value);
  console.log(`Extracted watermark:\n\n${extracted.value}`);

} else {
  console.log(result);
}
