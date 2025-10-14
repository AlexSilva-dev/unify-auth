package com.example.template.authentication.domain.useCases

import com.example.template.authentication.domain.entities.UserSession
import com.example.template.authentication.domain.ports.AuthenticationRepository

class RefreshTokenUseCase(
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend fun execute(refreshToken: String): RefreshTokenUseCaseResult {
        val findByRefreshTokenResult: Result<UserSession?> =
            authenticationRepository.findByRefreshToken(
                refreshToken = refreshToken
            )

        var userSession: UserSession? = null
        findByRefreshTokenResult.fold(
            onSuccess = { userSessionResult ->
                if (userSessionResult == null) return RefreshTokenUseCaseResult.InvalidRefreshToken
                userSession = userSessionResult
            },
            onFailure = { e ->
                return RefreshTokenUseCaseResult.InternalError(
                    message = e.message ?: "Internal Error"
                )
            }
        )

        if (userSession?.userId.isNullOrEmpty()) throw IllegalStateException(
            "User id cannot be null"
        )
        val userId = userSession.userId

        val newUserSession: UserSession = this.authenticationRepository.createUserSession(
            userId = userId!!
        )

        return RefreshTokenUseCaseResult.Success(
            userSession = newUserSession
        )
    }
}