package com.redcom1988.domain.auth.model

data class AuthToken(
    val accessToken: String,
    val refreshToken: String
)