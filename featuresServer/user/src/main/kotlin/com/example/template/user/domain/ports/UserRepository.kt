package com.example.template.user.domain.ports

import com.example.template.app.domain.entities.Failure
import com.example.template.app.domain.entities.Result
import com.example.template.user.domain.entities.User
import kotlin.uuid.ExperimentalUuidApi

interface UserRepository {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun findUserById(id: String): User?

    suspend fun findUserByEmail(email: String): Result<User?, Failure>

    suspend fun registerUser(user: User): User

    suspend fun deleteById(userId: String): Result<Unit, Failure>

}