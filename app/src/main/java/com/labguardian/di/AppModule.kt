package com.labguardian.di

import com.labguardian.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("serverUrl")
    fun provideServerUrl(): String = BuildConfig.SERVER_URL
}
