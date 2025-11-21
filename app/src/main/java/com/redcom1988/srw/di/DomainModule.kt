package com.redcom1988.srw.di

import com.redcom1988.data.repository.ClientRepositoryImpl
import com.redcom1988.domain.client.repository.ClientRepository
import com.redcom1988.domain.preference.ApplicationPreference
import org.koin.dsl.module

val domainModule = module {
    single { ApplicationPreference(get()) }
    single<ClientRepository> { ClientRepositoryImpl(get()) }
}