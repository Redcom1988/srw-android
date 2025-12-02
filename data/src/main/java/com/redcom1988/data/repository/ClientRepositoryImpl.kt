package com.redcom1988.data.repository

import com.redcom1988.data.remote.SRWApi
import com.redcom1988.data.remote.model.client.toDomain
import com.redcom1988.domain.client.model.Client
import com.redcom1988.domain.client.repository.ClientRepository

class ClientRepositoryImpl(
    private val api: SRWApi
): ClientRepository {

    override suspend fun fetchClientProfile(): Client {
        val response = api.getClientProfile()

        if (response.error != null) {
            throw Exception(response.error)
        }

        if (response.success != true) {
            throw Exception("Logout failed: ${response.message ?: "Unknown error"}")
        }

        val data = response.data ?: throw Exception("No data received")
        return data.toDomain()
    }
}