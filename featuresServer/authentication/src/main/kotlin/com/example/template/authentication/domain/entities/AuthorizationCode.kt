package com.example.template.authentication.domain.entities

data class AuthorizationCode(
    val token: String,
    val url: String,
)