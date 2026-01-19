package com.redcom1988.data.remote.model.submission

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@OptIn(ExperimentalTime::class)
data class SubmissionImageResponse(
    val id: String,
    val url: String,
    val metadata: List<SubmissionImageMetadataResponse>?,
    val createdAt: Instant
)