package com.viacce.cryptex.crypto.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.viacce.ui.theme.CryptexTheme

class CryptoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptexTheme {
                CryptoScreen(
                    onEncryptFile = {},
                    onDecryptFile = { }
                )
            }
        }
    }
}
