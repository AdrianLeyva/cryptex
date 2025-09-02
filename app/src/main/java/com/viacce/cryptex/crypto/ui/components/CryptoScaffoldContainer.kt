package com.viacce.cryptex.crypto.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.viacce.cryptex.R
import com.viacce.ui.components.CryptexButton

@Composable
fun CryptoScaffoldContainer(
    modifier: Modifier = Modifier,
    onEncryptListener: () -> Unit,
    onDecryptListener: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = context.getString(R.string.app_name),
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
                onEncryptListener()
            }
            CryptexButton(
                text = context.getString(R.string.decrypt),
                icon = Icons.Default.Lock,
                modifier = Modifier.fillMaxWidth()
            ) {
                onDecryptListener()
            }
        }
    }
}
