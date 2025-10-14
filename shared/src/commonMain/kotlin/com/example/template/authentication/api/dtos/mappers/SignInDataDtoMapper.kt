package com.example.template.authentication.api.dtos.mappers

import com.example.template.authentication.api.dtos.SignUpDataDto
import com.example.template.authentication.domain.entities.SignUpData


fun SignUpData.toDto(): SignUpDataDto {
    return SignUpDataDto(
        name = this.name,
        email = this.email,
        password = this.password,
    )
}

fun SignUpDataDto.toDomain(): SignUpData {
    return SignUpData(
        name = this.name,
        email = this.email,
        password = this.password,
    )
}