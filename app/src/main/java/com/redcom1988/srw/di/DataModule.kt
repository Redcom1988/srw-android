package com.redcom1988.srw.di

import com.redcom1988.data.remote.SRWApi
import org.koin.dsl.module

val dataModule = module {
    single<SRWApi> { SRWApi(get(), get()) }
}