package com.example.template.authentication.infrastructure.dataSources.remote

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.template.authentication.domain.entities.UserSession
import com.example.template.authentication.infrastructure.dataSources.JwtDataSource
import com.example.template.authentication.utils.JWT_AUDIENCE
import com.example.template.authentication.utils.JWT_ISSUER
import com.example.template.authentication.utils.JWT_SECRET
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class JwtDataSourceImpl(
    val jwtSecretKey: String? = JWT_SECRET,
    val jwtIssuer: String? = JWT_ISSUER,
    val jwtAudience: String? = JWT_AUDIENCE,
) : JwtDataSource {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun buildToken(
        userId: Uuid
    ): UserSession {
        val accessToken: String = this.generateAccessToken(userId)
        val refreshToken: String = this.generateRefreshToken(userId)
        return UserSession(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }

    override suspend fun getJti(token: String): String? {
        return JWT.decode(token).id
    }

    /**
     * Gera um Access Token JWT.
     * Access Tokens devem ter uma validade curta para segurança.
     *
     * @param userId O ID do usuário para o qual o token está sendo gerado.
     * @return O Access Token JWT como uma String.
     */
    @OptIn(ExperimentalUuidApi::class)
    fun generateAccessToken(userId: Uuid, minutes: Long = 9): String {
        val currentTime = System.currentTimeMillis()
        val expirationTime =
            currentTime + TimeUnit.SECONDS.toMillis(minutes)
        val jtiValue = UUID.randomUUID().toString()

        return JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withJWTId(jtiValue)
            .withClaim("userId", userId.toString())
            .withExpiresAt(Date(expirationTime))
            .withIssuedAt(Date(currentTime))
            .sign(Algorithm.HMAC256(jwtSecretKey))
    }

    /**
     * Gera um Refresh Token JWT.
     * Refresh Tokens devem ter uma validade mais longa que os Access Tokens.
     *
     * @param userId O ID do usuário para o qual o token está sendo gerado.
     * @return O Refresh Token JWT como uma String.
     */
    @OptIn(ExperimentalUuidApi::class)
    fun generateRefreshToken(userId: Uuid, days: Long = 9): String {
        val currentTime = System.currentTimeMillis()
        val expirationTime = currentTime + TimeUnit.SECONDS.toMillis(days)

        return JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withClaim("userId", userId.toString())
            .withExpiresAt(Date(expirationTime))
            .withIssuedAt(Date(currentTime))
            .sign(Algorithm.HMAC256(jwtSecretKey))
    }
}