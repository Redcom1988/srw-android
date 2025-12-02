package com.redcom1988.srw.util

/**
 * Maps submission status to a human-readable string
 */
fun String.toReadableStatus(): String {
    return when (this) {
        "PENDING" -> "Pending"
        "ML_PROCESSING" -> "Processing"
        "AWAITING_REVIEW" -> "Awaiting Review"
        "APPROVED" -> "Approved"
        "REJECTED" -> "Rejected"
        "ASSIGNED" -> "Assigned"
        "PICKED_UP" -> "Picked Up"
        "COMPLETED" -> "Completed"
        else -> this.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
    }
}

/**
 * Checks if the submission status is in a pre-review state
 * (where points are subject to change)
 */
fun String.isPreReviewStatus(): Boolean {
    return this in listOf("PENDING", "ML_PROCESSING", "AWAITING_REVIEW")
}

/**
 * Checks if the submission status is a terminal state
 * (no further transitions possible)
 */
fun String.isTerminalStatus(): Boolean {
    return this in listOf("REJECTED", "COMPLETED")
}

