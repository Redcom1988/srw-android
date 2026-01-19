package com.redcom1988.data.remote.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.redcom1988.core.network.parseAs
import com.redcom1988.data.remote.SRWApi
import com.redcom1988.data.remote.model.BaseResponse
import com.redcom1988.data.remote.model.PaginatedResponse
import com.redcom1988.data.remote.model.point.PointResponse
import com.redcom1988.data.remote.model.point.toDomain
import com.redcom1988.domain.point.model.Point
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PointsPagingSource(
    private val api: SRWApi
): PagingSource<Int, Point>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Point> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize

            val response = api.getClientPoints(page = page, pageSize = pageSize)
            val data = response.parseAs<BaseResponse<PaginatedResponse<PointResponse>>>()

            if (data.success == false) {
                return LoadResult.Error(Exception(data.message ?: "Failed to load points"))
            }

            val paginatedData = data.data ?: return LoadResult.Error(Exception("No data received"))
            val points = paginatedData.data.map { pointResponse ->
                pointResponse.toDomain()
            }
            val totalPages = paginatedData.totalPages

            LoadResult.Page(
                data = points,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= totalPages) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Point>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}