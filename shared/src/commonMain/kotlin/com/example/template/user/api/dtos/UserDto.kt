package com.example.template.user.api.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String? = null,
    val fullName: String,
    val email: String,
    val profilePictureUrl: String? = null,
    val createAt: String? = null,
    val updateAt: String? = null,
    val isActive: Boolean? = null
)

