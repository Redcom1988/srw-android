package com.redcom1988.domain.point.model

import java.time.LocalDateTime

data class Point(
    val id: Int,
    val submissionId: Int,
    val amount: Int,
    val createdAt: LocalDateTime,
)