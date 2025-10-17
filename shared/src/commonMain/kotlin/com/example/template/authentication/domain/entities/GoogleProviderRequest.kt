package com.example.template.authentication.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class GoogleProviderRequest(
    val idToken: String?,
    val authorizationCode: String?,
    val redirectUri: String?,
)
