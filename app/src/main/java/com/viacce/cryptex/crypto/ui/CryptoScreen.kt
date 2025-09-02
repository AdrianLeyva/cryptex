package com.viacce.cryptex.crypto.ui

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
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
    modifier: Modifier = Modifier,
    cryptoUiModel: CryptoUiModel,
    onEncryptFile: (ByteArray, String, String) -> Unit,
    onDecryptFile: (File, String) -> Unit
) {
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    val cryptexDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
        "Cryptex"
    )
    if (!cryptexDir.exists()) cryptexDir.mkdirs()


    val encryptLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val inputStream = context.contentResolver.openInputStream(selectedUri) ?: return@let
            val data = inputStream.readBytes()
            val fileName = getFileNameFromUri(context, selectedUri)
            onEncryptFile(data, fileName, "cryptex")
        }
    }

    val decryptLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val inputStream = context.contentResolver.openInputStream(selectedUri) ?: return@let
            val tempFile = File(context.cacheDir, "temp_${System.currentTimeMillis()}.cryptex")
            tempFile.outputStream().use { output -> inputStream.copyTo(output) }
            onDecryptFile(tempFile, "cryptex")
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

fun getFileNameFromUri(context: Context, uri: Uri): String {
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && nameIndex != -1) {
            return cursor.getString(nameIndex)
        }
    }
    return "file_${System.currentTimeMillis()}"
}
