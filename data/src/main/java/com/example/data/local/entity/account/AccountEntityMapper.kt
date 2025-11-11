package com.example.data.local.entity.account

import com.example.core.util.toLocalDateTime
import com.example.core.util.toUtcString
import com.example.domain.balance.model.Account

fun AccountEntity.toDomain(): Account {
    return Account(
        id = id,
        name = name,
        balance = balance,
        createdAt = createdAt.toLocalDateTime(),
        updatedAt = updatedAt.toLocalDateTime(),
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        name = name,
        balance = balance,
        createdAt = createdAt.toUtcString(),
        updatedAt = updatedAt.toUtcString(),
    )
}