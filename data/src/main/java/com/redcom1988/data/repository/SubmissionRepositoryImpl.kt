package com.redcom1988.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.redcom1988.data.remote.SRWApi
import com.redcom1988.data.remote.model.submission.toDomain
import com.redcom1988.data.remote.source.SubmissionsPagingSource
import com.redcom1988.domain.submission.model.Submission
import com.redcom1988.domain.submission.repository.SubmissionRepository

@OptIn(kotlin.time.ExperimentalTime::class)
class SubmissionRepositoryImpl(
    private val api: SRWApi
): SubmissionRepository {

    override fun getSubmissionsPager(): Pager<Int, Submission> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { SubmissionsPagingSource(api) }
        )
    }

    override suspend fun fetchRecentSubmissions(limit: Int): List<Submission> {
        val response = api.getSubmissions(page = 1, pageSize = limit)

        if (response.error != null) {
            throw Exception(response.error)
        }

        if (response.success != true) {
            throw Exception("Failed to fetch submissions: ${response.message ?: "Unknown error"}")
        }

        val data = response.data ?: throw Exception("No data received")
        return data.data.map { it.toDomain() }
    }

}