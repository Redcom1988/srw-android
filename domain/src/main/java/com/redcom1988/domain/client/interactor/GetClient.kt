package com.redcom1988.domain.client.interactor

import com.redcom1988.domain.client.model.Client
import com.redcom1988.domain.client.repository.ClientRepository
import kotlinx.coroutines.flow.Flow

class GetClient(
    private val clientRepository: ClientRepository
) {

    fun subscribeSingle(
        accountId: String
    ): Flow<Client?> {
        return clientRepository.subscribe(accountId)
    }

}