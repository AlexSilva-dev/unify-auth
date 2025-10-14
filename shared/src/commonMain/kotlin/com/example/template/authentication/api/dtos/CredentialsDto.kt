package com.example.template.authentication.api.dtos

data class CredentialsDto(
    val id: String,
    val userId: String,
    val accessToken: String,
    val refreshToken: String,
    val createAt: String,
    val updateAt: String,
)
