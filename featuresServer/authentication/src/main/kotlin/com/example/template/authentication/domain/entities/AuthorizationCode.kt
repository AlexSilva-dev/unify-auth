package com.example.template.authentication.domain.entities

data class AuthorizationCode(
    val authorizationCode: String,
    val url: String,
)