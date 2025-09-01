package com.viacce.crypto.data

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import com.viacce.crypto.algorithm.CryptexAlgorithm
import java.io.File
import java.io.IOException

class CryptexRepository(private val context: Context) {

    private val cryptoProvider = CryptexAlgorithm()


    fun encryptFile(data: ByteArray, originalFileName: String, password: String): File {
        val extension = originalFileName.substringAfterLast('.', "")
        val baseName = originalFileName.substringBeforeLast('.')
        val encryptedData = cryptoProvider.encrypt(data, password)

        val extensionBytes = extension.toByteArray(Charsets.UTF_8)
        val finalData = byteArrayOf(extensionBytes.size.toByte()) + extensionBytes + encryptedData
        val finalFileName = "$baseName.cryptex"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, finalFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/Cryptex")
        }

        val uri = context.contentResolver.insert(
            MediaStore.Files.getContentUri("external"),
            contentValues
        ) ?: throw IOException("No se pudo crear archivo en MediaStore")

        context.contentResolver.openOutputStream(uri)?.use { it.write(finalData) }

        return File(context.cacheDir, finalFileName)
    }

    fun decryptFile(file: File, password: String): File {
        val fileData = file.readBytes()

        val extLength = fileData[0].toInt()
        val extBytes = fileData.sliceArray(1 until 1 + extLength)
        val extension = extBytes.toString(Charsets.UTF_8)
        val encryptedBytes = fileData.sliceArray(1 + extLength until fileData.size)
        val decryptedData = cryptoProvider.decrypt(encryptedBytes, password)

        val originalName = file.name.removeSuffix(".cryptex")
        val finalFileName = if (extension.isNotEmpty()) "$originalName.$extension" else originalName

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, finalFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(extension))
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/Cryptex")
        }

        val uri = context.contentResolver.insert(
            MediaStore.Files.getContentUri("external"),
            contentValues
        ) ?: throw IOException("No se pudo crear archivo desencriptado")

        context.contentResolver.openOutputStream(uri)?.use { it.write(decryptedData) }

        return File(context.cacheDir, finalFileName)
    }

    private fun getMimeType(extension: String): String {
        return when (extension.lowercase()) {
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "mp4" -> "video/mp4"
            "pdf" -> "application/pdf"
            else -> "application/octet-stream"
        }
    }

    fun saveEncryptedFile(file: File, data: ByteArray): File {
        val outputFile = File(file.parentFile, "${file.name}.cryptex")
        outputFile.outputStream().use { it.write(data) }
        return outputFile
    }

    fun readEncryptedFile(file: File): ByteArray {
        return file.inputStream().use { it.readBytes() }
    }
}
