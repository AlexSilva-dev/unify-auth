package com.example.template.authentication.infrastructure.dataSources

import com.example.template.authentication.domain.entities.AuthorizationCode
import com.example.template.authentication.domain.entities.UserInfo

interface GoogleAuthenticationDataSource {
    suspend fun buildUserInfoByToken(token: String): UserInfo?
    suspend fun buildUserInfoByAuthorizationCode(authorizationCode: AuthorizationCode): UserInfo?
}