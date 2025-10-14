package com.example.template.authentication.infrastructure.repositories

import com.example.template.authentication.domain.entities.UserSession
import com.example.template.authentication.domain.ports.GoogleAuthRepositoryCommon

class TokenProviderImpl(
    private val googleAuthRepository: GoogleAuthRepositoryCommon,
): TokenProvider {

    private suspend fun getUserSession(): UserSession? {
        return this.googleAuthRepository.getUserSession()
    }

    override suspend fun getAccessToken(): String {
        val userSession: UserSession? = this.getUserSession()
        return userSession?.accessToken ?: throw Exception("UserSession is null")
    }

    override suspend fun getRefreshToken(): String {
        val userSession: UserSession? = this.getUserSession()
        return userSession?.refreshToken ?: throw Exception("UserSession is null")
    }

    override suspend fun refreshTokens(refreshToken: String): Unit {
        this.googleAuthRepository.refreshSession(
            refreshToken = refreshToken
        )
    }
}