package com.example.template.authentication.infrastructure.dataSources.local.mappers

import com.example.template.authentication.domain.entities.AuthenticationCredentialsProvider
import com.example.template.authentication.domain.entities.Credential
import com.example.template.authentication.infrastructure.dataSources.local.CredentialsDao
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun CredentialsDao.toDomain(): Credential {
    return Credential(
        id = this.id.value.toString(),
        userId = this.userId.toString(),
        provider = when(this.provider.uppercase()) {
            "GOOGLE" -> AuthenticationCredentialsProvider.GOOGLE
            "PASSWORD" -> AuthenticationCredentialsProvider.PASSWORD
            else -> throw Exception("Invalid provider")
        },
        providerId = this.providerId,
        secret = this.secret,
        createAt = this.createAt.toString(),
        updateAt = this.updateAt.toString()
    )
}