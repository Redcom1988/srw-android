package com.redcom1988.domain.point.interactor

import androidx.paging.PagingData
import com.redcom1988.domain.point.model.Point
import com.redcom1988.domain.point.repository.PointRepository
import kotlinx.coroutines.flow.Flow

class GetProfilePoints(
    private val pointRepository: PointRepository
) {

    operator fun invoke(): Flow<PagingData<Point>> {
        return pointRepository.getPointsPager().flow
    }

}