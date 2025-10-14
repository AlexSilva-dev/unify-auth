package com.example.template.authentication.infrastructure.repositories

interface TokenProvider {
    suspend fun getAccessToken(): String
    suspend fun getRefreshToken(): String
    suspend fun refreshTokens(refreshToken: String): Unit
}