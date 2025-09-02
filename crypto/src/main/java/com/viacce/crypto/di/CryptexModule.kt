package com.viacce.crypto.di

import android.content.Context
import com.viacce.crypto.algorithm.AESCryptexAlgorithm
import com.viacce.crypto.algorithm.ICryptexAlgorithm
import com.viacce.crypto.data.CryptexRepository
import com.viacce.crypto.domain.DecryptFileUseCase
import com.viacce.crypto.domain.EncryptFileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CryptexModule {

    @Provides
    @Singleton
    fun provideAESCryptoAlgorithm(): ICryptexAlgorithm = AESCryptexAlgorithm()

    @Provides
    @Singleton
    fun provideCryptexRepository(
        @ApplicationContext context: Context,
        cryptexAlgorithm: ICryptexAlgorithm
    ) = CryptexRepository(context, cryptexAlgorithm)

    @Provides
    @Singleton
    fun provideEncryptFileUseCase(
        cryptexRepository: CryptexRepository
    ) = EncryptFileUseCase(cryptexRepository)

    @Provides
    @Singleton
    fun provideDecryptFileUseCase(
        cryptexRepository: CryptexRepository
    ) = DecryptFileUseCase(cryptexRepository)
}
