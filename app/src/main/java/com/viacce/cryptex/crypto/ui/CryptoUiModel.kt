package com.viacce.cryptex.crypto.ui

import android.net.Uri
import java.io.File

data class CryptoUiModel(
    val isLoading: Boolean = false,
    val showPermissionRequesterDialog: Boolean = false,
    val showPasswordRequesterDialog: CryptexPasswordRequesterType? = null,
    val file: File? = null,
    val exception: Exception? = null
)

sealed class CryptexPasswordRequesterType {
    data class Encrypt(val selectedUri: Uri) : CryptexPasswordRequesterType()
    data class Decrypt(val selectedUri: Uri) : CryptexPasswordRequesterType()
}
