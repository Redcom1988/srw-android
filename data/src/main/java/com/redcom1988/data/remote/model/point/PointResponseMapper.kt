package com.redcom1988.data.remote.model.point

import com.redcom1988.domain.point.model.Point
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun PointResponse.toDomain(): Point {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    return Point(
        id = id,
        submissionId = submissionId,
        amount = amount,
        createdAt = LocalDateTime.parse(createdAt, formatter),
    )
}