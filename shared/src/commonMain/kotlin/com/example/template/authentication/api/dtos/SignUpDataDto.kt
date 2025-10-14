package com.example.template.authentication.api.dtos

import kotlinx.serialization.Serializable

@Serializable
data class SignUpDataDto(
    val name: String,
    val email: String,
    val password: String,
)
