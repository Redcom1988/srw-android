package com.redcom1988.data.remote.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.redcom1988.data.remote.SRWApi
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

            if (response.error != null) {
                return LoadResult.Error(Exception(response.error))
            }
            if (response.success != true) {
                return LoadResult.Error(Exception(response.message ?: "Unknown error"))
            }

            val data = response.data ?: return LoadResult.Error(Exception("No data received"))
            val submissions = data.data.map { submissionResponse ->
                submissionResponse.toDomain() }
            val totalPages = data.totalPages

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

