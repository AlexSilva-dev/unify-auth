package com.example.template.authentication.domain.useCases.interfaces

import com.example.template.authentication.ui.viewModels.GoogleAuthorization

interface GoogleAuthenticateUseCaseCommon {
    suspend fun execute(googleAuthorization: GoogleAuthorization): Boolean
}