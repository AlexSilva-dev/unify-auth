package com.example.template.authentication.infrastructure.dataSources

import com.example.template.app.domain.entities.Failure
import com.example.template.app.domain.entities.Result
import com.example.template.authentication.domain.entities.AuthorizationCode
import com.example.template.authentication.domain.entities.UserInfo

interface GoogleAuthenticationDataSource {
    suspend fun buildUserInfoByToken(token: String): UserInfo?
    suspend fun getIdTokenByAuthorizationCode(authorizationCode: AuthorizationCode): Result<String, Failure>
}