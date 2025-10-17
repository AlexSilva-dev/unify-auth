package com.example.template.authentication.api.dtos

import kotlinx.serialization.Serializable

@Serializable
data class GoogleProviderRequestDto(
    val idToken: String? = null,
    val authorizationCode: String? = null,
    val redirectUri: String? = null,
)