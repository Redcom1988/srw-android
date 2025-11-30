package com.redcom1988.domain.point.repository

import com.redcom1988.domain.point.model.Point
import kotlinx.coroutines.flow.Flow

interface PointRepository{

    fun subscribe(clientId: Int): Flow<List<Point?>>

}