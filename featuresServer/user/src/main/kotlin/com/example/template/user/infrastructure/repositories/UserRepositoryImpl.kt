package com.example.template.user.infrastructure.repositories

import com.example.template.app.domain.entities.Failure
import com.example.template.app.domain.entities.Result
import com.example.template.user.database.tables.UsersTable
import com.example.template.user.domain.entities.User
import com.example.template.user.domain.ports.UserRepository
import com.example.template.user.infrastructure.dataSource.local.UserDao
import com.example.template.user.infrastructure.dataSource.local.mappers.toDomain
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.ZoneOffset
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.uuid.ExperimentalUuidApi

class UserRepositoryImpl(
) : UserRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun findUserById(id: String): User? {
        val uuid: UUID = try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid UUID format")
        }

        val user: User? = transaction {
            val newUser = UserDao.findById(uuid)
            newUser?.toDomain()
        }
        return user
    }

    override suspend fun findUserByEmail(email: String): Result<User?, Failure> {
        try {
            val user: User? = transaction {
                val user: UserDao? = UserDao.find {
                    UsersTable.email eq email
                }.firstOrNull()
                user?.toDomain()
            }
            return Result.Success(
                data = user
            )
        } catch (e: Exception) {
            return Result.Failure(
                Failure.UnknownError(
                    exception = e
                )
            )
        }


    }

    @OptIn(ExperimentalTime::class)
    override suspend fun registerUser(user: User): User {
        val user: User = transaction {
            val newUser = UserDao.new {
                fullName = user.fullName
                email = user.email
                profilePictureUrl = user.profilePictureUrl
                createAt = Clock.System.now().toJavaInstant().atOffset(ZoneOffset.UTC)
                updateAt = Clock.System.now().toJavaInstant().atOffset(ZoneOffset.UTC)
            }
            newUser.toDomain()
        }
        return user
    }

    override suspend fun deleteById(userId: String): Result<Unit, Failure> {
        try {
            transaction {
                val userDao: UserDao? = UserDao.findById(UUID.fromString(userId))
                userDao?.delete()
            }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Failure(Failure.UnknownError(e))
        }
    }
}