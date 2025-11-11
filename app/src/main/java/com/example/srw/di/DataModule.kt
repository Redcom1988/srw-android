package com.example.srw.di

import androidx.room.Room
import com.example.data.local.SRWDatabase
import com.example.data.remote.SRWApi
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<SRWApi> { SRWApi(get(), get()) }
    single<SRWDatabase> {
        Room
            .databaseBuilder(
                androidContext(),
                SRWDatabase::class.java,
                "srw_db"
            )
            .fallbackToDestructiveMigration(true)
            .build()
    }
}