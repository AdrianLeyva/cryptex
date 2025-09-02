package com.viacce.cryptex.crypto.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viacce.core.datastore.CryptexPreferencesManager
import com.viacce.core.utils.CryptexFile.createCryptexDirectory
import com.viacce.core.utils.Result
import com.viacce.crypto.domain.DecryptFileUseCase
import com.viacce.crypto.domain.EncryptFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CryptoViewModel @Inject constructor(
    private val encryptUseCase: EncryptFileUseCase,
    private val decryptUseCase: DecryptFileUseCase,
    private val cryptexPreferencesManager: CryptexPreferencesManager
) : ViewModel() {

    val cryptoUiModelState: StateFlow<CryptoUiModel>
        get() = _cryptoUiModelState

    private var _cryptoUiModelState = MutableStateFlow(CryptoUiModel())

    init {
        createCryptexDirectory()
        validatePermissions()
    }

    private fun validatePermissions() {
        viewModelScope.launch(Dispatchers.IO) {
            cryptexPreferencesManager.isCryptexDirectoryPermissionGranted()
                .collectLatest { isGranted ->
                    if (!isGranted) {
                        withContext(Dispatchers.Main) {
                            emitUiModelState(showPermissionRequesterDialog = true)
                        }
                    }
                }
        }
    }

    fun encryptFile(selectedUri: Uri, password: String) {
        _cryptoUiModelState.value = CryptoUiModel(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = encryptUseCase.execute(selectedUri, password)
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Success -> {
                        emitUiModelState(isLoading = false, file = result.data)
                    }

                    is Result.Error -> {
                        result.exception.printStackTrace()
                        emitUiModelState(isLoading = false, exception = result.exception)
                    }
                }
            }
        }
    }

    fun decryptFile(selectedUri: Uri, password: String) {
        _cryptoUiModelState.value = CryptoUiModel(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = decryptUseCase.execute(selectedUri, password)
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Success -> {
                        emitUiModelState(isLoading = false, file = result.data)
                    }

                    is Result.Error -> {
                        result.exception.printStackTrace()
                        emitUiModelState(isLoading = false, exception = result.exception)
                    }
                }
            }
        }
    }

    fun requestPassword(cryptexPasswordRequesterType: CryptexPasswordRequesterType) {
        emitUiModelState(showPasswordRequesterDialog = cryptexPasswordRequesterType)
    }

    fun dismissPermissionsDialog() =
        emitUiModelState(showPermissionRequesterDialog = false)

    fun dismissPasswordDialog() =
        emitUiModelState(showPasswordRequesterDialog = null)

    fun saveCryptexDirectoryGrantedPermission() =
        cryptexPreferencesManager.saveCryptexDirectoryPermissionGranted()

    private fun emitUiModelState(
        isLoading: Boolean = false,
        showPermissionRequesterDialog: Boolean = false,
        showPasswordRequesterDialog: CryptexPasswordRequesterType? = null,
        file: File? = null,
        exception: Exception? = null
    ) {
        _cryptoUiModelState.value = CryptoUiModel(
            isLoading = isLoading,
            showPermissionRequesterDialog = showPermissionRequesterDialog,
            showPasswordRequesterDialog = showPasswordRequesterDialog,
            file = file,
            exception = exception
        )
    }
}
