/*
 * SPDX-FileCopyrightText: 2023-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e. V.
 *
 * SPDX-License-Identifier: LicenseRef-Fraunhofer-Gesellschaft
 */

package de.fraunhofer.isst.innamark.watermarker.types.files

import de.fraunhofer.isst.innamark.watermarker.types.responses.Event
import de.fraunhofer.isst.innamark.watermarker.types.responses.Status
import java.io.File

abstract class WatermarkableFile(
    internal val path: String?,
) {
    /** Converts the WatermarkableFile into raw bytes */
    abstract fun toBytes(): List<Byte>

    /** Checks if [this] and [other] are equal */
    abstract override fun equals(other: Any?): Boolean

    companion object {
        internal const val SOURCE = "File"
    }

    class ReadError(
        val path: String,
        val reason: String,
    ) : Event.Error("$SOURCE.read") {
        /** Returns a String explaining the event */
        override fun getMessage() = reason
    }

    class WriteError(
        val path: String,
        val reason: String,
    ) : Event.Error("$SOURCE.write") {
        /** Returns a String explaining the event */
        override fun getMessage() = reason
    }
}

/** Writes the text file from memory into the file [path] */
fun WatermarkableFile.writeToFile(path: String): Status {
    try {
        val file = File(path)
        file.writeBytes(this.toBytes().toByteArray())
    } catch (e: Exception) {
        return WatermarkableFile.WriteError(path, e.message ?: e.stackTraceToString()).into()
    }
    return Status.success()
}
