package com.example.srw.di

import android.content.Context
import com.example.core.network.NetworkHelper
import com.example.core.preference.AndroidPreferenceStore
import com.example.core.preference.PreferenceStore
import com.example.core.util.ToastHelper
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