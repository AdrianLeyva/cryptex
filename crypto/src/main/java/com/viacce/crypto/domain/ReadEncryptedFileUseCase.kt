package com.viacce.crypto.domain

import com.viacce.crypto.data.CryptexRepository
import java.io.File

class ReadEncryptedFileUseCase(private val repository: CryptexRepository) {
    fun execute(file: File): ByteArray {
        return repository.readEncryptedFile(file)
    }
}
