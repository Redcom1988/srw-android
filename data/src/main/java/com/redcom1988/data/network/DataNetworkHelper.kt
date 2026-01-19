package com.redcom1988.data.network

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.redcom1988.core.network.NetworkHelper
import com.redcom1988.core.network.NetworkPreference
import com.redcom1988.core.network.interceptor.AuthInterceptor
import com.redcom1988.core.network.interceptor.IgnoreGzipInterceptor
import com.redcom1988.core.network.interceptor.UncaughtExceptionInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.brotli.BrotliInterceptor
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

class DataNetworkHelper(
    private val networkHelper: NetworkHelper,
    private val context: Context,
    private val isDebugBuild: Boolean,
    private val preference: NetworkPreference,
): NetworkHelper(context, isDebugBuild, preference) {

    override val client: OkHttpClient = run {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .callTimeout(2, TimeUnit.MINUTES)
            .cache(
                Cache(
                    directory = File(context.cacheDir, "network_cache"),
                    maxSize = 5L * 1024 * 1024, // 5 MiB
                ),
            )
            .addInterceptor(UncaughtExceptionInterceptor())
            .addInterceptor(AuthInterceptor(preference))
            .addNetworkInterceptor(IgnoreGzipInterceptor())
            .addNetworkInterceptor(BrotliInterceptor)
            .authenticator(TokenAuthenticator(networkHelper, preference))


        if (isDebugBuild) {
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addNetworkInterceptor(httpLoggingInterceptor)
            builder.addInterceptor(ChuckerInterceptor(context))
        }

        builder.build()
    }

}