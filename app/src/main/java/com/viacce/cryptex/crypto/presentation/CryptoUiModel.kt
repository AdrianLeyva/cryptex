package com.viacce.cryptex.crypto.presentation

import java.io.File

data class CryptoUiModel(
    val isLoading: Boolean = false,
    val file: File? = null,
    val exception: Exception? = null
)
