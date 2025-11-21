package com.redcom1988.data.repository

import com.redcom1988.data.remote.SRWApi
import com.redcom1988.domain.point.model.Point
import com.redcom1988.domain.point.repository.PointRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PointRepositoryImpl(
    private val api: SRWApi
): PointRepository {

    override fun subscribe(clientId: Int): Flow<List<Point?>> {
        return flow {
            while (true) {
                try {
                    // TODO: Implement actual API call and data mapping
                    val points = listOf<Point>() // Replace with actual data fetching logic
                    emit(points)
                } catch (_: Exception) {
                    emit(emptyList())
                }
                delay(30000)
            }
        }.flowOn(Dispatchers.IO)
    }

}