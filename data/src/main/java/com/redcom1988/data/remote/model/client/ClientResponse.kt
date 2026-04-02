package com.redcom1988.data.remote.model.client

import kotlinx.serialization.Serializable

@Serializable
data class ClientResponse(
    val id: Int,
    val nfc: String,
    val name: String,
    val address: String = "",
    val latitude: Float? = null,
    val longitude: Float? = null,
    val totalPoints: Int
)