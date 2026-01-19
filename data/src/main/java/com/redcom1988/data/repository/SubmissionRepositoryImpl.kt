package com.redcom1988.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.redcom1988.core.network.parseAs
import com.redcom1988.data.remote.SRWApi
import com.redcom1988.data.remote.model.BaseResponse
import com.redcom1988.data.remote.model.PaginatedResponse
import com.redcom1988.data.remote.model.submission.SubmissionResponse
import com.redcom1988.data.remote.model.submission.toDomain
import com.redcom1988.data.remote.source.SubmissionsPagingSource
import com.redcom1988.domain.submission.model.Submission
import com.redcom1988.domain.submission.repository.SubmissionRepository
import java.io.File


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
        val data = response.parseAs<BaseResponse<PaginatedResponse<SubmissionResponse>>>()

        if (data.success == false) {
            throw Exception(data.message ?: "Failed to fetch submissions")
        }

        val submissionsData = data.data ?: throw Exception("No data received")
        return submissionsData.data.map { it.toDomain() }
    }

    override suspend fun uploadSubmission(imageFiles: List<File>): Submission {
        val response = api.uploadSubmission(imageFiles)
        val data = response.parseAs<BaseResponse<SubmissionResponse>>()

        if (data.success == false) {
            throw Exception(data.message ?: "Failed to upload submission")
        }

        val submissionData = data.data ?: throw Exception("No data received")
        return submissionData.toDomain()
    }

}