package com.viacce.crypto.domain

import android.net.Uri
import com.viacce.core.utils.Result
import com.viacce.crypto.data.CryptexRepository
import javax.inject.Inject

class EncryptFileUseCase @Inject constructor(
    private val repository: CryptexRepository
) {
    fun execute(selectedUri: Uri, password: String): Result<Uri> {
        return repository.encryptFile(selectedUri, password)
    }
}
