package com.redcom1988.domain.client.interactor

import com.redcom1988.domain.client.model.Client
import com.redcom1988.domain.client.repository.ClientRepository

class GetClientProfile(
    private val clientRepository: ClientRepository
) {

    suspend fun await(): Result {
        return try {
            val client = clientRepository.fetchClientProfile()
            Result.Success(client)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    sealed interface Result {
        data class Success(val client: Client) : Result
        data class Error(val error: Throwable) : Result
    }

}