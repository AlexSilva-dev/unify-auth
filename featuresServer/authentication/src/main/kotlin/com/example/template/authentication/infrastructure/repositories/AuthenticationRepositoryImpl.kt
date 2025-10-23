package com.example.template.authentication.infrastructure.repositories

import com.example.template.app.domain.entities.Failure
import com.example.template.authentication.domain.entities.*
import com.example.template.authentication.domain.ports.AuthenticationRepository
import com.example.template.authentication.infrastructure.dataSources.GoogleAuthenticationDataSource
import com.example.template.authentication.infrastructure.dataSources.JwtDataSource
import com.example.template.authentication.infrastructure.dataSources.local.CredentialsDao
import com.example.template.authentication.infrastructure.dataSources.local.UserSessionDao
import com.example.template.authentication.infrastructure.dataSources.local.mappers.toDomain
import com.example.template.authentication.infrastructure.database.tables.CredentialsTable
import com.example.template.authentication.infrastructure.database.tables.UserSessionsTable
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.ZoneOffset
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class AuthenticationRepositoryImpl(
    private val JwtDataSource: JwtDataSource,
    private val googleAuthenticationDataSource: GoogleAuthenticationDataSource
) : AuthenticationRepository {
    override suspend fun googleTokenVerify(token: String): UserInfo? {
        return this.googleAuthenticationDataSource.buildUserInfoByToken(
            token = token
        )
    }

    override suspend fun googleAuthorizationCodeVerify(authorizationCode: AuthorizationCode): com.example.template.app.domain.entities.Result<String, Failure> {
        return this.googleAuthenticationDataSource.getIdTokenByAuthorizationCode(
            authorizationCode = authorizationCode
        )
    }

    override suspend fun findByProvider(provider: AuthenticationCredentialsProvider, providerId: String): Credential? {
        val credential: Credential? = transaction {
            val credentialsEntityList = CredentialsDao.find {
                CredentialsTable.providerId eq providerId
                CredentialsTable.provider eq provider.name
            }
            val credentialsEntity = credentialsEntityList.firstOrNull()
            credentialsEntity?.toDomain()
        }
        return credential
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun saveCredentials(
        credential: Credential,
    ): com.example.template.app.domain.entities.Result<Credential, Failure> {
        if (credential.userId.isNullOrEmpty()
            || credential.provider == null
        ) {
            throw Exception("userId and provide can't null or empty")
        }
        try {
            val savedCredential: Credential = transaction {
                val newCredentials = CredentialsDao.new {
                    this.userId = UUID.fromString(credential.userId)
                    this.provider = credential.provider.name
                    if (!credential.providerId.isNullOrBlank()) {
                        this.providerId = credential.providerId
                    }
                    this.secret = credential.secret
                    this.createAt = Clock.System.now().toJavaInstant().atOffset(ZoneOffset.UTC)
                    this.updateAt = Clock.System.now().toJavaInstant().atOffset(ZoneOffset.UTC)
                }
                newCredentials.toDomain()
            }
            return com.example.template.app.domain.entities.Result.Success(
                data = savedCredential
            )
        } catch (e: Exception) {
            return com.example.template.app.domain.entities.Result.Failure(
                error = Failure.UnknownError(e)
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun createUserSession(
        userId: String
    ): UserSession {
        val userIdUuid: Uuid = Uuid.parse(userId)

        val tokens = this.JwtDataSource.buildToken(
            userId = userIdUuid,
        )
        if (tokens.accessToken.isNullOrEmpty() || tokens.refreshToken.isNullOrEmpty()) {
            throw RuntimeException("Invalid token")
        }

        val userSession: UserSession = transaction {
            val userSessionDao = UserSessionDao.new {
                this@new.userId = userIdUuid.toJavaUuid()
                accessToken = tokens.accessToken!!
                refreshToken = tokens.refreshToken!!
                createAt = Clock.System.now().toJavaInstant().atOffset(ZoneOffset.UTC)
                updateAt = Clock.System.now().toJavaInstant().atOffset(ZoneOffset.UTC)
            }
            userSessionDao.toDomain()
        }
        return userSession
    }

    override suspend fun findUserSessionByUserId(userId: String): UserSession? {
        val userIdUuid: UUID = UUID.fromString(userId)
        val userSession: UserSession? = transaction {
            val userSessionDao = UserSessionDao.find {
                UserSessionsTable.userId eq userIdUuid
            }.firstOrNull()
            userSessionDao?.toDomain()
        }
        return userSession
    }

    override suspend fun findByRefreshToken(refreshToken: String): Result<UserSession?> {
        if (refreshToken.isEmpty()) return Result.failure(
            Exception("Invalid refresh token")
        )

        try {
            val userSession: UserSession? = transaction {
                val userSessionDao = UserSessionDao.find {
                    UserSessionsTable.refreshToken eq refreshToken
                }.firstOrNull()
                userSessionDao?.toDomain()
            }

            return Result.success(userSession)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

}