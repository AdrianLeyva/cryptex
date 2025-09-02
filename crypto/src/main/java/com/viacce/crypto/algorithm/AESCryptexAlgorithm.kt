package com.viacce.crypto.algorithm

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class AESCryptexAlgorithm : ICryptexAlgorithm {

    override fun encrypt(data: ByteArray, password: String): ByteArray {
        val salt = ByteArray(SALT_SIZE).apply { SecureRandom().nextBytes(this) }
        val key = generateKey(password, salt)
        val iv = ByteArray(IV_SIZE).apply { SecureRandom().nextBytes(this) }
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
        val encrypted = cipher.doFinal(data)
        return salt + iv + encrypted
    }

    override fun decrypt(data: ByteArray, password: String): ByteArray {
        val salt = data.copyOfRange(0, SALT_SIZE)
        val iv = data.copyOfRange(SALT_SIZE, SALT_SIZE + IV_SIZE)
        val encryptedData = data.copyOfRange(SALT_SIZE + IV_SIZE, data.size)
        val key = generateKey(password, salt)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        return cipher.doFinal(encryptedData)
    }

    private fun generateKey(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance(KEY_ALGORITHM)
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_SIZE)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, ALGORITHM_NAME)
    }

    private companion object Companion {
        const val ALGORITHM_NAME = "AES"
        const val ALGORITHM = "AES/CBC/PKCS5Padding"
        const val KEY_ALGORITHM = "PBKDF2WithHmacSHA256"
        const val KEY_SIZE = 256
        const val ITERATIONS = 65536
        const val IV_SIZE = 16
        const val SALT_SIZE = 16
    }
}
