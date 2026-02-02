package com.redcom1988.srw.base

import android.app.Application
import android.util.Log
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.redcom1988.core.network.NetworkHelper
import okhttp3.OkHttpClient
import com.redcom1988.srw.di.coreModule
import com.redcom1988.srw.di.dataModule
import com.redcom1988.srw.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.koin.core.qualifier.named

class MainApplication: Application(), SingletonImageLoader.Factory, KoinComponent {
    private val networkHelper: NetworkHelper by inject(named("authenticated"))

    private val imageLoaderClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cache(networkHelper.client.cache)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            logger(
                object: Logger() {
                    override fun display(level: Level, msg: MESSAGE) {
                        when (level) {
                            Level.DEBUG -> Log.d(null, msg)
                            Level.INFO -> Log.i(null, msg)
                            Level.WARNING -> Log.w(null, msg)
                            Level.ERROR -> Log.e(null, msg)
                            Level.NONE -> Log.v(null, msg)
                        }
                    }
                }
            )
            androidContext(this@MainApplication)
            modules(
                listOf(
                    coreModule,
                    dataModule,
                    domainModule,
                )
            )
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory(imageLoaderClient))
            }
            .crossfade(true)
            .build()
    }
}