package com.theost.workchat.network.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null
    private val json = Json { ignoreUnknownKeys = true }

    fun getClient(baseUrl: String, email: String, apiKey: String): Retrofit {
        if (retrofit == null) {
            val contentType = "application/json".toMediaType()

            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(email, apiKey))
                .addNetworkInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(
                    @OptIn(ExperimentalSerializationApi::class)
                    json.asConverterFactory(contentType)
                )
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
        }
        return retrofit!!
    }
}