package com.redcom1988.domain.client.repository

import com.redcom1988.domain.client.model.Client
import kotlinx.coroutines.flow.Flow

interface ClientRepository {

    suspend fun fetchClientProfile(): Client

}