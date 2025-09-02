package com.viacce.core.di

import android.content.Context
import com.viacce.core.datastore.CryptexPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CryptexPreferencesModule {

    @Provides
    @Singleton
    fun provideCryptexPreferencesManager(
        @ApplicationContext context: Context,
    ) = CryptexPreferencesManager(context)
}
