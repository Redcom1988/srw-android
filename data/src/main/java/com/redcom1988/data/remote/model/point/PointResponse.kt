package com.redcom1988.data.remote.model.point

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PointResponse(
    @SerialName("id") val id: Int,
    @SerialName("client_id") val clientId: Int,
    @SerialName("value") val value: Int,
    @SerialName("reason") val reason: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)