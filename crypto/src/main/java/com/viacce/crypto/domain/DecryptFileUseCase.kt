package com.viacce.crypto.domain

import com.viacce.crypto.algorithm.CryptexAlgorithm
import com.viacce.crypto.algorithm.CryptexKeyGenerator
import com.viacce.crypto.data.CryptexRepository
import java.io.File

class DecryptFileUseCase(
    private val repository: CryptexRepository
) {
    
    fun execute(file: File, password: String): File {
        val (encryptedData, iv, salt) = repository.readCryptexFile(file)
        val key = CryptexKeyGenerator.generateKeyFromPassword(password, salt)
        val decrypted = CryptexAlgorithm.decrypt(encryptedData, key, iv)
        val originalName = file.name.removeSuffix(CryptexAlgorithm.EXTENSION)
        val outputFile = File(file.parentFile, originalName)
        outputFile.writeBytes(decrypted)
        return outputFile
    }
}
