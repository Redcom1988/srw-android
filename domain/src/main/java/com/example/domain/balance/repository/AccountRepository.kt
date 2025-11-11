package com.example.domain.balance.repository

import com.example.domain.balance.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    fun subscribe(accountId: String): Flow<Account>

    suspend fun deleteAll()

}