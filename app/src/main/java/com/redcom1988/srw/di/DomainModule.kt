package com.redcom1988.srw.di

import com.redcom1988.data.repository.AccountRepositoryImpl
import com.redcom1988.domain.balance.repository.AccountRepository
import com.redcom1988.domain.preference.ApplicationPreference
import org.koin.dsl.module

val domainModule = module {
    single { ApplicationPreference(get()) }
    single<AccountRepository> { AccountRepositoryImpl(get()) }
}