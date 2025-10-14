package com.example.template.authentication.domain.useCases

import com.example.template.app.domain.entities.Failure
import com.example.template.app.domain.entities.Result
import com.example.template.app.domain.entities.SignUpFailure
import com.example.template.authentication.domain.entities.AuthConfig
import com.example.template.authentication.domain.entities.AuthenticationCredentialsProvider
import com.example.template.authentication.domain.entities.Credential
import com.example.template.authentication.domain.entities.EmailValidator
import com.example.template.authentication.domain.entities.PasswordValidate
import com.example.template.authentication.domain.entities.SignUpData
import com.example.template.authentication.domain.ports.AuthenticationRepository
import com.example.template.user.domain.entities.User
import com.example.template.user.domain.ports.UserRepository

class SignUpUseCase(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend fun execute(signUpData: SignUpData): Result<User, Failure> {
        val validateResult: Result<Unit, Failure> = this.validateData(
            signUpData = signUpData
        )

        if (validateResult is Result.Failure) return validateResult

        val registerResult: User = this.userRepository.registerUser(
            user = User(
                fullName = signUpData.name,
                email = signUpData.email,
            )
        )

        val credentialResult: Result<Credential, Failure> = this.saveCredential(
            password = signUpData.password,
            userId = registerResult.id!!
        )
        if (credentialResult is Result.Failure) {
            this.userRepository.deleteById(
                userId = registerResult.id!!
            )
            return Result.Failure(
                error = Failure.UnknownError(
                    Exception("Falha ao criar credenciais")
                )
            )
        }

//        this.sendConfirmationEmail(
//            user = registerResult
//        )
        return Result.Success(
            data = registerResult
        )
    }

    private suspend fun saveCredential(password: String, userId: String): Result<Credential, Failure> {
        try {
            val hash: String = AuthConfig.passwordEncoder.encode(
                /* rawPassword = */ password
            )
            val credential: Credential = Credential(
                provider = AuthenticationCredentialsProvider.PASSWORD,
                providerId = null,
                secret = hash,
                userId = userId,
            )
            return this.authenticationRepository.saveCredentials(
                credential = credential
            )
        } catch (e: Exception) {
            return Result.Failure(
                error = Failure.UnknownError(
                    exception = e
                )
            )
        }
    }

    private suspend fun validateData(signUpData: SignUpData): Result<Unit, Failure> {
        val validateNameResult: Failure? = this.validateName(
            name = signUpData.name
        )
        if (validateNameResult != null) return Result.Failure(validateNameResult)

        val validateEmailResult: Failure? = this.validateEmail(
            email = signUpData.email
        )
        if (validateEmailResult != null) return Result.Failure(validateEmailResult)

        val validatePassword: Failure? = this.validatePassword(
            password = signUpData.password
        )
        if (validatePassword != null) return Result.Failure(validatePassword)

        return Result.Success(Unit)
    }

    private fun validatePassword(password: String): Failure? {
        if (!PasswordValidate.isValid(password)) return SignUpFailure.InvalidPassword
        return null
    }

    private suspend fun validateEmail(email: String): Failure? {
        if (!EmailValidator.isValid(email)) return SignUpFailure.InvalidEmail
        val findResult: Result<User?, Failure> = this.userRepository.findUserByEmail(email)
        if (findResult is Result.Failure) {
            throw Exception("Não deveria ocorrer erro ao verificar se usario existe: $findResult.error)")
        }
        findResult as Result.Success
        val user: User? = findResult.data
        if (user != null) return SignUpFailure.EmailAlreadyExists
        return null
    }

    private fun validateName(name: String): Failure? {
        if (name.isEmpty() && name.length < 3) return SignUpFailure.InvalidName
        return null
    }
}