package com.redcom1988.domain.client.model

import java.time.LocalDateTime

data class Client(
    val id: Int,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)