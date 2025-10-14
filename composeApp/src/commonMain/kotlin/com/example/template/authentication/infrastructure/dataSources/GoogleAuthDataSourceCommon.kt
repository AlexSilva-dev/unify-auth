package com.example.template.authentication.infrastructure.dataSources

import com.example.template.authentication.api.dtos.AuthorizationCodeDto
import com.example.template.authentication.domain.entities.UserSession

interface GoogleAuthDataSourceCommon {
    suspend fun googleAuthenticationByToken(token: String): UserSession
    suspend fun refreshSession(refreshToken: String): UserSession
    suspend fun googleAuthenticationByAuthorizationCode(authorizationCodeDto: AuthorizationCodeDto): UserSession
}