package com.example.template.authentication.infrastructure.repositories

import com.example.template.app.infrastructure.dataSources.SettingsDataStore
import com.example.template.authentication.api.dtos.AuthorizationCodeDto
import com.example.template.authentication.api.dtos.UserSessionDto
import com.example.template.authentication.api.dtos.mappers.toDomain
import com.example.template.authentication.api.dtos.mappers.toDto
import com.example.template.authentication.domain.entities.UserSession
import com.example.template.authentication.domain.ports.GoogleAuthRepositoryCommon
import com.example.template.authentication.infrastructure.dataSources.GoogleAuthDataSourceCommon
import com.example.template.authentication.ui.viewModels.GoogleAuthorization

class GoogleAuthRepository(
    private val googleAuthDataSource: GoogleAuthDataSourceCommon,
    private val settingsDataStore: SettingsDataStore,
): GoogleAuthRepositoryCommon {
    override suspend fun authenticateByToken(token: String): UserSession {
        val userSession: UserSession = this.googleAuthDataSource.googleAuthenticationByToken(
            token = token
        )
        this.saveUserSession(
            userSession = userSession
        )
        return userSession
    }

    override suspend fun authenticateByAuthorizationCode(googleAuthorization: GoogleAuthorization): UserSession {
        if (googleAuthorization.redirectUrl.isNullOrEmpty()) {
            throw Exception("Invalid redirect url")
        }
        val userSession: UserSession = this.googleAuthDataSource.googleAuthenticationByAuthorizationCode(
            authorizationCodeDto = AuthorizationCodeDto(
                token = googleAuthorization.token,
                redirectUrl = googleAuthorization.redirectUrl
            )
        )
        this.saveUserSession(
            userSession = userSession
        )
        return userSession
    }

    private suspend fun saveUserSession(userSession: UserSession) {
        if (userSession.accessToken == null || userSession.refreshToken == null) {
            throw IllegalArgumentException(
                "UserSession must have accessToken and refreshToken"
            )
        }

        this.settingsDataStore.remove(
            key = "userSession"
        )

        this.settingsDataStore.save<UserSessionDto>(
            key = "userSession",
            serializer = UserSessionDto.serializer(),
            value = userSession.toDto()
        )
    }

    override suspend fun getUserSession(): UserSession? {
        val userSessionDto: UserSessionDto = this.settingsDataStore.getObject(
            key = "userSession",
            serializer = UserSessionDto.serializer(),
            default = UserSessionDto(),
        )
        if (userSessionDto.id == null) return null

        return userSessionDto.toDomain()
    }

    override suspend fun refreshSession(refreshToken: String): UserSession {
        val userSession: UserSession = this.googleAuthDataSource.refreshSession(
            refreshToken = refreshToken
        )
        this.saveUserSession(
            userSession = userSession
        )
        return userSession
    }

}