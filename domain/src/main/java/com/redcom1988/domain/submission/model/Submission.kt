package com.redcom1988.domain.submission.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Submission(
    val id: Int,
//    val clientId: Int,
//    val clientName: String,
//    val agentId: Int,
    val agentName: String?,
    val status: SubmissionStatus,
    val rejectionReason: String?,
//    val adminNotes: String,
//    val pickupLocation: String,
    val totalPoints: Int?,
    val images: List<SubmissionImage>?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val processedAt: Instant?,
    val reviewedAt: Instant?,
    val assignedAt: Instant?,
    val pickedUpAt: Instant?,
)