package com.viacce.crypto.extensions

internal fun String.getMimeType() = when (lowercase()) {
    "png" -> "image/png"
    "jpg", "jpeg" -> "image/jpeg"
    "mp4" -> "video/mp4"
    "pdf" -> "application/pdf"
    else -> "application/octet-stream"
}
