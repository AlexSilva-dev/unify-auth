package com.example.template.authentication.domain.entities

import kotlin.uuid.Uuid

data class UserSession (
    val id: String? = null,
    val userId: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val createAt: String? = null,
    val updateAt: String? = null
)