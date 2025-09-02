package com.viacce.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CryptexPreferencesManager(private val context: Context) {

    fun saveCryptexDirectoryPermissionGranted() {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit { preferences ->
                preferences[CryptexPreferencesKeys.IS_CRYPTEX_DIRECTORY_PERMISSION_GRANTED] = true
            }
        }
    }

    fun isCryptexDirectoryPermissionGranted(): Flow<Boolean> {
        return context.dataStore.data
            .map { preferences ->
                preferences[CryptexPreferencesKeys.IS_CRYPTEX_DIRECTORY_PERMISSION_GRANTED] ?: false
            }
    }
}
