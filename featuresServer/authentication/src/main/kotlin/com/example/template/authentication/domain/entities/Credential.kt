package com.example.template.authentication.domain.entities

import kotlin.uuid.ExperimentalUuidApi

data class Credential @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String? = null,
    val userId: String? = null,
    val provider: AuthenticationCredentialsProvider? = null,
    val providerId: String? = null,
    val secret: String? = null,
    val createAt: String? = null,
    val updateAt: String? = null
)
