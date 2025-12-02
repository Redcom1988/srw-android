package com.redcom1988.data.remote.model.client

import com.redcom1988.domain.client.model.Client
import java.time.format.DateTimeFormatter

fun ClientResponse.toDomain(): Client {
    DateTimeFormatter.ISO_DATE_TIME
    return Client(
        id = id,
        name = name,
        address = address,
        nfc = nfc,
        totalPoints = totalPoints
    )
}

