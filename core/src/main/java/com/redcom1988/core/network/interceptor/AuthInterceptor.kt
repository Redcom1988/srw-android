package com.redcom1988.core.network.interceptor

import com.redcom1988.core.network.NetworkPreference
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val preference: NetworkPreference
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = preference.accessToken().get()
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(request)
    }
}