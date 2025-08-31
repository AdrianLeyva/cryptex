package com.viacce.cryptex.crypto.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viacce.crypto.data.CryptexRepository
import com.viacce.crypto.domain.DecryptFileUseCase
import com.viacce.crypto.domain.EncryptFileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class CryptoViewModel : ViewModel() {

    val cryptoUiModelState: StateFlow<CryptoUiModel>
        get() = _cryptoUiModelState
    private var _cryptoUiModelState = MutableStateFlow(CryptoUiModel())

    fun encryptFile(file: File, password: String) {
        val repository = CryptexRepository()
        val usecase = EncryptFileUseCase(repository)
        emitUiModelState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val file = usecase.execute(file, password)
            withContext(Dispatchers.Main) {
                emitUiModelState(isLoading = false, file = file)
            }
        }
    }

    fun decryptFile(file: File, password: String) {
        val repository = CryptexRepository()
        val usecase = DecryptFileUseCase(repository)
        emitUiModelState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val file = usecase.execute(file, password)
            withContext(Dispatchers.Main) {
                emitUiModelState(isLoading = false, file = file)
            }
        }
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
