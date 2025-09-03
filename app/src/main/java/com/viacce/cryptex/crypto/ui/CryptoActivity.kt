package com.viacce.cryptex.crypto.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.viacce.ui.theme.CryptexTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CryptoActivity : ComponentActivity() {

    private val viewModel: CryptoViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptexTheme {
                CryptoScreen(viewModel = viewModel)
            }
        }
    }
}
