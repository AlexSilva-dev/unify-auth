package com.example.template.authentication.infrastructure.dataSources.local.mappers

import com.example.template.authentication.domain.entities.UserSession
import com.example.template.authentication.infrastructure.dataSources.local.UserSessionDao

fun UserSessionDao.toDomain(): UserSession {
    return UserSession(
        id = this.id.toString(),
        userId = this.userId.toString(),
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        createAt = this.createAt.toString(),
        updateAt = this.updateAt.toString(),
    )
}