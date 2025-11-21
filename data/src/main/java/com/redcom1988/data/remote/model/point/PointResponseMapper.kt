package com.redcom1988.data.remote.model.point

import com.redcom1988.domain.point.model.Point
import java.time.format.DateTimeFormatter

fun PointResponse.toDomain(): Point {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    return Point(
        id = id,
        clientId = clientId,
        value = value,
        reason = reason,
        createdAt = java.time.LocalDateTime.parse(createdAt, formatter),
        updatedAt = java.time.LocalDateTime.parse(updatedAt, formatter),
    )
}