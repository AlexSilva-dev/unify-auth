package com.example.template.authentication.domain.useCases.interfaces

import com.example.template.authentication.domain.entities.AuthenticationWithGoogleUseCaseResult
import com.example.template.authentication.domain.entities.GoogleProviderRequest

interface AuthenticationWithGoogleUseCase {
    suspend fun execute(googleProviderRequest: GoogleProviderRequest): AuthenticationWithGoogleUseCaseResult
}