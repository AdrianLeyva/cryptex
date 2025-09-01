package com.viacce.crypto.domain

import com.viacce.crypto.data.CryptexRepository
import java.io.File

class SaveEncryptedFileUseCase(private val repository: CryptexRepository) {
    fun execute(file: File, encryptedData: ByteArray): File {
        return repository.saveEncryptedFile(file, encryptedData)
    }
}
