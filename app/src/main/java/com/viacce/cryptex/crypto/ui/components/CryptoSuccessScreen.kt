package com.viacce.cryptex.crypto.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import java.io.File

@Composable
fun CryptoSuccessScreen(
    file: File,
    snackBarHostState: SnackbarHostState
) {
    LaunchedEffect(file) {
        snackBarHostState.showSnackbar(
            message = "File processed successfully",
            actionLabel = "Ok",
            withDismissAction = true
        )
    }
}
