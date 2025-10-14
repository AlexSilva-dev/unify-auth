package com.example.template.user.api.dtos.mappers

import com.example.template.user.api.dtos.UserDto
import com.example.template.user.domain.entities.User

fun UserDto.toDomain(): User {
    return User(
        id = this.id,
        fullName = this.fullName,
        email = this.email,
        profilePictureUrl = this.profilePictureUrl,
        createAt = this.createAt,
        updateAt = this.updateAt,
        isActive = this.isActive,
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = this.id,
        fullName = this.fullName,
        email = this.email,
        profilePictureUrl = this.profilePictureUrl,
        createAt = this.createAt,
        updateAt = this.updateAt,
        isActive = this.isActive,
    )
}