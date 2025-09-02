package com.viacce.crypto.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import com.viacce.core.exceptions.DecryptionException
import com.viacce.core.exceptions.EncryptionException
import com.viacce.core.utils.CryptexFile.createTemporalFile
import com.viacce.core.utils.CryptexFile.getFileNameFromUri
import com.viacce.core.utils.Result
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

    fun encryptFile(selectedUri: Uri, password: String): Result<File> = try {
        context.contentResolver.openInputStream(selectedUri)?.use { inputStream ->
            val data = inputStream.readBytes()
            val fileName = getFileNameFromUri(context, selectedUri)
            val extension = fileName.substringAfterLast('.', "")
            val baseName = fileName.substringBeforeLast('.')
            val encryptedData = cryptoAlgorithm.encrypt(data, password)
            val extensionBytes = extension.toByteArray(Charsets.UTF_8)
            val finalData =
                byteArrayOf(extensionBytes.size.toByte()) + extensionBytes + encryptedData
            val finalFileName = "$baseName$EXTENSION"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, finalFileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + FOLDER)
            }
            val uri = context.contentResolver.insert(
                MediaStore.Files.getContentUri("external"),
                contentValues
            ) ?: throw IOException("Error creating file in MediaStore")
            context.contentResolver.openOutputStream(uri)?.use { it.write(finalData) }
            Result.Success(File(context.cacheDir, finalFileName))
        } ?: Result.Error(IOException("Failed to open input stream for URI: $selectedUri"))
    } catch (e: Exception) {
        Result.Error(EncryptionException(), e.message)
    }

    fun decryptFile(selectedUri: Uri, password: String): Result<File> = try {
        val inputStream = context.contentResolver.openInputStream(selectedUri)
        val file = createTemporalFile(context, EXTENSION)
        file.outputStream().use { output -> inputStream?.copyTo(output) }
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
        ) ?: throw IOException("Error creating decrypted file")
        context.contentResolver.openOutputStream(uri)?.use { it.write(decryptedData) }
        DocumentFile.fromSingleUri(context, selectedUri)?.delete()
        Result.Success(File(context.cacheDir, finalFileName))
    } catch (e: Exception) {
        Result.Error(DecryptionException(), e.message)
    }
}
