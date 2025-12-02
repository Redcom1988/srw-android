package com.redcom1988.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.redcom1988.data.remote.SRWApi
import com.redcom1988.data.remote.source.PointsPagingSource
import com.redcom1988.domain.point.model.Point
import com.redcom1988.domain.point.repository.PointRepository

class PointRepositoryImpl(
    private val api: SRWApi
): PointRepository {

    override fun getPointsPager(): Pager<Int, Point> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { PointsPagingSource(api) }
        )
    }

}