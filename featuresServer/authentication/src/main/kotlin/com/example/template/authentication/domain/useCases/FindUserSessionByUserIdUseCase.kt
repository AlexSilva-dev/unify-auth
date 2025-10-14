package com.example.template.authentication.com.exemple.template.authentication.domain.useCases

import com.example.template.authentication.domain.entities.UserSession
import com.example.template.authentication.domain.ports.AuthenticationRepository

class FindUserSessionByUserIdUseCase(
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend fun execute(userId: String): UserSession? {
        val userSession: UserSession? =
            this.authenticationRepository.findUserSessionByUserId(userId)
        return userSession
    }
}