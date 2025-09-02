package com.viacce.crypto.domain

import com.viacce.crypto.data.CryptexRepository
import java.io.File
import javax.inject.Inject

class DecryptFileUseCase @Inject constructor(
    private val repository: CryptexRepository
) {
    fun execute(file: File, password: String): File {
        return repository.decryptFile(file, password)
    }
}
