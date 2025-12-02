package com.redcom1988.srw.screens.pointsscreen

import androidx.paging.PagingData
import cafe.adriel.voyager.core.model.ScreenModel
import com.redcom1988.core.util.inject
import com.redcom1988.domain.point.interactor.GetProfilePoints
import com.redcom1988.domain.point.model.Point
import kotlinx.coroutines.flow.Flow

class PointsScreenModel(
    private val getProfilePoints: GetProfilePoints = inject()
) : ScreenModel {

    val pointsPagingData: Flow<PagingData<Point>> = getProfilePoints()

}
