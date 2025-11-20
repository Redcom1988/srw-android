package com.redcom1988.domain.balance.interactor

import com.redcom1988.domain.balance.model.Account
import com.redcom1988.domain.balance.repository.AccountRepository
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