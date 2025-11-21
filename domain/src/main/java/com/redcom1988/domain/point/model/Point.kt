package com.redcom1988.domain.point.model

import java.time.LocalDateTime

data class Point(
    val id: Int,
    val clientId: Int,
    val value: Int,
    val reason: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)