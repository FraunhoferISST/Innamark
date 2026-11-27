/*
 * SPDX-FileCopyrightText: 2024-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e. V.
 *
 * SPDX-License-Identifier: LicenseRef-Fraunhofer-Gesellschaft
 */
import innamark from "../../watermarker/build/dist/js/productionExecutable/watermarker.js";

const { PlainTextWatermarker } = innamark.de.fraunhofer.isst.innamark.watermarker.watermarkers.text;
const { Watermark } = innamark.de.fraunhofer.isst.innamark.watermarker.types.watermarks;

const watermarker = new PlainTextWatermarker();
const watermark = Watermark.Companion.fromString("Hello World!");
const cover = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";

const result = watermarker.addWatermark(cover, watermark);

if (result.isSuccess) {
  const extracted = watermarker.getWatermarkAsString(result.value);
  console.log("Embedding successful. Watermarked text:\n");
  console.log(`${result.value}\n`);
  console.log("Extracted watermark:\n");
  console.log(`${extracted.value}\n`);
} else {
  console.log(result);
}
