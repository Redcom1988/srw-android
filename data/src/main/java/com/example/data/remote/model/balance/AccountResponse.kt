package com.example.data.remote.model.balance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountResponse(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("balance") val balance: Double,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)