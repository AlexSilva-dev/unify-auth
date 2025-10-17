package com.example.template.authentication.domain.entities

sealed class AuthenticationWithGoogleUseCaseResult {
    data class Success(
        val userSession: UserSession,
    ) : AuthenticationWithGoogleUseCaseResult()

    /**
     * O token é inválido ou expirado
     */
    object InvalidToken : AuthenticationWithGoogleUseCaseResult()

    /**
     * O token é válido, mas o usuário não existe no DB
     */
    data class UserNotRegistered(
        val userSession: UserSession,
    ) : AuthenticationWithGoogleUseCaseResult()

    /**
     * Erro inesperado
     */
    object UnknownError : AuthenticationWithGoogleUseCaseResult()

    object InvalidRedirectUrl : AuthenticationWithGoogleUseCaseResult()

    object GoogleUserNotFound : AuthenticationWithGoogleUseCaseResult()
}