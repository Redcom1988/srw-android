package com.redcom1988.data.remote.model.submission

import com.redcom1988.domain.submission.model.Submission
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun SubmissionResponse.toDomain(): Submission {
    return Submission(
        id = id,
        agentName = agentName,
        status = status,
        rejectionReason = rejectionReason,
        totalPoints = totalPoints,
        imageCount = imageCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
        processedAt = processedAt,
        reviewedAt = reviewedAt,
        assignedAt = assignedAt,
        pickedUpAt = pickedUpAt,
    )
}