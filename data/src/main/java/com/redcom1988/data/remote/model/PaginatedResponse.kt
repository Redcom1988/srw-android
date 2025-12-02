package com.redcom1988.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    val data: List<T>,
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val totalPages: Int
)