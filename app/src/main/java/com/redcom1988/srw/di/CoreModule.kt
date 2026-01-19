package com.redcom1988.srw.di

import android.content.Context
import com.redcom1988.core.network.NetworkHelper
import com.redcom1988.core.network.NetworkPreference
import com.redcom1988.core.preference.AndroidPreferenceStore
import com.redcom1988.core.preference.PreferenceStore
import com.redcom1988.core.util.ToastHelper
import com.redcom1988.data.network.DataNetworkHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coreModule = module {
    single { ToastHelper(androidContext()) }

    single(named("default")) {
        NetworkHelper(
            context = androidContext(),
            isDebugBuild = true,
            networkPreference = get()
        )
    }

    single<NetworkHelper>(named("authenticated")) {
        DataNetworkHelper(
            networkHelper = get(named("default")),
            context = androidContext(),
            isDebugBuild = true,
            preference = get(),
        )
    }

    single { NetworkPreference(get()) }

    single<PreferenceStore> {
        AndroidPreferenceStore(
            androidContext().getSharedPreferences("app_pref", Context.MODE_PRIVATE)
        )
    }

}