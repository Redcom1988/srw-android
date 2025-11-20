package com.example.srw.di

import com.example.data.repository.AccountRepositoryImpl
import com.example.domain.balance.repository.AccountRepository
import com.example.domain.preference.ApplicationPreference
import org.koin.dsl.module

val domainModule = module {
    single { ApplicationPreference(get()) }
    single<AccountRepository> { AccountRepositoryImpl(get()) }
}