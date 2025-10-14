package com.example.template.authentication.infrastructure.dataSources

import com.example.template.authentication.domain.entities.UserSession
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface JwtDataSource {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun buildToken(userId: Uuid): UserSession
    suspend fun getJti(token: String): String?
}