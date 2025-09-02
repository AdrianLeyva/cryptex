package com.viacce.crypto.domain

import com.viacce.crypto.data.CryptexRepository
import java.io.File
import javax.inject.Inject

class EncryptFileUseCase @Inject constructor(
    private val repository: CryptexRepository
) {
    fun execute(data: ByteArray, fileName: String, password: String): File {
        return repository.encryptFile(data, fileName, password)
    }
}
