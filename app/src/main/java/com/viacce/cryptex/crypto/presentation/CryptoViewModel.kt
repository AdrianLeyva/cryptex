package com.viacce.cryptex.crypto.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class CryptoViewModel : ViewModel() {

    val cryptoUiModelState: StateFlow<CryptoUiModel>
        get() = _cryptoUiModelState
    private var _cryptoUiModelState = MutableStateFlow(CryptoUiModel())

    fun encryptFile(file: File, password: String) {

    }

    fun decryptFile(password: String) {

    }

    private fun emitUiModelState(
        isLoading: Boolean = false,
        file: File? = null,
        exception: Exception? = null
    ) {
        _cryptoUiModelState.value = CryptoUiModel(
            isLoading = isLoading,
            file = file,
            exception = exception
        )
    }
}
