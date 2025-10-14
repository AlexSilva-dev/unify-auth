package com.example.template.authentication.ui.viewModels

data class GoogleAuthorization(
    val token: String,
    val redirectUrl: String? = null
)
