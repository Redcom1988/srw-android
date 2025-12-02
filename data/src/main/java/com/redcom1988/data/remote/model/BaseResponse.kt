package com.redcom1988.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val success: Boolean,
    val code: Int,
    val message: String? = null,
    val data: T
)