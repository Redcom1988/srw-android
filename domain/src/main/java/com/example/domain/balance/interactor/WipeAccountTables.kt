package com.example.domain.balance.interactor

import com.example.domain.balance.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WipeAccountTables(
    private val accountRepository: AccountRepository
) {

    suspend fun await() {
        withContext(Dispatchers.IO) {
            accountRepository.deleteAll()
        }
    }

}