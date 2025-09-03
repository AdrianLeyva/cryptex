package com.viacce.cryptex.crypto.ui.components

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun CryptoSuccessScreen(
    uri: Uri,
    snackBarHostState: SnackbarHostState
) {
    LaunchedEffect(uri) {
        snackBarHostState.showSnackbar(
            message = "File processed successfully",
            actionLabel = "Ok",
            withDismissAction = true
        )
    }
}
