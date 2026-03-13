package com.labguardian.core.network.di

import com.labguardian.core.network.LabGuardianApi
import com.labguardian.core.network.OkHttpStationWebSocket
import com.labguardian.core.network.StationWebSocket
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        moshi: Moshi,
        @Named("serverUrl") serverUrl: String,
    ): Retrofit = Retrofit.Builder()
        .baseUrl("$serverUrl/api/v1/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideLabGuardianApi(retrofit: Retrofit): LabGuardianApi =
        retrofit.create(LabGuardianApi::class.java)

    @Provides
    @Singleton
    fun provideStationWebSocket(
        client: OkHttpClient,
        @Named("serverUrl") serverUrl: String,
    ): StationWebSocket {
        val wsUrl = serverUrl.replace("http://", "ws://").replace("https://", "wss://")
        return OkHttpStationWebSocket(client, wsUrl)
    }
}
