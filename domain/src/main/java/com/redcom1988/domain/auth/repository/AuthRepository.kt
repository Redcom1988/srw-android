package com.redcom1988.domain.auth.repository

import com.redcom1988.domain.auth.model.AuthToken

interface AuthRepository {
    suspend fun login(nfcNumber: String): AuthToken
}