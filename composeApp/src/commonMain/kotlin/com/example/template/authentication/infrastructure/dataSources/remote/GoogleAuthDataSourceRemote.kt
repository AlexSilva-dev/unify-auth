package com.example.template.authentication.infrastructure.dataSources.remote

import com.example.template.app.navigations.AuthenticationRequiredException
import com.example.template.app.utils.SERVER_HOST
import com.example.template.app.utils.SERVER_PORT
import com.example.template.app.utils.SERVER_PROTOCOL
import com.example.template.authentication.api.dtos.AuthorizationCodeDto
import com.example.template.authentication.api.dtos.GoogleProviderRequestDto
import com.example.template.authentication.api.dtos.UserSessionDto
import com.example.template.authentication.api.dtos.mappers.toDomain
import com.example.template.authentication.domain.entities.UserSession
import com.example.template.authentication.infrastructure.dataSources.GoogleAuthDataSourceCommon
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class GoogleAuthDataSourceRemote(
    private val client: HttpClient,
    private val unauthenticatedClient: HttpClient,
    private val host: String = SERVER_HOST,
    private val port: Int = SERVER_PORT,
    private val protocol: String = SERVER_PROTOCOL
) : GoogleAuthDataSourceCommon {
    override suspend fun googleAuthenticationByToken(token: String): UserSession {
        val httpResponse: HttpResponse = this.unauthenticatedClient.post {
            url {
                protocol = if (this@GoogleAuthDataSourceRemote.protocol == "http") {
                    URLProtocol.HTTP
                } else {
                    URLProtocol.HTTPS
                }
                host = this@GoogleAuthDataSourceRemote.host
                port = this@GoogleAuthDataSourceRemote.port
                path("/auth/login/google/token")
            }
            contentType(type = ContentType.Application.Json)
            setBody(
                GoogleProviderRequestDto(
                    idToken = token
                )
            )
        }
        val userSession: UserSessionDto = httpResponse.body()
        return userSession.toDomain()
    }

    override suspend fun googleAuthenticationByAuthorizationCode(
        authorizationCodeDto: AuthorizationCodeDto
    ): UserSession {
        val httpResponse: HttpResponse = this.unauthenticatedClient.post {
            url {
                protocol = if (this@GoogleAuthDataSourceRemote.protocol == "http") {
                    URLProtocol.HTTP
                } else {
                    URLProtocol.HTTPS
                }
                host = this@GoogleAuthDataSourceRemote.host
                port = this@GoogleAuthDataSourceRemote.port
                path("/auth/login/google/authorization-code")
            }
            contentType(type = ContentType.Application.Json)
            setBody(
                authorizationCodeDto
            )
        }
        val userSession: UserSessionDto = httpResponse.body()
        return userSession.toDomain()
    }

    override suspend fun refreshSession(refreshToken: String): UserSession {

        val httpResponse: HttpResponse = this.unauthenticatedClient.post {
            url {
                protocol = if (this@GoogleAuthDataSourceRemote.protocol == "http") {
                    URLProtocol.HTTP
                } else {
                    URLProtocol.HTTPS
                }
                host = this@GoogleAuthDataSourceRemote.host
                port = this@GoogleAuthDataSourceRemote.port
                headers.append(
                    name = "refresh_token",
                    value = refreshToken
                )

                path("/auth/login/refresh-token")
            }
            contentType(type = ContentType.Application.Json)
        }
        if (httpResponse.status == HttpStatusCode.Unauthorized) {
            throw AuthenticationRequiredException()
        }
        val userSession: UserSessionDto = httpResponse.body()
        return userSession.toDomain()
    }

}