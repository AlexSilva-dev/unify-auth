package com.example.template.authentication.api.dtos.mappers

import com.example.template.authentication.api.dtos.CredentialsDto
import com.example.template.authentication.domain.entities.UserSession

fun CredentialsDto.toDomain(): UserSession {
    return UserSession(
        id = this.id,
        userId = this.userId,
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        createAt = this.createAt,
        updateAt = this.updateAt,
    )
}