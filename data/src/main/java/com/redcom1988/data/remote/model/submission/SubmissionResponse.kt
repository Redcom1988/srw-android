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
    val submissionAddress: String?,
    val submissionLatitude: Float?,
    val submissionLongitude: Float?,
    val totalPoints: Int?,
    val images: List<SubmissionImageResponse>?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val processedAt: Instant?,
    val reviewedAt: Instant?,
    val assignedAt: Instant?,
    val pickedUpAt: Instant?,
)