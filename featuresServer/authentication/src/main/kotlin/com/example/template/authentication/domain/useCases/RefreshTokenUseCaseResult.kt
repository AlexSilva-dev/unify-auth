package com.example.template.authentication.domain.useCases

import com.example.template.authentication.domain.entities.UserSession

sealed class RefreshTokenUseCaseResult(
) {
    data class Success(
        val userSession: UserSession,
    ): RefreshTokenUseCaseResult()

    object InvalidRefreshToken: RefreshTokenUseCaseResult()
    data class InternalError(val message: String): RefreshTokenUseCaseResult()
}