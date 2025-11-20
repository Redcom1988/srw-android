package com.redcom1988.srw.di

import android.content.Context
import com.redcom1988.core.network.NetworkHelper
import com.redcom1988.core.preference.AndroidPreferenceStore
import com.redcom1988.core.preference.PreferenceStore
import com.redcom1988.core.util.ToastHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single { ToastHelper(androidContext()) }
    single { NetworkHelper(androidContext(), false) }
    single<PreferenceStore> {
        AndroidPreferenceStore(
            androidContext().getSharedPreferences("app_pref", Context.MODE_PRIVATE)
        )
    }

}