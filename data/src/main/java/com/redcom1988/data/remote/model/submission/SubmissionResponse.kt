package com.redcom1988.data.remote.model.submission

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@OptIn(ExperimentalTime::class)
data class SubmissionResponse(
    val id: Int,
    val clientId: Int?,
    val clientName: String?,
    val agentId: Int?,
    val agentName: String?,
    val status: String,
    val rejectionReason: String?,
    val adminNotes: String?,
    val pickupLocation: String?,
    val totalPoints: Int?,
    val imageCount: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
    val processedAt: Instant?,
    val reviewedAt: Instant?,
    val assignedAt: Instant?,
    val pickedUpAt: Instant?,
)