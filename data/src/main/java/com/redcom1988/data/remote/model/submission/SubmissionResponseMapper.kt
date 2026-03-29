package com.redcom1988.data.remote.model.submission

import com.redcom1988.domain.submission.model.Submission
import com.redcom1988.domain.submission.model.SubmissionImage
import com.redcom1988.domain.submission.model.SubmissionImageMetadata
import com.redcom1988.domain.submission.model.SubmissionStatus
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun SubmissionResponse.toDomain(): Submission {
    return Submission(
        id = id,
        agentName = agentName,
        status = status.toSubmissionStatus(),
        rejectionReason = rejectionReason,
        submissionAddress = submissionAddress,
        submissionLatitude = submissionLatitude,
        submissionLongitude = submissionLongitude,
        totalPoints = totalPoints,
        images = images?.map { it.toDomain() },
        createdAt = createdAt,
        updatedAt = updatedAt,
        processedAt = processedAt,
        reviewedAt = reviewedAt,
        assignedAt = assignedAt,
        pickedUpAt = pickedUpAt,
    )
}

@OptIn(ExperimentalTime::class)
fun SubmissionImageResponse.toDomain(): SubmissionImage {
    return SubmissionImage(
        id = id,
        url = url,
        metadata = metadata?.map { it.toDomain() },
        createdAt = createdAt,
    )
}

fun SubmissionImageMetadataResponse.toDomain(): SubmissionImageMetadata {
    return SubmissionImageMetadata(
        id = id,
        trashType = trashType,
        amount = amount,
        points = points
    )
}

private fun String.toSubmissionStatus(): SubmissionStatus {
    return when (this.uppercase()) {
        "PENDING" -> SubmissionStatus.PENDING
        "ML_PROCESSING" -> SubmissionStatus.ML_PROCESSING
        "AWAITING_REVIEW" -> SubmissionStatus.AWAITING_REVIEW
        "APPROVED" -> SubmissionStatus.APPROVED
        "REJECTED" -> SubmissionStatus.REJECTED
        "ASSIGNED" -> SubmissionStatus.ASSIGNED
        "PICKED_UP" -> SubmissionStatus.PICKED_UP
        "COMPLETED" -> SubmissionStatus.COMPLETED
        else -> SubmissionStatus.PENDING // Default fallback
    }
}

