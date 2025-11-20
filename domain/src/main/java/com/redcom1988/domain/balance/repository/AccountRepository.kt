package com.redcom1988.domain.balance.repository

import com.redcom1988.domain.balance.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    fun subscribe(accountId: String): Flow<Account?>

    suspend fun deleteAll()

}