package com.viacce.crypto.algorithm

interface ICryptexAlgorithm {
    fun encrypt(data: ByteArray, password: String): ByteArray
    fun decrypt(data: ByteArray, password: String): ByteArray
}
