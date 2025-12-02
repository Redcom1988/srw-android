package com.redcom1988.srw.di

import com.redcom1988.data.repository.AuthRepositoryImpl
import com.redcom1988.data.repository.ClientRepositoryImpl
import com.redcom1988.data.repository.PointRepositoryImpl
import com.redcom1988.data.repository.SubmissionRepositoryImpl
import com.redcom1988.domain.auth.interactor.Login
import com.redcom1988.domain.auth.interactor.Logout
import com.redcom1988.domain.auth.interactor.RefreshToken
import com.redcom1988.domain.auth.repository.AuthRepository
import com.redcom1988.domain.client.interactor.GetClientProfile
import com.redcom1988.domain.client.repository.ClientRepository
import com.redcom1988.domain.point.interactor.GetProfilePoints
import com.redcom1988.domain.point.repository.PointRepository
import com.redcom1988.domain.preference.ApplicationPreference
import com.redcom1988.domain.submission.interactor.GetRecentSubmissions
import com.redcom1988.domain.submission.interactor.GetSubmissions
import com.redcom1988.domain.submission.interactor.UploadSubmission
import com.redcom1988.domain.submission.repository.SubmissionRepository
import org.koin.dsl.module

val domainModule = module {
    single { ApplicationPreference(get()) }

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single { Login(get(), get()) }
    single { Logout(get(), get()) }
    single { RefreshToken(get(), get()) }


    single<ClientRepository> { ClientRepositoryImpl(get()) }
    single { GetClientProfile(get()) }

    single<PointRepository> { PointRepositoryImpl(get()) }
    single { GetProfilePoints(get()) }

    single<SubmissionRepository> { SubmissionRepositoryImpl(get()) }
    single { GetSubmissions(get()) }
    single { GetRecentSubmissions(get()) }
    single { UploadSubmission(get()) }

}