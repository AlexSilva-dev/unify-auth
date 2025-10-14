package com.example.template.user.domain.useCases

import com.example.template.user.domain.entities.User
import com.example.template.user.domain.ports.UserRepository

class FindUserByIdUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(
        id: String
    ): User? {
        return userRepository.findUserById(
            id = id
        )
    }
}