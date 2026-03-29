package com.redcom1988.domain.submission.model

enum class SubmissionStatus {
    PENDING,
    ML_PROCESSING,
    AWAITING_REVIEW,
    APPROVED,
    REJECTED,
    ASSIGNED,
    PICKED_UP,
    COMPLETED
}