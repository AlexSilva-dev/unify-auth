package com.example.template.authentication.domain.ports

import com.example.template.app.domain.entities.Failure
import com.example.template.authentication.domain.entities.AuthorizationCode
import com.example.template.authentication.domain.entities.Credential
import com.example.template.authentication.domain.entities.UserInfo
import com.example.template.authentication.domain.entities.UserSession

interface AuthenticationRepository {
    suspend fun googleTokenVerify(token: String): UserInfo?
    suspend fun findCredentialsByGoogleId(googleId: String): Credential?
    suspend fun saveCredentials(credential: Credential): com.example.template.app.domain.entities.Result<Credential, Failure>
    suspend fun createUserSession(userId: String): UserSession
    suspend fun findUserSessionByUserId(userId: String): UserSession?
    suspend fun findByRefreshToken(refreshToken: String): Result<UserSession?>
    suspend fun googleAuthorizationCodeVerify(authorizationCode: AuthorizationCode): com.example.template.app.domain.entities.Result<String, Failure>
}