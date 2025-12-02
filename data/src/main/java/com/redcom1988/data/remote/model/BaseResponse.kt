package com.redcom1988.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val success: Boolean? = null,
    val code: Int? = null,
    val message: String? = null,
    val data: T? = null,
    val error: String? = null
)