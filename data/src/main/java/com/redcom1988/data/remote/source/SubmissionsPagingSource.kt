package com.redcom1988.data.remote.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.redcom1988.core.network.parseAs
import com.redcom1988.data.remote.SRWApi
import com.redcom1988.data.remote.model.BaseResponse
import com.redcom1988.data.remote.model.PaginatedResponse
import com.redcom1988.data.remote.model.submission.SubmissionResponse
import com.redcom1988.data.remote.model.submission.toDomain
import com.redcom1988.domain.submission.model.Submission
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SubmissionsPagingSource(
    private val api: SRWApi
): PagingSource<Int, Submission>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Submission> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize

            val response = api.getSubmissions(page = page, pageSize = pageSize)
            val data = response.parseAs<BaseResponse<PaginatedResponse<SubmissionResponse>> >()

            if (data.success == false) {
                return LoadResult.Error(Exception(data.message ?: "Failed to load submissions"))
            }

            val paginatedData = data.data ?: return LoadResult.Error(Exception("No data received"))
            val submissions = paginatedData.data.map { submissionResponse ->
                submissionResponse.toDomain()
            }
            val totalPages = paginatedData.totalPages

            LoadResult.Page(
                data = submissions,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= totalPages) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Submission>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

