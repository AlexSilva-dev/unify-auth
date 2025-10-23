package com.example.template.authentication.domain.useCases

import com.example.template.app.domain.entities.Failure
import com.example.template.app.domain.entities.Result
import com.example.template.authentication.domain.entities.*
import com.example.template.authentication.domain.ports.AuthenticationRepository
import com.example.template.authentication.domain.ports.TransactionManager
import com.example.template.user.domain.entities.User
import com.example.template.user.domain.ports.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class AuthenticationWithGoogleUseCaseImplTest {

    private val authenticationRepository: AuthenticationRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val transactionManager: TransactionManager = mockk()

    private lateinit var useCase: AuthenticationWithGoogleUseCaseImpl

    @BeforeEach
    fun setUp() {
        // Mock the transactionManager to simply execute the block directly for testing purposes
        // In a real scenario, this would handle transaction logic
        coEvery { transactionManager.invoke<Any>(any()) } coAnswers {
            val block = arg<suspend () -> Any>(0)
            block.invoke()
        }
        useCase = AuthenticationWithGoogleUseCaseImpl(authenticationRepository, userRepository, transactionManager)
    }

    @Nested
    @DisplayName("Authentication with Authorization Code")
    inner class AuthenticationWithAuthorizationCode {

        private val authCodeRequest = GoogleProviderRequest(
            authorizationCode = "some_auth_code",
            redirectUri = "http://localhost:8080/callback",
            idToken = null
        )
        private val idToken = "some_id_token"
        private val userInfo = UserInfo(
            providerId = "google_user_id",
            name = "Test User",
            email = "test@example.com",
            profilePictureUrl = "http://example.com/pic.jpg",
            secret = "some_secret",
            provider = AuthenticationCredentialsProvider.GOOGLE
        )
        private val user = User(
            id = UUID.randomUUID().toString(),
            fullName = userInfo.name,
            email = userInfo.email,
            profilePictureUrl = userInfo.profilePictureUrl
        )
        private val userSession = UserSession(
            userId = user.id,
            accessToken = "access_token",
            refreshToken = "refresh_token"
        )
        private val credential = Credential(
            userId = user.id,
            provider = AuthenticationCredentialsProvider.GOOGLE,
            providerId = userInfo.providerId,
            secret = userInfo.secret
        )

        @Test
        @DisplayName("should successfully authenticate a new user with authorization code")
        fun `should successfully authenticate a new user with authorization code`() = runTest {
            // Arrange
            coEvery {
                authenticationRepository.googleAuthorizationCodeVerify(any())
            } returns Result.Success(idToken)
            coEvery { authenticationRepository.googleTokenVerify(idToken) } returns userInfo
            coEvery {
                authenticationRepository.findByProvider(any(), any())
            } returns null // User not found by provider
            coEvery { userRepository.registerUser(any()) } returns user
            coEvery { authenticationRepository.saveCredentials(any()) } returns Result.Success(credential)
            coEvery { authenticationRepository.createUserSession(user.id!!) } returns userSession

            // Act
            val result = useCase.execute(authCodeRequest)

            // Assert
            assertEquals(AuthenticationWithGoogleUseCaseResult.UserNotRegistered(userSession), result)
        }

        @Test
        @DisplayName("should successfully authenticate an existing user with authorization code")
        fun `should successfully authenticate an existing user with authorization code`() = runTest {
            // Arrange
            coEvery { authenticationRepository.googleAuthorizationCodeVerify(any()) } returns Result.Success(idToken)
            coEvery { authenticationRepository.googleTokenVerify(idToken) } returns userInfo
            coEvery {
                authenticationRepository.findByProvider(
                    any(),
                    any()
                )
            } returns credential // User found by provider
            coEvery { userRepository.findUserById(credential.userId!!) } returns user
            coEvery { authenticationRepository.createUserSession(user.id!!) } returns userSession

            // Act
            val result = useCase.execute(authCodeRequest)

            // Assert
            assertEquals(AuthenticationWithGoogleUseCaseResult.Success(userSession), result)
        }

        @Test
        @DisplayName("should return InvalidRedirectUrl if redirectUri is missing for authorization code")
        fun `should return InvalidRedirectUrl if redirectUri is missing for authorization code`() = runTest {
            // Arrange
            val request = authCodeRequest.copy(redirectUri = null)

            // Act
            val result = useCase.execute(request)

            // Assert
            assertEquals(AuthenticationWithGoogleUseCaseResult.InvalidRedirectUrl, result)
        }

        @Test
        @DisplayName("should return GoogleUserNotFound if authorization code verification fails")
        fun `should return GoogleUserNotFound if authorization code verification fails`() = runTest {
            // Arrange
            coEvery { authenticationRepository.googleAuthorizationCodeVerify(any()) } returns Result.Failure(
                Failure.ServerError(
                    500,
                    "Error"
                )
            )

            // Act
            val result = useCase.execute(authCodeRequest)

            // Assert
            assertEquals(AuthenticationWithGoogleUseCaseResult.GoogleUserNotFound, result)
        }

        @Test
        @DisplayName("should return InvalidToken if google token verification fails")
        fun `should return InvalidToken if google token verification fails`() = runTest {
            // Arrange
            coEvery { authenticationRepository.googleAuthorizationCodeVerify(any()) } returns Result.Success(idToken)
            coEvery { authenticationRepository.googleTokenVerify(idToken) } returns null

            // Act
            val result = useCase.execute(authCodeRequest)

            // Assert
            assertEquals(AuthenticationWithGoogleUseCaseResult.InvalidToken, result)
        }

        @Test
        @DisplayName("should return UnknownError if user registration fails")
        fun `should return UnknownError if user registration fails`() = runTest {
            // Arrange
            coEvery { authenticationRepository.googleAuthorizationCodeVerify(any()) } returns Result.Success(idToken)
            coEvery { authenticationRepository.googleTokenVerify(idToken) } returns userInfo
            coEvery { authenticationRepository.findByProvider(any(), any()) } returns null
            coEvery { userRepository.registerUser(any()) } returns user.copy(id = null) // Simulate user registration failure

            // Act
            val result = useCase.execute(authCodeRequest)

            // Assert
            assertEquals(AuthenticationWithGoogleUseCaseResult.UnknownError, result)
        }

        @Test
        @DisplayName("should return UnknownError if saving credentials fails and rollback user")
        fun `should return UnknownError if saving credentials fails and rollback user`() = runTest {
            // Arrange
            coEvery { authenticationRepository.googleAuthorizationCodeVerify(any()) } returns Result.Success(idToken)
            coEvery { authenticationRepository.googleTokenVerify(idToken) } returns userInfo
            coEvery { authenticationRepository.findByProvider(any(), any()) } returns null
            coEvery { userRepository.registerUser(any()) } returns user
            coEvery { authenticationRepository.saveCredentials(any()) } returns Result.Failure(
                Failure.UnknownError(
                    Exception("DB Error")
                )
            )
            coEvery { userRepository.deleteById(user.id!!) } returns Result.Success(Unit) // Mock rollback

            // Act
            val result = useCase.execute(authCodeRequest)

            // Assert
            assertEquals(AuthenticationWithGoogleUseCaseResult.UnknownError, result)
        }
    }

    @Nested
    @DisplayName("Authentication with ID Token directly")
    inner class AuthenticationWithIdTokenDirectly {

        private val idToken = "some_id_token"
        private val idTokenRequest = GoogleProviderRequest(
            idToken = idToken,
            authorizationCode = null,
            redirectUri = null
        )
        private val userInfo = UserInfo(
            providerId = "google_user_id",
            name = "Test User",
            email = "test@example.com",
            profilePictureUrl = "http://example.com/pic.jpg",
            secret = "some_secret",
            provider = AuthenticationCredentialsProvider.GOOGLE
        )
        private val user = User(
            id = UUID.randomUUID().toString(),
            fullName = userInfo.name,
            email = userInfo.email,
            profilePictureUrl = userInfo.profilePictureUrl
        )
        private val userSession = UserSession(
            userId = user.id,
            accessToken = "access_token",
            refreshToken = "refresh_token"
        )
        private val credential = Credential(
            userId = user.id,
            provider = AuthenticationCredentialsProvider.GOOGLE,
            providerId = userInfo.providerId,
            secret = userInfo.secret
        )

        @Test
        @DisplayName("should successfully authenticate a new user with ID token")
        fun `should successfully authenticate a new user with ID token`() = runTest {
            // Arrange
            coEvery { authenticationRepository.googleTokenVerify(idToken) } returns userInfo
            coEvery { authenticationRepository.findByProvider(any(), any()) } returns null
            coEvery { userRepository.registerUser(any()) } returns user
            coEvery { authenticationRepository.saveCredentials(any()) } returns Result.Success(credential)
            coEvery { authenticationRepository.createUserSession(user.id!!) } returns userSession

            // Act
            val result = useCase.execute(idTokenRequest)

            // Assert
            assertEquals(AuthenticationWithGoogleUseCaseResult.UserNotRegistered(userSession), result)
        }

        @Test
        @DisplayName("should successfully authenticate an existing user with ID token")
        fun `should successfully authenticate an existing user with ID token`() = runTest {
            // Arrange
            coEvery { authenticationRepository.googleTokenVerify(idToken) } returns userInfo
            coEvery { authenticationRepository.findByProvider(any(), any()) } returns credential
            coEvery { userRepository.findUserById(credential.userId!!) } returns user
            coEvery { authenticationRepository.createUserSession(user.id!!) } returns userSession

            // Act
            val result = useCase.execute(idTokenRequest)

            // Assert
            assertEquals(AuthenticationWithGoogleUseCaseResult.Success(userSession), result)
        }

        @Test
        @DisplayName("should return InvalidToken if google token verification fails")
        fun `should return InvalidToken if google token verification fails`() = runTest {
            // Arrange
            coEvery { authenticationRepository.googleTokenVerify(idToken) } returns null

            // Act
            val result = useCase.execute(idTokenRequest)

            // Assert
            assertEquals(AuthenticationWithGoogleUseCaseResult.InvalidToken, result)
        }
    }
}
