package com.example.template.authentication.domain.ports

import com.example.template.app.domain.entities.Failure
import com.example.template.app.domain.entities.Result
import com.example.template.authentication.domain.entities.SignUpData
import com.example.template.user.domain.entities.User

interface SignUpRepository {
    suspend fun accountCreate(signUpData: SignUpData): Result<User, Failure>
}
