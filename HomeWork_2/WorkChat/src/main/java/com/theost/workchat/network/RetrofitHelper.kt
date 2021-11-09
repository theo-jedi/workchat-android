package com.theost.workchat.network

object RetrofitHelper {
    private const val BASE_URL = "https://tinkoff-android-fall21.zulipchat.com/api/v1/"

    private const val DEFAULT_EMAIL = "feds.msc@gmail.com"
    private const val DEFAULT_API_KEY = "rd3KQacRLprtLaIE4agsL1IbHDpwRhFn"

    val retrofitService: Api
        get() = RetrofitClient.getClient(BASE_URL, DEFAULT_EMAIL, DEFAULT_API_KEY).create(Api::class.java)
}