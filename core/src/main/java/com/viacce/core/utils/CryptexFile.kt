package com.viacce.core.utils

import android.content.Context
import android.net.Uri
import android.os.Environment.DIRECTORY_DOCUMENTS
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore.MediaColumns.DISPLAY_NAME
import java.io.File

object CryptexFile {

    private const val DIRECTORY_CRYPTEX = "Cryptex"
    private const val TEMP_PREFIX = "temp_"
    private const val FILE_PREFIX = "file_"

    fun createCryptexDirectory() {
        File(getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), DIRECTORY_CRYPTEX).run {
            if (!exists()) mkdirs()
        }
    }

    fun createTemporalFile(context: Context, extension: String) = File(
        context.cacheDir,
        "$TEMP_PREFIX${System.currentTimeMillis()}$extension"
    )

    fun getFileNameFromUri(context: Context, uri: Uri): String {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                return cursor.getString(nameIndex)
            }
        }
        return "$FILE_PREFIX${System.currentTimeMillis()}"
    }
}
