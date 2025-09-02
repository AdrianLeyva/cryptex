package com.viacce.core.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

private const val CRYPTEX_PREFERENCES = "cryptex_preferences"

val Context.dataStore by preferencesDataStore(name = CRYPTEX_PREFERENCES)
