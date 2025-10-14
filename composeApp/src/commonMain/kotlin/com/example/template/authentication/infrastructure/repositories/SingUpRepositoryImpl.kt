package com.example.template.authentication.infrastructure.repositories

import com.example.template.app.domain.entities.Failure
import com.example.template.app.domain.entities.Result
import com.example.template.authentication.domain.entities.SignUpData
import com.example.template.authentication.domain.ports.SignUpRepository
import com.example.template.authentication.infrastructure.dataSources.SignInDataSource
import com.example.template.user.domain.entities.User

class SingUpRepositoryImpl(
    private val signInDataSourceRemote: SignInDataSource
): SignUpRepository {
    override suspend fun accountCreate(signUpData: SignUpData): Result<User, Failure> {
        return this.signInDataSourceRemote.accountCreate(signUpData = signUpData)
    }
}