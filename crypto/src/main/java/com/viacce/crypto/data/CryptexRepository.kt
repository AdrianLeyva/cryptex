package com.viacce.crypto.data

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import com.viacce.crypto.algorithm.CryptexConfig.EXTENSION
import com.viacce.crypto.algorithm.CryptexConfig.FOLDER
import com.viacce.crypto.algorithm.ICryptexAlgorithm
import com.viacce.crypto.extensions.getMimeType
import java.io.File
import java.io.IOException
import javax.inject.Inject

class CryptexRepository @Inject constructor(
    private val context: Context,
    private val cryptoAlgorithm: ICryptexAlgorithm
) {

    fun encryptFile(data: ByteArray, originalFileName: String, password: String): File {
        val extension = originalFileName.substringAfterLast('.', "")
        val baseName = originalFileName.substringBeforeLast('.')
        val encryptedData = cryptoAlgorithm.encrypt(data, password)
        val extensionBytes = extension.toByteArray(Charsets.UTF_8)
        val finalData = byteArrayOf(extensionBytes.size.toByte()) + extensionBytes + encryptedData
        val finalFileName = "$baseName$EXTENSION"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, finalFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + FOLDER)
        }
        val uri = context.contentResolver.insert(
            MediaStore.Files.getContentUri("external"),
            contentValues
        ) ?: throw IOException("Error to create the file in MediaStore")
        context.contentResolver.openOutputStream(uri)?.use { it.write(finalData) }
        return File(context.cacheDir, finalFileName)
    }

    fun decryptFile(file: File, password: String): File {
        val fileData = file.readBytes()
        val extLength = fileData[0].toInt()
        val extBytes = fileData.sliceArray(1 until 1 + extLength)
        val extension = extBytes.toString(Charsets.UTF_8)
        val encryptedBytes = fileData.sliceArray(1 + extLength until fileData.size)
        val decryptedData = cryptoAlgorithm.decrypt(encryptedBytes, password)
        val originalName = file.name.removeSuffix(EXTENSION)
        val finalFileName = if (extension.isNotEmpty()) "$originalName.$extension" else originalName
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, finalFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, extension.getMimeType())
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + FOLDER)
        }
        val uri = context.contentResolver.insert(
            MediaStore.Files.getContentUri("external"),
            contentValues
        ) ?: throw IOException("Error to decrypt file")
        context.contentResolver.openOutputStream(uri)?.use { it.write(decryptedData) }
        return File(context.cacheDir, finalFileName)
    }
}
