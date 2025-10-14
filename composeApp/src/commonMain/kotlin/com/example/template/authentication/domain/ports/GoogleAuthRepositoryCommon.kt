package com.example.template.authentication.domain.ports

import com.example.template.authentication.domain.entities.UserSession
import com.example.template.authentication.ui.viewModels.GoogleAuthorization

interface GoogleAuthRepositoryCommon {
    suspend fun authenticateByToken(token: String): UserSession
    suspend fun getUserSession(): UserSession?
    suspend fun refreshSession(refreshToken: String): UserSession
    suspend fun authenticateByAuthorizationCode(googleAuthorization: GoogleAuthorization): UserSession
}