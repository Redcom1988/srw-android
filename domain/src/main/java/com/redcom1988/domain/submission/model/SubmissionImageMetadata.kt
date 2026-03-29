package com.redcom1988.domain.submission.model

import java.io.Serializable

data class SubmissionImageMetadata(
    val id: Int,
    val trashType: String,
    val amount: Int,
    val points: Int
) : Serializable