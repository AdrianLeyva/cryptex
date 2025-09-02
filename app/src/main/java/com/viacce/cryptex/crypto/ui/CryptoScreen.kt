package com.viacce.cryptex.crypto.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.viacce.cryptex.crypto.ui.components.CryptoErrorScreen
import com.viacce.cryptex.crypto.ui.components.CryptoPasswordDialog
import com.viacce.cryptex.crypto.ui.components.CryptoPermissionsDialog
import com.viacce.cryptex.crypto.ui.components.CryptoScaffoldContainer
import com.viacce.cryptex.crypto.ui.components.CryptoSuccessScreen
import com.viacce.ui.components.CryptexScaffold

@Composable
fun CryptoScreen(
    modifier: Modifier = Modifier,
    viewModel: CryptoViewModel
) {
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) {
        it?.let { treeUri ->
            context.contentResolver.takePersistableUriPermission(
                treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            viewModel.saveCryptexDirectoryGrantedPermission()
        }
    }

    val encryptLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        it?.let { selectedUri ->
            viewModel.requestPassword(CryptexPasswordRequesterType.Encrypt(selectedUri))
        }
    }

    val decryptLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        it?.let { selectedUri ->
            viewModel.requestPassword(CryptexPasswordRequesterType.Decrypt(selectedUri))
        }
    }

    CryptexScaffold(snackBarHostState = snackBarHostState) {
        val state by viewModel.cryptoUiModelState.collectAsStateWithLifecycle()
        CryptoScaffoldContainer(
            modifier = modifier,
            onEncryptListener = {
                encryptLauncher.launch(arrayOf("*/*"))
            },
            onDecryptListener = {
                decryptLauncher.launch(arrayOf("*/*"))
            }
        )
        with(state) {
            if (isLoading) LinearProgressIndicator()
            if (showPermissionRequesterDialog)
                CryptoPermissionsDialog(
                    onConfirm = {
                        viewModel.dismissPermissionsDialog()
                        permissionLauncher.launch(null)
                    },
                    onDismiss = {
                        viewModel.dismissPermissionsDialog()
                    }
                )
            if (showPasswordRequesterDialog != null) {
                CryptoPasswordDialog(
                    onConfirm = { password ->
                        viewModel.dismissPasswordDialog()
                        with(showPasswordRequesterDialog) {
                            when (this) {
                                is CryptexPasswordRequesterType.Encrypt -> {
                                    viewModel.encryptFile(selectedUri, password)
                                }

                                is CryptexPasswordRequesterType.Decrypt -> {
                                    viewModel.decryptFile(selectedUri, password)
                                }
                            }
                        }
                    },
                    onDismiss = {
                        viewModel.dismissPasswordDialog()
                    }
                )
            }
            if (file != null)
                CryptoSuccessScreen(
                    file = file,
                    snackBarHostState = snackBarHostState
                )
            if (exception != null)
                CryptoErrorScreen(
                    exception = exception,
                    snackBarHostState = snackBarHostState
                )
        }
    }
}
