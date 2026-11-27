/*
 * SPDX-FileCopyrightText: 2023-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e. V.
 *
 * SPDX-License-Identifier: LicenseRef-Fraunhofer-Gesellschaft
 */

package de.fraunhofer.isst.innamark.watermarker.utils

import de.fraunhofer.isst.innamark.watermarker.types.responses.Event
import de.fraunhofer.isst.innamark.watermarker.types.responses.Result
import kotlin.js.JsExport

const val COMPRESSION_LEVEL: Int = 9

expect object Compression {
    /** Compresses [data] using the deflate algorithm */
    fun deflate(data: ByteArray): ByteArray

    /** Uncompresses [data] using the inflate algorithm */
    fun inflate(data: ByteArray): Result<ByteArray>
}

@JsExport
class InflationError(
    val reason: String,
) : Event.Error("Compression.inflate") {
    /** Returns a String explaining the event */
    override fun getMessage() = "Error inflating bytes: $reason."
}
