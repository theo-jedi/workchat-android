package com.theost.workchat.di.base

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.theost.workchat.network.api.Api
import com.theost.workchat.network.api.ApiConfig
import com.theost.workchat.network.api.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

@Module
class NetworkModule {

    private val json = Json { ignoreUnknownKeys = true }

    @Provides
    @Reusable
    fun provideMessengerApi(retrofit: Retrofit): Api {
        return retrofit.create(Api::class.java)
    }

    @Provides
    @Reusable
    fun provideRetrofitClient(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConfig.DOMAIN)
            .client(httpClient)
            .addConverterFactory(
                @OptIn(ExperimentalSerializationApi::class)
                json.asConverterFactory("application/json".toMediaType())
            )
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Reusable
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(AuthInterceptor(ApiConfig.AUTH_EMAIL, ApiConfig.AUTH_API_KEY))
            .connectTimeout(ApiConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(ApiConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Reusable
    fun provideMessengerUrl(): String = ApiConfig.DOMAIN
}