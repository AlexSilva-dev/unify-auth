package com.example.template.authentication.domain.entities

data class UserInfo(
    val provider: AuthenticationCredentialsProvider,
    val providerId: String,
    val name: String,
    val email: String,
    val secret: String? = null,
    val profilePictureUrl: String?,
)