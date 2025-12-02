package com.redcom1988.data.remote.model.point

import kotlinx.serialization.Serializable

@Serializable
data class PointResponse(
    val id: Int,
    val submissionId: Int,
    val amount: Int,
    val createdAt: String,
)