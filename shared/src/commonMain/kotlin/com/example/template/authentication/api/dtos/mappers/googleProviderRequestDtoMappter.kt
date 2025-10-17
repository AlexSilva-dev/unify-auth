package com.example.template.authentication.api.dtos.mappers

import com.example.template.authentication.api.dtos.GoogleProviderRequestDto
import com.example.template.authentication.domain.entities.GoogleProviderRequest

fun GoogleProviderRequestDto.toDomain(): GoogleProviderRequest {
    return GoogleProviderRequest(
        idToken = this.idToken,
        authorizationCode = this.authorizationCode,
        redirectUri = this.redirectUri,
    )
}