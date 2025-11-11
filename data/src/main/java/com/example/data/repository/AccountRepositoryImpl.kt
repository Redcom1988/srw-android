package com.example.data.repository

import com.example.data.local.SRWDatabase
import com.example.data.local.dao.AccountDao
import com.example.data.local.entity.account.toDomain
import com.example.data.remote.SRWApi
import com.example.domain.balance.model.Account
import com.example.domain.balance.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class AccountRepositoryImpl(
    private val api: SRWApi,
    private val database: SRWDatabase
): AccountRepository {

    private val accountDao: AccountDao = database.accountDao()

    override fun subscribe(accountId: String): Flow<Account> {
        return accountDao.subscribeSingle(accountId)
            .map { it.toDomain() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun deleteAll() {
        return accountDao.deleteAll()
    }

}