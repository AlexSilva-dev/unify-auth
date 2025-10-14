package com.example.template.authentication.api.dtos.mappers

import com.example.template.authentication.api.dtos.UserSessionDto
import com.example.template.authentication.domain.entities.UserSession

fun UserSessionDto.toDomain(): UserSession {
    return UserSession(
        id = this.id,
        userId = this.userId,
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        createAt = this.createAt,
        updateAt = this.updateAt
    )
}

fun UserSession.toDto(): UserSessionDto {
    return UserSessionDto(
        id = this.id,
        userId = this.userId,
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        createAt = this.createAt,
        updateAt = this.updateAt
    )
}