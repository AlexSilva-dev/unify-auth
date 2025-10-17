package com.example.template.authentication.api.dtos.mappers

import com.example.template.authentication.api.dtos.AuthorizationCodeDto
import com.example.template.authentication.domain.entities.AuthorizationCode

fun AuthorizationCodeDto.toDomain(): AuthorizationCode {
    return AuthorizationCode(
        authorizationCode = this.token,
        url = this.redirectUrl
    )
}
