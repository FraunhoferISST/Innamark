/*
 * Copyright (c) 2023-2024 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
 *
 * This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 * that can be found in the LICENSE file.
 */

package de.fraunhofer.isst.innamark.watermarker.files

import de.fraunhofer.isst.innamark.watermarker.returnTypes.Event
import kotlin.js.JsExport

@JsExport
abstract class WatermarkableFile(internal val path: String?) {
    /** Converts the WatermarkableFile into raw bytes */
    abstract fun toBytes(): List<Byte>

    /** Checks if [this] and [other] are equal */
    abstract override fun equals(other: Any?): Boolean

    companion object {
        internal const val SOURCE = "File"
    }

    class ReadError(val path: String, val reason: String) :
        Event.Error("$SOURCE.read") {
        /** Returns a String explaining the event */
        override fun getMessage() = reason
    }

    class WriteError(val path: String, val reason: String) :
        Event.Error("$SOURCE.write") {
        /** Returns a String explaining the event */
        override fun getMessage() = reason
    }
}
