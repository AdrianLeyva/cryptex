package com.viacce.cryptex.crypto.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
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
    val (permissionLauncher, encryptLauncher, decryptLauncher) =
        rememberCryptoLaunchers(viewModel, context)

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
        if (state.isLoading) LinearProgressIndicator()
        if (state.showPermissionRequesterDialog)
            CryptoPermissionsDialog(
                onConfirm = {
                    viewModel.dismissPermissionsDialog()
                    permissionLauncher.launch(null)
                },
                onDismiss = {
                    viewModel.dismissPermissionsDialog()
                }
            )
        state.showPasswordRequesterDialog?.let {
            CryptoPasswordDialog(
                onConfirm = { password ->
                    viewModel.dismissPasswordDialog()
                    with(it) {
                        when (this) {
                            is CryptexPasswordRequesterType.Encrypt ->
                                viewModel.encryptFile(selectedUri, password)

                            is CryptexPasswordRequesterType.Decrypt -> viewModel.decryptFile(
                                selectedUri,
                                password
                            )
                        }
                    }
                },
                onDismiss = {
                    viewModel.dismissPasswordDialog()
                }
            )
        }
        state.uri?.let {
            CryptoSuccessScreen(
                uri = it,
                snackBarHostState = snackBarHostState
            )
        }
        state.exception?.let {
            CryptoErrorScreen(
                exception = it,
                snackBarHostState = snackBarHostState
            )
        }
    }
}

@Composable
private fun rememberCryptoLaunchers(
    viewModel: CryptoViewModel,
    context: Context
): Triple<ManagedActivityResultLauncher<Uri?, Uri?>,
        ManagedActivityResultLauncher<Array<String>, Uri?>,
        ManagedActivityResultLauncher<Array<String>, Uri?>> {

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { treeUri ->
        treeUri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            viewModel.saveCryptexDirectoryGrantedPermission()
        }
    }

    val encryptLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.requestPassword(CryptexPasswordRequesterType.Encrypt(it)) }
    }

    val decryptLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.requestPassword(CryptexPasswordRequesterType.Decrypt(it)) }
    }

    return Triple(permissionLauncher, encryptLauncher, decryptLauncher)
}
