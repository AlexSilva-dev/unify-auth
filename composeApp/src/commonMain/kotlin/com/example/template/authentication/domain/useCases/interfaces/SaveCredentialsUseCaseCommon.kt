package com.example.template.authentication.domain.useCases.interfaces

import com.example.template.authentication.domain.entities.UserSession

interface SaveCredentialsUseCaseCommon {
    fun execute(userSession: UserSession)
}