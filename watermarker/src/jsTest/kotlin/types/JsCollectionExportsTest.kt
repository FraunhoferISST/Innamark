/*
 * Copyright (c) 2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

// This file was developed with AI assistance.

package de.fraunhofer.isst.innamark.watermarker.types

import de.fraunhofer.isst.innamark.watermarker.types.responses.Status
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.CRC32InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.CustomInnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.InnamarkTagBuilder
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.InnamarkTagJs
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.SizedCRC32InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.SizedSHA3256InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JsCollectionExportsTest {
    @Test
    fun getEvents_returns_independent_js_arrays() {
        val status = Status()
        val emptyEvents = status.asDynamic().getEvents()
        assertTrue(js("Array.isArray(emptyEvents)") as Boolean)
        assertEquals(0, emptyEvents.length as Int)

        val warning = Watermark.MultipleMostFrequentWarning(2)
        status.addEvent(warning)
        val events = status.asDynamic().getEvents()
        assertTrue(js("Array.isArray(events)") as Boolean)
        assertEquals(1, events.length as Int)
        assertEquals(warning.getMessage(), events[0].getMessage() as String)

        events.pop()
        assertEquals(1, status.getEvents().size)
    }

    @Test
    fun mostFrequent_accepts_and_returns_js_arrays() {
        val companion = Watermark.Companion.asDynamic()
        val emptyResult = companion.mostFrequent(emptyArray<Watermark>())
        val emptyWatermarks = emptyResult.value
        assertTrue(js("Array.isArray(emptyWatermarks)") as Boolean)
        assertEquals(0, emptyWatermarks.length as Int)
        assertTrue(emptyResult.isSuccess as Boolean)

        val first = Watermark.fromString("first")
        val second = Watermark.fromString("second")
        val result = companion.mostFrequent(arrayOf(first, second, first, second))
        val watermarks = result.value
        assertTrue(js("Array.isArray(watermarks)") as Boolean)
        assertEquals(4, watermarks.length as Int)
        assertTrue(result.isWarning as Boolean)
        assertEquals(first, watermarks[0])
        assertEquals(second, watermarks[2])
    }
}

class JsTagExportsTest {
    @Test
    fun tag_metadata_uses_numbers_and_hides_internal_ranges() {
        val custom = CustomInnamarkTag.fromString("test").asDynamic()
        val tagJs = InnamarkTagJs.asDynamic()
        assertTrue(js("typeof custom.getTag === 'undefined'") as Boolean)
        assertTrue(js("typeof custom.extractTag === 'undefined'") as Boolean)
        assertEquals(128, tagJs.getTag(custom) as Int)
        assertEquals(128, tagJs.extractTag(custom) as Int)

        val invalidTagInstance = CustomInnamarkTag(byteArrayOf(0, 1)).asDynamic()
        val invalidTagEvent = invalidTagInstance.validate().getEvents()[0]
        assertTrue(js("typeof invalidTagEvent.expectedTag === 'undefined'") as Boolean)
        assertEquals(128, tagJs.expectedTag(invalidTagEvent) as Int)
        assertEquals(0, tagJs.actualTag(invalidTagEvent) as Int)

        val tag =
            SizedCRC32InnamarkTag(
                byteArrayOf(
                    SizedCRC32InnamarkTag.TYPE_TAG.toByte(),
                    -1,
                    -1,
                    -1,
                    -1,
                    -1,
                    -1,
                    -1,
                    -1,
                ),
            )
        val dynamicTag = tag.asDynamic()
        assertTrue(js("typeof dynamicTag.getSizeRange === 'undefined'") as Boolean)
        assertTrue(js("typeof dynamicTag.getChecksumRange === 'undefined'") as Boolean)
        val hashTag = SizedSHA3256InnamarkTag.fromString("test").asDynamic()
        assertTrue(js("typeof hashTag.getHashRange === 'undefined'") as Boolean)
        assertTrue(hashTag.validateHash().isSuccess as Boolean)
        assertTrue(js("typeof dynamicTag.extractSize === 'undefined'") as Boolean)
        val sizeValue = tagJs.extractSize(dynamicTag).value
        assertTrue(js("typeof sizeValue === 'number'") as Boolean)
        assertEquals(4294967295.0, sizeValue as Double)
        val sizeStatus = dynamicTag.validateSize()
        assertTrue(sizeStatus.isWarning as Boolean)
        val sizeWarning = sizeStatus.getEvents()[0]
        assertTrue(js("typeof sizeWarning.expectedSize === 'undefined'") as Boolean)
        val expectedSize = tagJs.expectedSize(sizeWarning)
        assertTrue(js("typeof expectedSize === 'number'") as Boolean)
        assertEquals(4294967295.0, expectedSize as Double)
        val checksumValue = tagJs.extractChecksum(dynamicTag).value
        assertTrue(js("typeof checksumValue === 'number'") as Boolean)
        assertEquals(4294967295.0, checksumValue as Double)
        val jsChecksum = tagJs.calculateCRC32("123456789".encodeToByteArray())
        assertTrue(js("typeof jsChecksum === 'number'") as Boolean)
        assertEquals(3421780262.0, jsChecksum as Double)
        val checksum = CRC32InnamarkTag.calculateChecksum("123456789".encodeToByteArray())
        assertEquals(0xcbf43926u, checksum)

        val validTag = CRC32InnamarkTag.fromString("test")
        val calculatedChecksum = tagJs.calculateChecksum(validTag.asDynamic()).value
        assertTrue(js("typeof calculatedChecksum === 'number'") as Boolean)
        val builderResult =
            InnamarkTagBuilder
                .Companion
                .asDynamic()
                .fromInnamarkTag(validTag.asDynamic())
        assertTrue(builderResult.isSuccess as Boolean)
        assertEquals("test", builderResult.value.text as String)
        assertTrue(validTag.asDynamic().validateChecksum().isSuccess as Boolean)
        val invalidBytes = validTag.watermarkContent.copyOf()
        invalidBytes[1] = (invalidBytes[1].toInt() xor 1).toByte()
        val invalidTag = CRC32InnamarkTag(invalidBytes).asDynamic()
        val invalidStatus = invalidTag.validateChecksum()
        assertTrue(invalidStatus.isWarning as Boolean)
        val warning = invalidStatus.getEvents()[0]
        assertTrue(js("typeof warning.expectedChecksum === 'undefined'") as Boolean)
        assertTrue(js("typeof tagJs.expectedChecksum(warning) === 'number'") as Boolean)
        assertTrue(js("typeof tagJs.actualChecksum(warning) === 'number'") as Boolean)
    }
}
