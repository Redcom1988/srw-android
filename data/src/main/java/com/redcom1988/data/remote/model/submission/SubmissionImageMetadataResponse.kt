package com.redcom1988.data.remote.model.submission

import kotlinx.serialization.Serializable

@Serializable
data class SubmissionImageMetadataResponse(
    val id: Int,
    val trashType: String,
    val amount: Int,
    val points: Int
)