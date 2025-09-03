package com.viacce.crypto.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment.DIRECTORY_DOCUMENTS
import android.provider.MediaStore.Files.getContentUri
import android.provider.MediaStore.MediaColumns.DISPLAY_NAME
import android.provider.MediaStore.MediaColumns.MIME_TYPE
import android.provider.MediaStore.MediaColumns.RELATIVE_PATH
import androidx.documentfile.provider.DocumentFile
import com.viacce.core.exceptions.DecryptionException
import com.viacce.core.exceptions.EncryptionException
import com.viacce.core.utils.CryptexFile.createTemporalFile
import com.viacce.core.utils.CryptexFile.getFileNameFromUri
import com.viacce.core.utils.Result
import com.viacce.crypto.algorithm.CryptexConfig.BINARY_MIME
import com.viacce.crypto.algorithm.CryptexConfig.CRYPTEX_EXTENSION
import com.viacce.crypto.algorithm.CryptexConfig.CRYPTEX_FOLDER
import com.viacce.crypto.algorithm.ICryptexAlgorithm
import com.viacce.crypto.extensions.getMimeType
import java.io.IOException
import javax.inject.Inject

class CryptexRepository @Inject constructor(
    private val context: Context,
    private val cryptoAlgorithm: ICryptexAlgorithm
) {

    fun encryptFile(selectedUri: Uri, password: String): Result<Uri> = runCatching {
        context.contentResolver.openInputStream(selectedUri)?.use { inputStream ->
            val fileName = getFileNameFromUri(context, selectedUri)
            val extension = fileName.substringAfterLast('.', "")
            val baseName = fileName.substringBeforeLast('.')
            val data = inputStream.readBytes()
            val encryptedData = cryptoAlgorithm.encrypt(data, password)
            val extensionBytes = extension.toByteArray(Charsets.UTF_8)
            val finalData = byteArrayOf(extensionBytes.size.toByte()) +
                    extensionBytes + encryptedData
            val finalFileName = "$baseName$CRYPTEX_EXTENSION"
            saveToMediaStore(
                fileName = finalFileName,
                mimeType = BINARY_MIME,
                path = DIRECTORY_DOCUMENTS + CRYPTEX_FOLDER,
                data = finalData
            )
        } ?: throw IOException("Failed to open input stream for URI: $selectedUri")
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(EncryptionException(), it.localizedMessage) }
    )

    fun decryptFile(selectedUri: Uri, password: String): Result<Uri> = runCatching {
        val file = createTemporalFile(context, CRYPTEX_EXTENSION)
        context.contentResolver.openInputStream(selectedUri)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
        val fileData = file.readBytes()
        val extLength = fileData[0].toInt()
        val extension = fileData.copyOfRange(1, 1 + extLength).toString(Charsets.UTF_8)
        val encryptedBytes = fileData.copyOfRange(1 + extLength, fileData.size)
        val decryptedData = cryptoAlgorithm.decrypt(encryptedBytes, password)
        val originalName = file.name.removeSuffix(CRYPTEX_EXTENSION)
        val finalFileName = if (extension.isNotEmpty()) "$originalName.$extension" else originalName
        val uri = saveToMediaStore(
            fileName = finalFileName,
            mimeType = extension.getMimeType(),
            path = DIRECTORY_DOCUMENTS + CRYPTEX_FOLDER,
            data = decryptedData
        )
        DocumentFile.fromSingleUri(context, selectedUri)?.delete()
        uri
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DecryptionException(), it.localizedMessage) }
    )

    private fun saveToMediaStore(
        fileName: String,
        mimeType: String,
        path: String,
        data: ByteArray
    ): Uri {
        val contentValues = ContentValues().apply {
            put(DISPLAY_NAME, fileName)
            put(MIME_TYPE, mimeType)
            put(RELATIVE_PATH, path)
        }
        val uri = context.contentResolver.insert(getContentUri("external"), contentValues)
            ?: throw IOException("Error creating file in MediaStore")
        context.contentResolver.openOutputStream(uri)?.use { it.write(data) }
            ?: throw IOException("Error writing to MediaStore outputStream")
        return uri
    }
}
