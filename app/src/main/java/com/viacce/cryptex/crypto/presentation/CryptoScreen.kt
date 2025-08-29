package com.viacce.cryptex.crypto.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.viacce.cryptex.R
import com.viacce.ui.components.CryptexButton
import com.viacce.ui.components.CryptexScaffold
import java.io.File

@Composable
fun CryptoScreen(
    cryptoUiModel: CryptoUiModel,
    modifier: Modifier = Modifier,
    onEncryptFile: (File) -> Unit,
    onDecryptFile: (File) -> Unit
) {
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val encryptLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = File(it.path ?: "")
            onEncryptFile(file)
        }
    }
    val decryptLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = File(it.path ?: "")
            onDecryptFile(file)
        }
    }

    CryptexScaffold(snackBarHostState = snackBarHostState) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Cryptex",
                modifier = Modifier.padding(bottom = 16.dp),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CryptexButton(
                    text = context.getString(R.string.encrypt),
                    icon = Icons.Default.Lock,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    encryptLauncher.launch("*/*")
                }
                CryptexButton(
                    text = context.getString(R.string.decrypt),
                    icon = Icons.Default.Lock,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    decryptLauncher.launch("*/*")
                }
            }
        }
        with(cryptoUiModel) {
            if (isLoading) LinearProgressIndicator()
            if (file != null)
                LaunchedEffect(file) {
                    snackBarHostState.showSnackbar(
                        message = "File processing finished",
                        actionLabel = "Ok",
                        withDismissAction = true
                    )
                }
            if (exception != null)
                LaunchedEffect(exception) {
                    snackBarHostState.showSnackbar(
                        message = "Error to process the file",
                        actionLabel = "Ok",
                        withDismissAction = true
                    )
                }
        }
    }
}
