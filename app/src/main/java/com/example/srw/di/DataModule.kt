package com.example.srw.di

import com.example.data.remote.SRWApi
import org.koin.dsl.module

val dataModule = module {
    single<SRWApi> { SRWApi(get(), get()) }
}