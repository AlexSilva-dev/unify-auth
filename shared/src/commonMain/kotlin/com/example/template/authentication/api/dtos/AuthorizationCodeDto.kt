package com.example.template.authentication.api.dtos

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationCodeDto(
    val token: String,
    val redirectUrl: String
)
