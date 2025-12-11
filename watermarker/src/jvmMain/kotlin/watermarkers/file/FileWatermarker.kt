/*
 * Copyright (c) 2023-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.watermarkers.file

import de.fraunhofer.isst.innamark.watermarker.types.files.WatermarkableFile
import de.fraunhofer.isst.innamark.watermarker.types.responses.Event
import de.fraunhofer.isst.innamark.watermarker.types.responses.Result
import de.fraunhofer.isst.innamark.watermarker.types.responses.Status
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.InnamarkTag
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.InnamarkTagBuilder
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark.MultipleMostFrequentWarning
import de.fraunhofer.isst.innamark.watermarker.types.watermarks.Watermark.StringDecodeWarning

/**
 * Interface for implementations facilitating watermarking of file covers.
 */
interface FileWatermarker<File : WatermarkableFile> {
    /**
     * Adds a watermark created from [watermark] ByteArray to file content at [source] and writes
     * it to [target].
     */
    fun addWatermark(
        source: String,
        target: String = source,
        watermark: ByteArray,
        wrap: Boolean = true,
        fileType: String? = null,
    ): Status

    /**
     * Adds a watermark created from [watermark] String to file content at [source] and writes
     * it to [target].
     */
    fun addWatermark(
        source: String,
        target: String = source,
        watermark: String,
        fileType: String? = null,
    ): Status = addWatermark(source, target, watermark.encodeToByteArray())

    /**
     * Adds a watermark object to file content at [source] and writes it to [target].
     */
    fun addWatermark(
        source: String,
        target: String = source,
        watermark: Watermark,
        fileType: String? = null,
    ): Status = addWatermark(source, target, watermark.watermarkContent)

    /**
     * Adds a watermark created from [innamarkTagBuilder] to file content at [source] and writes
     * it to [target].
     */
    fun addWatermark(
        source: String,
        target: String = source,
        innamarkTagBuilder: InnamarkTagBuilder,
        fileType: String? = null,
    ): Status = addWatermark(source, target, innamarkTagBuilder.finish())

    /** Checks if the file at [source] contains a watermark */
    fun containsWatermark(
        source: String,
        fileType: String? = null,
    ): Result<Boolean>

    /**
     * Returns a [Result] containing a list of [Watermark]s in the file at [source]. Attempts
     * to parse Watermarks found into [InnamarkTag]s and returns them instead if
     * [InnamarkTag.validate] returns [Event.Success] on all Watermarks.
     *
     * When [squash] is true: watermarks with the same content are merged.
     * When [singleWatermark] is true: only the most frequent watermark is returned.
     */
    fun getWatermarks(
        source: String,
        fileType: String? = null,
        squash: Boolean = false,
        singleWatermark: Boolean = false,
    ): Result<List<Watermark>>

    /**
     * Returns a [Result] containing the most frequent Watermark in the file at [source] as a String.
     *
     * Result contains an empty String if no Watermarks were found.
     * Result contains a [MultipleMostFrequentWarning] in cases where an unambiguous Watermark could not be extracted.
     * Result contains a [StringDecodeWarning] in cases where a byte cannot be read as UTF-8.
     */
    fun getWatermarkAsString(
        source: String,
        fileType: String? = null,
    ): Result<String>

    /**
     * Returns a [Result] containing the most frequent Watermark in the file at [source] as a
     * ByteArray.
     *
     * Result contains an empty ByteArray if no Watermarks were found.
     * Result contains a [MultipleMostFrequentWarning] in cases where an unambiguous Watermark could not be extracted.
     */
    fun getWatermarkAsByteArray(
        source: String,
        fileType: String? = null,
    ): Result<ByteArray>

    /**
     * Removes all watermarks in the file at [source] and writes the result to [target].
     */
    fun removeWatermarks(
        source: String,
        target: String = source,
        fileType: String? = null,
    ): Status

    /** Parses [bytes] as File */
    fun parseBytes(bytes: ByteArray): Result<File>

    /** Returns the name of the FileWatermarker. Used in Event messages. */
    fun getSource(): String
}
