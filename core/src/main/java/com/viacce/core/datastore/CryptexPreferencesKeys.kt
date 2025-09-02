package com.viacce.core.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey

object CryptexPreferencesKeys {
    val IS_CRYPTEX_DIRECTORY_PERMISSION_GRANTED =
        booleanPreferencesKey("is_cryptex_directory_permission_granted")
}
