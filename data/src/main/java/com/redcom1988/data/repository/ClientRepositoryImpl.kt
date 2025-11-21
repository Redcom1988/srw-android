package com.redcom1988.data.repository

import com.redcom1988.data.remote.SRWApi
import com.redcom1988.domain.client.model.Client
import com.redcom1988.domain.client.repository.ClientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ClientRepositoryImpl(
    private val api: SRWApi
): ClientRepository {

    override fun subscribe(clientId: Int): Flow<Client?> {
        return flow {
            while (true) {
                try {
                    // TODO: Implement actual API call to fetch account data
                    // val account = api.getAccount(clientId)
                    // emit(account)
                    emit(null)
                } catch (_: Exception) {
                    emit(null)
                }
                delay(30000) // Poll every 30 seconds, adjust as needed
            }
        }.flowOn(Dispatchers.IO)
    }
}