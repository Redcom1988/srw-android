package com.redcom1988.srw.di

import com.redcom1988.data.remote.SRWApi
import com.redcom1988.data.repository.AuthRepositoryImpl
import com.redcom1988.data.repository.ClientRepositoryImpl
import com.redcom1988.data.repository.PointRepositoryImpl
import com.redcom1988.data.repository.SubmissionRepositoryImpl
import com.redcom1988.domain.auth.repository.AuthRepository
import com.redcom1988.domain.client.repository.ClientRepository
import com.redcom1988.domain.point.repository.PointRepository
import com.redcom1988.domain.submission.repository.SubmissionRepository
import org.koin.dsl.module

val dataModule = module {
    single<SRWApi> { SRWApi(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<ClientRepository> { ClientRepositoryImpl(get()) }
    single<PointRepository> { PointRepositoryImpl(get()) }
    single<SubmissionRepository> { SubmissionRepositoryImpl(get()) }
}