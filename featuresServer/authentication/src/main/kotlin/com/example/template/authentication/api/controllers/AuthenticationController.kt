package com.example.template.authentication.api.controllers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.template.app.domain.entities.Failure
import com.example.template.app.domain.entities.Result
import com.example.template.authentication.api.dtos.GoogleProviderRequestDto
import com.example.template.authentication.api.dtos.SignUpDataDto
import com.example.template.authentication.api.dtos.mappers.toDomain
import com.example.template.authentication.api.dtos.mappers.toDto
import com.example.template.authentication.domain.entities.AuthenticationWithGoogleUseCaseResult
import com.example.template.authentication.domain.useCases.RefreshTokenUseCase
import com.example.template.authentication.domain.useCases.RefreshTokenUseCaseResult
import com.example.template.authentication.domain.useCases.SignUpUseCase
import com.example.template.authentication.domain.useCases.interfaces.AuthenticationWithGoogleUseCase
import com.example.template.authentication.utils.JWT_AUDIENCE
import com.example.template.authentication.utils.JWT_ISSUER
import com.example.template.authentication.utils.JWT_SECRET
import com.example.template.user.api.dtos.mappers.toDto
import com.example.template.user.domain.entities.User
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class AuthenticationController(
    private val authenticationWithGoogleUseCase: AuthenticationWithGoogleUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
) {
    suspend fun tokenAuthenticationWithGoogle(call: RoutingCall) {

        val googleProviderRequestDto: GoogleProviderRequestDto = call.receive<GoogleProviderRequestDto>()
        if (
            googleProviderRequestDto.idToken.isNullOrEmpty()
            && (googleProviderRequestDto.redirectUri.isNullOrEmpty() && googleProviderRequestDto.authorizationCode.isNullOrEmpty())
        ) {
            call.respondNullable(
                status = HttpStatusCode.BadRequest,
                message = "Invalid request"
            )
            return
        }
        val result: AuthenticationWithGoogleUseCaseResult = this.authenticationWithGoogleUseCase.execute(
            googleProviderRequest = googleProviderRequestDto.toDomain()
        )

        when (result) {
            is AuthenticationWithGoogleUseCaseResult.InvalidToken -> {
                call.respondNullable(
                    status = HttpStatusCode.BadRequest,
                    message = "Invalid token"
                )
            }

            is AuthenticationWithGoogleUseCaseResult.UnknownError -> {
                call.respondNullable(
                    status = HttpStatusCode.InternalServerError,
                    message = "Unknown error"
                )
            }

            is AuthenticationWithGoogleUseCaseResult.UserNotRegistered -> {
                call.respondNullable(
                    status = HttpStatusCode.Created,
                    message = result.userSession.toDto()
                )
            }

            is AuthenticationWithGoogleUseCaseResult.InvalidRedirectUrl -> {
                call.respondNullable(
                    status = HttpStatusCode.BadRequest,
                    message = "Invalid redirect url"
                )
            }

            is AuthenticationWithGoogleUseCaseResult.GoogleUserNotFound -> {
                call.respondNullable(
                    status = HttpStatusCode.BadRequest,
                    message = "Google user not found"
                )
            }

            is AuthenticationWithGoogleUseCaseResult.Success -> {
                call.respondNullable(
                    status = HttpStatusCode.OK,
                    message = result.userSession.toDto()
                )
            }
        }

    }


    suspend fun refreshToken(call: RoutingCall) {
        val tokenReceive: String? = call.request.headers["refresh_token"]

        if (tokenReceive.isNullOrEmpty()) {
            call.respondNullable(
                status = HttpStatusCode.BadRequest,
                message = "Invalid token"
            )
            return
        }
        var jwtVerifier: DecodedJWT? = null

        try {
            try {
                jwtVerifier = this.decodedJWT(tokenReceive)
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = "Invalid token"
                )
            }
            if (jwtVerifier == null) {
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = "Invalid token"
                )
                return
            }
            val refreshTokenUseCaseResult = this.refreshTokenUseCase.execute(
                refreshToken = jwtVerifier.token
            )
            when (refreshTokenUseCaseResult) {
                RefreshTokenUseCaseResult.InvalidRefreshToken -> {
                    call.respond(
                        status = HttpStatusCode.Unauthorized,
                        message = "Invalid token"
                    )
                }

                is RefreshTokenUseCaseResult.InternalError -> {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = "Internal server error"
                    )
                }

                is RefreshTokenUseCaseResult.Success -> {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = refreshTokenUseCaseResult.userSession.toDto()
                    )
                }

            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError
            )
        }
    }

    private fun decodedJWT(tokenReceive: String): DecodedJWT? = JWT.require(Algorithm.HMAC256(JWT_SECRET))
        .withIssuer(JWT_ISSUER)
        .withAudience(JWT_AUDIENCE)
        .build()
        .verify(tokenReceive)


    suspend fun signUp(call: RoutingCall) {

        val signUpDataDto: SignUpDataDto = call.receive<SignUpDataDto>()
        if (signUpDataDto.name.isEmpty() || signUpDataDto.email.isEmpty() || signUpDataDto.password.isEmpty()) {
            call.respondNullable(
                status = HttpStatusCode.BadRequest,
                message = "Invalid data"
            )
            return
        }
        val result: Result<User, Failure> = this.signUpUseCase.execute(
            signUpData = signUpDataDto.toDomain()
        )

        if (result is Result.Success) {
            call.respondNullable(
                status = HttpStatusCode.Created,
                message = result.data.toDto()
            )
            return
        }

        when (val error = (result as Result.Failure).error) {
            is Failure.ServerError -> {
                call.respondNullable(
                    status = HttpStatusCode.InternalServerError,
                    message = "Internal server error"
                )
            }

            is Failure.UnknownError -> {
                call.respondNullable(
                    status = HttpStatusCode.InternalServerError,
                    message = "Unknown error"
                )

            }

            else -> {
                call.respondNullable(
                    status = HttpStatusCode.BadRequest,
                    message = "Bad request"
                )
            }
        }
    }
}