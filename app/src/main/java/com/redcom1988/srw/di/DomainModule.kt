package com.redcom1988.srw.di

import com.redcom1988.data.repository.AuthRepositoryImpl
import com.redcom1988.data.repository.ClientRepositoryImpl
import com.redcom1988.domain.auth.interactor.Login
import com.redcom1988.domain.auth.interactor.Logout
import com.redcom1988.domain.auth.interactor.RefreshToken
import com.redcom1988.domain.auth.repository.AuthRepository
import com.redcom1988.domain.client.repository.ClientRepository
import com.redcom1988.domain.preference.ApplicationPreference
import org.koin.dsl.module

val domainModule = module {
    single { ApplicationPreference(get()) }
    single<ClientRepository> { ClientRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    single { Login(get(), get()) }
    single { Logout(get(), get()) }
    single { RefreshToken(get(), get()) }
}