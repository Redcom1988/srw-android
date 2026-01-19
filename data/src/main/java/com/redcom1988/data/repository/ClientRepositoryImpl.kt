package com.redcom1988.data.repository

import com.redcom1988.core.network.parseAs
import com.redcom1988.data.remote.SRWApi
import com.redcom1988.data.remote.model.BaseResponse
import com.redcom1988.data.remote.model.client.ClientResponse
import com.redcom1988.data.remote.model.client.toDomain
import com.redcom1988.domain.client.model.Client
import com.redcom1988.domain.client.repository.ClientRepository

class ClientRepositoryImpl(
    private val api: SRWApi
): ClientRepository {

    override suspend fun fetchClientProfile(): Client {
        val response = api.getClientProfile()
        val data = response.parseAs<BaseResponse<ClientResponse>>()

        if (data.success == false) {
            throw Exception(data.message ?: "Failed to fetch profile")
        }

        val clientData = data.data ?: throw Exception("No data received")
        return clientData.toDomain()
    }
}