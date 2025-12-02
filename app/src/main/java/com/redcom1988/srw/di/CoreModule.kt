package com.redcom1988.srw.di

import android.content.Context
import com.redcom1988.core.network.NetworkHelper
import com.redcom1988.core.preference.AndroidPreferenceStore
import com.redcom1988.core.preference.PreferenceStore
import com.redcom1988.core.util.ToastHelper
import com.redcom1988.data.network.AuthInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single { ToastHelper(androidContext()) }

    single { AuthInterceptor(get(), inject()) }
    single { NetworkHelper(androidContext(), false, listOf(get<AuthInterceptor>())) }

    single<PreferenceStore> {
        AndroidPreferenceStore(
            androidContext().getSharedPreferences("app_pref", Context.MODE_PRIVATE)
        )
    }

}