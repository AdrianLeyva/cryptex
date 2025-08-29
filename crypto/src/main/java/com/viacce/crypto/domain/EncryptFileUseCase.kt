package com.viacce.crypto.domain

import com.viacce.crypto.algorithm.CryptexAlgorithm
import com.viacce.crypto.algorithm.CryptexKeyGenerator
import com.viacce.crypto.data.CryptexRepository
import java.io.File

class EncryptFileUseCase(
    private val repository: CryptexRepository
) {

    fun execute(file: File, password: String): File {
        val data = file.readBytes()
        val salt = CryptexKeyGenerator.generateSalt()
        val key = CryptexKeyGenerator.generateKeyFromPassword(password, salt)
        val (encrypted, iv) = CryptexAlgorithm.encrypt(data, key)
        return repository.saveCryptexFile(file, encrypted, iv)
    }
}
