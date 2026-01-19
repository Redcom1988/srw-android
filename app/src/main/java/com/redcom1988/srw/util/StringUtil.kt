package com.redcom1988.srw.util

import com.redcom1988.domain.submission.model.SubmissionStatus

/**
 * Maps submission status to a human-readable string
 */
fun SubmissionStatus.toReadableStatus(): String {
    return when (this) {
        SubmissionStatus.PENDING -> "Pending"
        SubmissionStatus.ML_PROCESSING -> "Processing"
        SubmissionStatus.AWAITING_REVIEW -> "Awaiting Review"
        SubmissionStatus.APPROVED -> "Approved"
        SubmissionStatus.REJECTED -> "Rejected"
        SubmissionStatus.ASSIGNED -> "Assigned"
        SubmissionStatus.PICKED_UP -> "Picked Up"
        SubmissionStatus.COMPLETED -> "Completed"
    }
}

/**
 * Checks if the submission status is in a pre-review state
 * (where points are subject to change)
 */
fun SubmissionStatus.isPreReviewStatus(): Boolean {
    return this in listOf(SubmissionStatus.PENDING, SubmissionStatus.ML_PROCESSING, SubmissionStatus.AWAITING_REVIEW)
}

/**
 * Checks if the submission status is a terminal state
 * (no further transitions possible)
 */
fun SubmissionStatus.isTerminalStatus(): Boolean {
    return this in listOf(SubmissionStatus.REJECTED, SubmissionStatus.COMPLETED)
}

