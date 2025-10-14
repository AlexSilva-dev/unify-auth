package com.example.template.authentication.domain.useCases

import com.example.template.authentication.domain.entities.UserSession
import com.example.template.authentication.domain.ports.GoogleAuthRepositoryCommon
import com.example.template.authentication.domain.useCases.interfaces.GoogleAuthenticateUseCaseCommon
import com.example.template.authentication.ui.viewModels.GoogleAuthorization

class GoogleAuthenticateUseCase(
    private val googleAuthRepository: GoogleAuthRepositoryCommon,
) : GoogleAuthenticateUseCaseCommon {
    override suspend fun execute(googleAuthorization: GoogleAuthorization): Boolean {
        var isAuthenticated: Boolean = false
        try {

                val userSession: UserSession = if (googleAuthorization.redirectUrl.isNullOrEmpty()) {
                        this.googleAuthRepository.authenticateByToken(
                        token = googleAuthorization.token
                    )
                } else {
                    this.googleAuthRepository.authenticateByAuthorizationCode(
                        googleAuthorization = googleAuthorization
                    )
                }
                isAuthenticated = true
        } catch (e: Exception) {
            println("Ocorreu um erro")
            println(e.message)
        }

        return isAuthenticated
    }
}