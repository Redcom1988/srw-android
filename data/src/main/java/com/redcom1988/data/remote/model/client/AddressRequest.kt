package com.redcom1988.data.remote.model.client

import kotlinx.serialization.Serializable

@Serializable
class AddressRequest(
    val address: String? = null,
    val latitude: Float? = null,
    val longitude: Float? = null
)