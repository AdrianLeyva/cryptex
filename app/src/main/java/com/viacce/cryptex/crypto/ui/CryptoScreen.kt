package com.viacce.cryptex.crypto.ui

import android.net.Uri
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
import com.viacce.core.utils.CryptexFile.createTemporalFile
import com.viacce.core.utils.CryptexFile.getFileNameFromUri
import com.viacce.cryptex.crypto.ui.components.CryptoErrorScreen
import com.viacce.cryptex.crypto.ui.components.CryptoScaffoldContainer
import com.viacce.cryptex.crypto.ui.components.CryptoSuccessScreen
import com.viacce.crypto.algorithm.CryptexConfig
import com.viacce.ui.components.CryptexScaffold

@Composable
fun CryptoScreen(
    modifier: Modifier = Modifier,
    viewModel: CryptoViewModel
) {
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    val encryptLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        it?.let { selectedUri ->
            val inputStream = context.contentResolver.openInputStream(selectedUri) ?: return@let
            val data = inputStream.readBytes()
            val fileName = getFileNameFromUri(context, selectedUri)
            viewModel.encryptFile(data, fileName, "cryptex")
        }
    }

    val decryptLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val inputStream = context.contentResolver.openInputStream(selectedUri) ?: return@let
            val tempFile = createTemporalFile(context, CryptexConfig.EXTENSION)
            tempFile.outputStream().use { output -> inputStream.copyTo(output) }
            viewModel.decryptFile(tempFile, "cryptex")
        }
    }

    CryptexScaffold(snackBarHostState = snackBarHostState) {
        val state by viewModel.cryptoUiModelState.collectAsStateWithLifecycle()
        CryptoScaffoldContainer(
            modifier = modifier,
            onEncryptListener = {
                encryptLauncher.launch("*/*")
            },
            onDecryptListener = {
                decryptLauncher.launch("*/*")
            }
        )
        with(state) {
            if (isLoading) LinearProgressIndicator()
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
