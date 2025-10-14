package com.example.template.authentication.api.dtos

import kotlinx.serialization.Serializable

@Serializable
data class TokenDto(
    val token: String
)
