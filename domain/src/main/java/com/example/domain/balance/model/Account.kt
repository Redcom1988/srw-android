package com.example.domain.balance.model

import java.time.LocalDateTime

data class Account(
    val id: String,
    val name: String,
    val balance: Double,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)