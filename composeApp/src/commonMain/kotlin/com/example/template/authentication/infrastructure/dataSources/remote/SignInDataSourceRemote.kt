package com.example.template.authentication.infrastructure.dataSources.remote

import com.example.template.app.domain.entities.Failure
import com.example.template.app.domain.entities.Result
import com.example.template.app.domain.entities.SignUpFailure
import com.example.template.app.utils.SERVER_HOST
import com.example.template.app.utils.SERVER_PORT
import com.example.template.app.utils.SERVER_PROTOCOL
import com.example.template.authentication.api.dtos.mappers.toDto
import com.example.template.authentication.domain.entities.SignUpData
import com.example.template.authentication.infrastructure.dataSources.SignInDataSource
import com.example.template.user.api.dtos.UserDto
import com.example.template.user.api.dtos.mappers.toDomain
import com.example.template.user.domain.entities.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path

class SignInDataSourceRemote(
    private val client: HttpClient,
    private val unauthenticatedClient: HttpClient,
    private val host: String = SERVER_HOST,
    private val port: Int = SERVER_PORT,
    private val protocol: String = SERVER_PROTOCOL
) : SignInDataSource {
    override suspend fun accountCreate(signUpData: SignUpData): Result<User, Failure> {
        return try {
            val httpResponse: HttpResponse = this.unauthenticatedClient.post {
                url {
                    protocol = if (this@SignInDataSourceRemote.protocol == "http") {
                        URLProtocol.HTTP
                    } else {
                        URLProtocol.HTTPS
                    }
                    host = this@SignInDataSourceRemote.host
                    port = this@SignInDataSourceRemote.port
                    path("/auth/signup")
                }
                contentType(type = ContentType.Application.Json)
                setBody(
                    signUpData.toDto()
                )
            }
            when (httpResponse.status) {
                HttpStatusCode.Conflict -> {
                    Result.Failure(
                        SignUpFailure.EmailAlreadyExists
                    )
                }

                HttpStatusCode.BadRequest -> {
                    Result.Failure(
                        Failure.ServerError(
                            code = httpResponse.status.value,
                            message = httpResponse.body()
                        )
                    )
                }

                HttpStatusCode.Created -> {

                    val resultServer: UserDto = httpResponse.body<UserDto>()
                    Result.Success(resultServer.toDomain())
                }

                else -> {
                    Result.Failure(
                        Failure.ServerError(
                            code = httpResponse.status.value,
                            message = httpResponse.body()
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Result.Failure(
                Failure.UnknownError(
                    exception = e
                )
            )
        }
    }
}