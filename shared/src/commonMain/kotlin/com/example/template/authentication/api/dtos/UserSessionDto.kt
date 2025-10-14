package com.example.template.authentication.api.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserSessionDto(
    val id: String? = null,
    val userId: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val createAt: String? = null,
    val updateAt: String? = null
)
