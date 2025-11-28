/*
 * Copyright (c) 2023-2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.types.files

import de.fraunhofer.isst.innamark.watermarker.types.responses.Event
import de.fraunhofer.isst.innamark.watermarker.types.responses.Result
import java.io.File

class TextFile private constructor(
    path: String?,
    var content: String,
) : WatermarkableFile(path) {
    /** Converts the TextFile into raw bytes */
    override fun toBytes(): List<Byte> = this.content.encodeToByteArray().asList()

    companion object {
        internal val source = TextFile::class.simpleName!!

        /** Creates a TextFile with the text parsed from [content] */
        @JvmStatic
        fun fromBytes(
            bytes: ByteArray,
            path: String? = null,
        ): Result<TextFile> {
            val text =
                try {
                    bytes.decodeToString(throwOnInvalidSequence = true)
                } catch (e: CharacterCodingException) {
                    // TODO: Check available information
                    return InvalidByteError().into<_>()
                }

            val textFile = TextFile(path, text)
            return Result.success(textFile)
        }

        /** Creates a TextFile with text [text] */
        @JvmStatic
        fun fromString(text: String): TextFile = TextFile(null, text)
    }

    /** Checks if [this] and [other] are equal */
    override fun equals(other: Any?): Boolean =
        when (other) {
            is TextFile -> this.content == other.content
            else -> false
        }

    override fun hashCode(): Int = content.hashCode()

    class InvalidByteError : Event.Error(source) {
        /** Returns a String explaining the event */
        override fun getMessage(): String = "Cannot parse text file: File contains invalid byte(s)."
    }
}

/**
 * Parses the file [path] as TextFile
 *
 * Returns an error if:
 *  - Unable to read the file [path]
 *  - The file [path] contains invalid UTF-8 bytes
 */
fun TextFile.Companion.fromFile(path: String): Result<TextFile> {
    val bytes =
        try {
            File(path).readBytes()
        } catch (e: Exception) {
            return WatermarkableFile.ReadError(path, e.message ?: e.stackTraceToString()).into<_>()
        }
    return fromBytes(bytes, path)
}
