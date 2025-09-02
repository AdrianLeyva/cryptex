package com.viacce.cryptex.crypto.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.viacce.core.exceptions.DecryptionException
import com.viacce.core.exceptions.EncryptionException
import com.viacce.cryptex.R

@Composable
fun CryptoErrorScreen(
    exception: Exception,
    snackBarHostState: SnackbarHostState
) {
    val message = when (exception) {
        is EncryptionException -> stringResource(R.string.error_encryption)
        is DecryptionException -> stringResource(R.string.error_decryption)
        else -> stringResource(R.string.error_generic)
    }
    LaunchedEffect(exception) {
        snackBarHostState.showSnackbar(
            message = message,
            actionLabel = "Ok",
            withDismissAction = true
        )
    }
}
