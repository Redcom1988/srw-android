package com.example.data.repository

import com.example.data.remote.SRWApi
import com.example.domain.balance.model.Account
import com.example.domain.balance.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AccountRepositoryImpl(
    @Suppress("unused") // Will be used when API is implemented
    private val api: SRWApi
): AccountRepository {

    // Fetch data from server only - no local database
    override fun subscribe(accountId: String): Flow<Account?> {
        return flow {
            while (true) {
                try {
                    // TODO: Implement actual API call to fetch account data
                    // val account = api.getAccount(accountId)
                    // emit(account)
                    emit(null)
                } catch (_: Exception) {
                    emit(null)
                }
                delay(30000) // Poll every 30 seconds, adjust as needed
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun deleteAll() {
        // No local data to delete - this is a no-op now
        // Could be used to clear any in-memory cache if needed
    }

}