package com.example.template.authentication.domain.useCases

import com.example.template.app.domain.entities.Result
import com.example.template.app.domain.entities.Failure
import com.example.template.authentication.domain.entities.SignUpData
import com.example.template.authentication.domain.ports.SignUpRepository
import com.example.template.user.domain.entities.User

class SignUpUseCase(
    private val signUpRepository: SignUpRepository
) {

    suspend fun execute(
        signUpData: SignUpData
    ): Result<User, Failure> {
        return this.signUpRepository.accountCreate(
            signUpData = signUpData
        )
    }
}
