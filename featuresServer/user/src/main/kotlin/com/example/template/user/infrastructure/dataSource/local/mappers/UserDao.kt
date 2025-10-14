package com.example.template.user.infrastructure.dataSource.local.mappers

import com.example.template.user.infrastructure.dataSource.local.UserDao
import com.example.template.user.domain.entities.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toKotlinUuid

@OptIn(ExperimentalUuidApi::class)
fun UserDao.toDomain(): User {
    return User(
        id = this.id.value.toString(),
        fullName = this.fullName,
        email = this.email,
        profilePictureUrl = this.profilePictureUrl,
        createAt = this.createAt.toString(),
    )
}
