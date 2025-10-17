package com.example.template.authentication.domain.useCases

import com.example.template.app.domain.entities.Failure
import com.example.template.app.domain.entities.Result
import com.example.template.authentication.domain.entities.*
import com.example.template.authentication.domain.ports.AuthenticationRepository
import com.example.template.authentication.domain.useCases.interfaces.AuthenticationWithGoogleUseCase
import com.example.template.user.domain.entities.User
import com.example.template.user.domain.ports.UserRepository
import kotlin.uuid.ExperimentalUuidApi

class AuthenticationWithGoogleUseCaseImpl(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository,
) : AuthenticationWithGoogleUseCase {
    override suspend fun execute(googleProviderRequest: GoogleProviderRequest): AuthenticationWithGoogleUseCaseResult {
        var idToken: String? = googleProviderRequest.idToken
        if (!googleProviderRequest.authorizationCode.isNullOrEmpty()) {
            if (googleProviderRequest.redirectUri.isNullOrEmpty()) return AuthenticationWithGoogleUseCaseResult.InvalidRedirectUrl

            val result: Result<String, Failure> =
                this.authenticationRepository.googleAuthorizationCodeVerify(
                    authorizationCode = AuthorizationCode(
                        authorizationCode = googleProviderRequest.authorizationCode!!,
                        url = googleProviderRequest.redirectUri!!
                    )
                )
            if (result is Result.Failure) {
                return AuthenticationWithGoogleUseCaseResult.GoogleUserNotFound
            }
            if (result is Result.Success) {
                idToken = result.data
            }
        }

        if (idToken.isNullOrEmpty()) return AuthenticationWithGoogleUseCaseResult.UnknownError
        val result: AuthenticationWithGoogleUseCaseResult = this.authenticationWithIdToken(
            idToken = idToken
        )

        return result
    }

    suspend fun authenticationWithIdToken(idToken: String): AuthenticationWithGoogleUseCaseResult {
        val userInfo: UserInfo? = this.authenticationRepository.googleTokenVerify(
            token = idToken
        )
        if (userInfo == null) return AuthenticationWithGoogleUseCaseResult.InvalidToken

        val user = this.getUser(
            userInfo = userInfo
        )
        if (user == null) {
            return this.register(
                userInfo
            )
        }

        return this.createSessionForRegisteredUser(
            user = user,
        )
    }


    @OptIn(ExperimentalUuidApi::class)
    private suspend fun getUser(userInfo: UserInfo): User? {
        val credential: Credential? = this.authenticationRepository.findCredentialsByGoogleId(
            googleId = userInfo.providerId
        )
        if (credential == null) return null

        val user: User? = this.userRepository.findUserById(
            id = credential.userId.toString()
        )
        return user
    }

    private suspend fun register(
        userInfo: UserInfo,
    ): AuthenticationWithGoogleUseCaseResult {
        try {
            val user: User = this.userRepository.registerUser(
                user = User(
                    fullName = userInfo.name,
                    email = userInfo.email,
                    profilePictureUrl = userInfo.profilePictureUrl,
                )
            )
            if (user.id == null) return AuthenticationWithGoogleUseCaseResult.UnknownError

            val credential: Credential = Credential(
                userId = user.id,
                provider = AuthenticationCredentialsProvider.GOOGLE,
                providerId = userInfo.providerId,
                secret = userInfo.secret,
            )
            this.authenticationRepository.saveCredentials(
                credential = credential,
            )

            val userSession: UserSession = this.authenticationRepository
                .createUserSession(
                    userId = user.id!!
                )


            return AuthenticationWithGoogleUseCaseResult.UserNotRegistered(
                userSession = userSession
            )
        } catch (e: Exception) {
            return AuthenticationWithGoogleUseCaseResult.UnknownError
        }
    }

    private suspend fun createSessionForRegisteredUser(
        user: User,
    ): AuthenticationWithGoogleUseCaseResult {
        if (user.id.isNullOrEmpty()) return AuthenticationWithGoogleUseCaseResult.UnknownError
        val userSession: UserSession = this.authenticationRepository
            .createUserSession(
                userId = user.id!!
            )

        return AuthenticationWithGoogleUseCaseResult.Success(
            userSession = userSession
        )
    }

}