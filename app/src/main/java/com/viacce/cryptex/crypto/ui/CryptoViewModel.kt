package com.viacce.cryptex.crypto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viacce.crypto.domain.DecryptFileUseCase
import com.viacce.crypto.domain.EncryptFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CryptoViewModel @Inject constructor(
    private val encryptUseCase: EncryptFileUseCase,
    private val decryptUseCase: DecryptFileUseCase
) : ViewModel() {

    val cryptoUiModelState: StateFlow<CryptoUiModel>
        get() = _cryptoUiModelState

    private var _cryptoUiModelState = MutableStateFlow(CryptoUiModel())

    fun encryptFile(data: ByteArray, fileName: String, password: String) {
        _cryptoUiModelState.value = CryptoUiModel(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = encryptUseCase.execute(data, fileName, password)
                withContext(Dispatchers.Main) {
                    emitUiModelState(isLoading = false, file = file)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    emitUiModelState(isLoading = false, exception = e)
                }
            }
        }
    }

    fun decryptFile(file: File, password: String) {
        _cryptoUiModelState.value = CryptoUiModel(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val decrypted = decryptUseCase.execute(file, password)
                withContext(Dispatchers.Main) {
                    emitUiModelState(isLoading = false, file = decrypted)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    emitUiModelState(isLoading = false, exception = e)
                }
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
