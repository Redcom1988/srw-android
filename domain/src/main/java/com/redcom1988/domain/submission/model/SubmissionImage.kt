package com.redcom1988.domain.submission.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class SubmissionImage(
    val id: String,
    val url: String,
    val metadata: List<SubmissionImageMetadata>?,
    val createdAt: Instant
)