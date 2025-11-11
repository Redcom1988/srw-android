package com.example.data.remote.model.balance

import com.example.data.local.entity.account.AccountEntity
import com.example.domain.balance.model.Account

fun AccountResponse.toEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        name = name,
        balance = balance,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

