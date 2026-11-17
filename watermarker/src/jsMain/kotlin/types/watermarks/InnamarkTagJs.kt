/*
 * Copyright (c) 2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

// This file was developed with AI assistance.

package de.fraunhofer.isst.innamark.watermarker.types.watermarks

import de.fraunhofer.isst.innamark.watermarker.types.responses.Event
import de.fraunhofer.isst.innamark.watermarker.types.responses.Result
import kotlin.js.JsExport

/** JavaScript-friendly numeric accessors for unsigned 32-bit InnamarkTag values. */
@JsExport
object InnamarkTagJs {
    /** Returns an InnamarkTag's identifier as a JavaScript number. */
    fun getTag(tag: InnamarkTagInterface): Int = tag.getTag().toInt()

    /** Extracts an InnamarkTag's identifier as a JavaScript number. */
    fun extractTag(tag: InnamarkTagInterface): Int = tag.extractTag().toInt()

    /** Extracts a tag size as a JavaScript number. */
    fun extractSize(tag: InnamarkTag.Sized): Result<Double> = tag.extractSize().toDoubleResult()

    /** Extracts a tag checksum as a JavaScript number. */
    fun extractChecksum(tag: InnamarkTag.Checksum): Result<Double> =
        tag.extractChecksum().toDoubleResult()

    /** Calculates a tag checksum as a JavaScript number. */
    fun calculateChecksum(tag: InnamarkTag.Checksum): Result<Double> =
        tag.calculateChecksum().toDoubleResult()

    /** Calculates the CRC32 checksum of [input] as a JavaScript number. */
    fun calculateCRC32(input: ByteArray): Double =
        CRC32InnamarkTag.calculateChecksum(input).toDouble()

    /** Returns the expected size from a mismatched-size warning as a JavaScript number. */
    fun expectedSize(event: Event): Double =
        (event as? InnamarkTag.MismatchedSizeWarning)?.expectedSize?.toDouble()
            ?: error("Event is not a mismatched-size warning.")

    /** Returns the actual size from a mismatched-size warning as a JavaScript number. */
    fun actualSize(event: Event): Double =
        (event as? InnamarkTag.MismatchedSizeWarning)?.actualSize?.toDouble()
            ?: error("Event is not a mismatched-size warning.")

    /** Returns the expected checksum from a checksum warning as a JavaScript number. */
    fun expectedChecksum(event: Event): Double =
        (event as? InnamarkTag.InvalidChecksumWarning)?.expectedChecksum?.toDouble()
            ?: error("Event is not a checksum warning.")

    /** Returns the actual checksum from a checksum warning as a JavaScript number. */
    fun actualChecksum(event: Event): Double =
        (event as? InnamarkTag.InvalidChecksumWarning)?.actualChecksum?.toDouble()
            ?: error("Event is not a checksum warning.")

    /** Returns the unknown tag identifier from an error as a JavaScript number. */
    fun unknownTag(event: Event): Int =
        (event as? InnamarkTag.UnknownTagError)?.tag?.toInt()
            ?: error("Event is not an unknown-tag error.")

    /** Returns the expected tag identifier from an invalid-tag error as a JavaScript number. */
    fun expectedTag(event: Event): Int =
        (event as? InnamarkTag.InvalidTagError)?.expectedTag?.toInt()
            ?: error("Event is not an invalid-tag error.")

    /** Returns the actual tag identifier from an invalid-tag error as a JavaScript number. */
    fun actualTag(event: Event): Int =
        (event as? InnamarkTag.InvalidTagError)?.actualTag?.toInt()
            ?: error("Event is not an invalid-tag error.")
}

private fun Result<UInt>.toDoubleResult(): Result<Double> = Result(status, value?.toDouble())
