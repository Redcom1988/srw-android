package com.redcom1988.data.remote.model.balance

import com.redcom1988.domain.balance.model.Account
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun AccountResponse.toDomain(): Account {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    return Account(
        id = id,
        name = name,
        balance = balance,
        createdAt = LocalDateTime.parse(createdAt, formatter),
        updatedAt = LocalDateTime.parse(updatedAt, formatter),
    )
}

