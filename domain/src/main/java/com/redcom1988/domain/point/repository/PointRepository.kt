package com.redcom1988.domain.point.repository

import androidx.paging.Pager
import com.redcom1988.domain.point.model.Point
import kotlinx.coroutines.flow.Flow

interface PointRepository{

    fun getPointsPager(): Pager<Int, Point>

}