package com.example.template.authentication.domain.useCases.interfaces

import com.example.template.authentication.domain.entities.AuthenticationWithGoogleUseCaseResult
import com.example.template.authentication.domain.entities.AuthorizationCode

interface AuthenticationWithGoogleUseCase {
    suspend fun execute(token: String): AuthenticationWithGoogleUseCaseResult
    suspend fun execute(authorizationCode: AuthorizationCode): AuthenticationWithGoogleUseCaseResult
}