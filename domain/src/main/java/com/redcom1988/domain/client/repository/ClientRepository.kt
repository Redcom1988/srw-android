package com.redcom1988.domain.client.repository

import com.redcom1988.domain.client.model.Client
import kotlinx.coroutines.flow.Flow

interface ClientRepository {

    fun subscribe(clientId: Int): Flow<Client?>

}