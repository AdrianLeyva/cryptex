package com.viacce.crypto.data

import com.viacce.crypto.algorithm.CryptexAlgorithm
import java.io.File

class CryptexRepository {

    fun saveCryptexFile(originalFile: File, encryptedData: ByteArray, iv: ByteArray): File {
        val outputFile =
            File(originalFile.parentFile, "${originalFile.name}${CryptexAlgorithm.EXTENSION}")
        outputFile.outputStream().use { stream ->
            stream.write(iv.size)
            stream.write(iv)
            stream.write(encryptedData)
        }
        return outputFile
    }

    fun readCryptexFile(file: File): Triple<ByteArray, ByteArray, ByteArray> {
        val bytes = file.readBytes()
        var index = 0
        
        val saltSize = bytes[index++].toInt()
        val salt = bytes.copyOfRange(index, index + saltSize)
        index += saltSize

        val ivSize = bytes[index++].toInt()
        val iv = bytes.copyOfRange(index, index + ivSize)
        index += ivSize

        val encryptedData = bytes.copyOfRange(index, bytes.size)
        return Triple(encryptedData, iv, salt)
    }
}
