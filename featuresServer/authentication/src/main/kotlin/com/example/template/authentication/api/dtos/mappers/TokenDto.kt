package com.example.template.authentication.api.dtos.mappers

data class TokenDto(
    val accessToken: String,
    val refreshToken: String,
)
