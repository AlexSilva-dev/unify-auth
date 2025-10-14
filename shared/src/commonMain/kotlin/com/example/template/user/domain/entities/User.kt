package com.example.template.user.domain.entities

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * @todo
 */
data class User @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String? = null,
    val fullName: String,
    val email: String,
    val profilePictureUrl: String? = null,
    val createAt: String? = null,
    val updateAt: String? = null,
    val isActive: Boolean? = null
)
