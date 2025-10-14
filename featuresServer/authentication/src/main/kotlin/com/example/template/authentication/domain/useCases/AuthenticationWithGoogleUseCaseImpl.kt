package com.example.template.authentication.domain.useCases

import com.example.template.authentication.domain.entities.AuthenticationWithGoogleUseCaseResult
import com.example.template.authentication.domain.entities.AuthorizationCode
import com.example.template.authentication.domain.entities.Credential
import com.example.template.authentication.domain.entities.UserInfo
import com.example.template.authentication.domain.entities.UserSession
import com.example.template.authentication.domain.ports.AuthenticationRepository
import com.example.template.authentication.domain.useCases.interfaces.AuthenticationWithGoogleUseCase
import com.example.template.user.domain.entities.User
import com.example.template.user.domain.ports.UserRepository
import kotlin.uuid.ExperimentalUuidApi

class AuthenticationWithGoogleUseCaseImpl(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository,
) : AuthenticationWithGoogleUseCase {
    override suspend fun execute(token: String): AuthenticationWithGoogleUseCaseResult {
        val userInfo: UserInfo? = this.authenticationRepository.googleTokenVerify(
            token = token
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

    override suspend fun execute(authorizationCode: AuthorizationCode): AuthenticationWithGoogleUseCaseResult {
        val userInfo: UserInfo? = this.authenticationRepository.googleAuthorizationCodeVerify(
            authorizationCode = authorizationCode
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
    ): AuthenticationWithGoogleUseCaseResult.UserNotRegistered {
        try {
            val user: User = this.userRepository.registerUser(
                user = User(
                    fullName = userInfo.name,
                    email = userInfo.email,
                    profilePictureUrl = userInfo.profilePictureUrl,
                )
            )
            if (user.id == null) throw Exception("User id cannot be null")

            val credential: Credential = Credential(
                userId = user.id,
                provider = userInfo.provider,
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
            throw Exception("Error registering user")
        }
    }

    private suspend fun createSessionForRegisteredUser(
        user: User,
    ): AuthenticationWithGoogleUseCaseResult.Success {
        if (user.id.isNullOrEmpty()) throw Exception("User id cannot be null")
        val userSession: UserSession = this.authenticationRepository
            .createUserSession(
                userId = user.id!!
            )

        return AuthenticationWithGoogleUseCaseResult.Success(
            userSession = userSession
        )
    }

}