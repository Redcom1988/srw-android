package com.redcom1988.data.remote.model.client

import com.redcom1988.domain.client.model.Client
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun ClientResponse.toDomain(): Client {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    return Client(
        id = id,
        name = name,
        createdAt = LocalDateTime.parse(createdAt, formatter),
        updatedAt = LocalDateTime.parse(updatedAt, formatter),
    )
}

