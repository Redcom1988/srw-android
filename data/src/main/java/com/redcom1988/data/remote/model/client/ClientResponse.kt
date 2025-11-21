package com.redcom1988.data.remote.model.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientResponse(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)