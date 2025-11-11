package com.example.domain.balance.interactor

import com.example.domain.balance.model.Account
import com.example.domain.balance.repository.AccountRepository
import kotlinx.coroutines.flow.Flow

class GetAccount(
    private val accountRepository: AccountRepository
) {

    fun subscribeSingle(
        accountId: String
    ): Flow<Account?> {
        return accountRepository.subscribe(accountId)
    }

}