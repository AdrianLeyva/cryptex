package com.viacce.cryptex.crypto.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.viacce.core.exceptions.DecryptionException
import com.viacce.core.exceptions.EncryptionException

@Composable
fun CryptoErrorScreen(
    exception: Exception,
    snackBarHostState: SnackbarHostState
) {
    val message = when (exception) {
        is EncryptionException -> ""
        is DecryptionException -> ""
        else -> "Error to process the file."
    }
    LaunchedEffect(exception) {
        snackBarHostState.showSnackbar(
            message = message,
            actionLabel = "Ok",
            withDismissAction = true
        )
    }
}
