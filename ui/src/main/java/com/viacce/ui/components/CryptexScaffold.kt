package com.viacce.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.viacce.ui.theme.ElectricRadialGradient
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptexScaffold(
    title: String = "",
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
    snackBarHostState: SnackbarHostState,
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            if (title.isNotEmpty())
                TopAppBar(
                    title = {
                        Text(
                            text = title.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                            },
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    navigationIcon = {
                        if (navigationIcon != null && onNavigationClick != null) {
                            IconButton(onClick = onNavigationClick) {
                                Icon(imageVector = navigationIcon, contentDescription = null)
                            }
                        }
                    },
                    actions = { actions?.invoke() }
                )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .background(ElectricRadialGradient)
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            content()
        }
    }
}
