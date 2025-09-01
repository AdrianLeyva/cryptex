package com.viacce.crypto.algorithm

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class CryptexAlgorithm {

    private val algorithm = "AES/CBC/PKCS5Padding"
    private val keyAlgorithm = "PBKDF2WithHmacSHA256"
    private val keySize = 256
    private val iterations = 65536
    private val ivSize = 16
    private val saltSize = 16

    fun encrypt(data: ByteArray, password: String): ByteArray {
        val salt = ByteArray(saltSize).apply { SecureRandom().nextBytes(this) }
        val key = generateKey(password, salt)
        val iv = ByteArray(ivSize).apply { SecureRandom().nextBytes(this) }
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
        val encrypted = cipher.doFinal(data)
        return salt + iv + encrypted
    }

    fun decrypt(data: ByteArray, password: String): ByteArray {
        val salt = data.copyOfRange(0, saltSize)
        val iv = data.copyOfRange(saltSize, saltSize + ivSize)
        val encryptedData = data.copyOfRange(saltSize + ivSize, data.size)
        val key = generateKey(password, salt)
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        return cipher.doFinal(encryptedData)
    }

    private fun generateKey(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance(keyAlgorithm)
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keySize)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }
}
