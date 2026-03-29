package com.redcom1988.domain.client.model

import java.time.LocalDateTime

data class Client(
    val id: Int,
    val nfc: String,
    val name: String,
    val address: String,
    val latitude: Float,
    val longitude: Float,
    val totalPoints: Int
)