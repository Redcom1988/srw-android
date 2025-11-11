package com.example.srw.di

import com.example.data.repository.AccountRepositoryImpl
import com.example.domain.balance.repository.AccountRepository
import org.koin.dsl.module

val domainModule = module {
    single<AccountRepository> { AccountRepositoryImpl(get(), get()) }
}