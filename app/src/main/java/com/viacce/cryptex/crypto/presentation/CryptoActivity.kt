package com.viacce.cryptex.crypto.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.viacce.ui.theme.CryptexTheme

class CryptoActivity : ComponentActivity() {

    private val viewModel = CryptoViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptexTheme {
                val cryptoUiModel by viewModel.cryptoUiModelState.collectAsState()
                CryptoScreen(
                    cryptoUiModel = cryptoUiModel,
                    onEncryptFile = {
                        viewModel.encryptFile(it, "cryptex")
                    },
                    onDecryptFile = {
                        viewModel.decryptFile(it, "cryptex")
                    }
                )
            }
        }
    }
}
