package com.theost.workchat.network.api

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(email: String, apiKey: String) : Interceptor {

    private val credential: String = Credentials.basic(email, apiKey)

    override fun intercept(chain: Interceptor.Chain): Response {
        val authenticatedRequest = chain.request().newBuilder()
            .header("Authorization", credential)
            .build()
        return chain.proceed(authenticatedRequest)
    }

}