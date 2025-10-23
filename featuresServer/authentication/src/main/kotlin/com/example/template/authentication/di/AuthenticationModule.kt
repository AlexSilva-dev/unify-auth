package com.example.template.authentication.di

import com.example.template.authentication.api.controllers.AuthenticationController
import com.example.template.authentication.domain.ports.AuthenticationRepository
import com.example.template.authentication.domain.ports.TransactionManager
import com.example.template.authentication.domain.useCases.AuthenticationWithGoogleUseCaseImpl
import com.example.template.authentication.domain.useCases.RefreshTokenUseCase
import com.example.template.authentication.domain.useCases.SignUpUseCase
import com.example.template.authentication.domain.useCases.interfaces.AuthenticationWithGoogleUseCase
import com.example.template.authentication.infrastructure.dataSources.GoogleAuthenticationDataSource
import com.example.template.authentication.infrastructure.dataSources.JwtDataSource
import com.example.template.authentication.infrastructure.dataSources.remote.GoogleAuthenticationDataSourceImpl
import com.example.template.authentication.infrastructure.dataSources.remote.JwtDataSourceImpl
import com.example.template.authentication.infrastructure.database.ExposedTransactionManager
import com.example.template.authentication.infrastructure.repositories.AuthenticationRepositoryImpl
import org.koin.dsl.module

/**
 * Este é o "kit de montagem" da feature de autenticação.
 * Ele ensina o Koin a criar todas as classes necessárias para esta feature.
 */
val authenticationModule = module {

    // Camada de Infrastructure
    // -------------------------------------------------
    // DataSources (geralmente singletons, pois não guardam estado)
    single<GoogleAuthenticationDataSource> { GoogleAuthenticationDataSourceImpl() }
    single<JwtDataSource> { JwtDataSourceImpl() }
    single<TransactionManager> {
        ExposedTransactionManager(
            database = get()
        )
    }

    // Repositories (geralmente singletons)
    // O Koin injetará as dependências (JwtDataSource e GoogleAuthenticationDataSource) usando get()
    single<AuthenticationRepository> {
        AuthenticationRepositoryImpl(
            JwtDataSource = get(),
            googleAuthenticationDataSource = get()
        )
    }


    // Camada de Domain
    // -------------------------------------------------
    // UseCases (geralmente factories, pois são leves e podem ser criados a cada uso)
    // O Koin injetará o AuthenticationRepository e o UserRepository (que virá de outro módulo)
    factory<AuthenticationWithGoogleUseCase> {
        AuthenticationWithGoogleUseCaseImpl(
            authenticationRepository = get(),
            userRepository = get(),
            transactionManager = get()
        )
    }
    factory {
        SignUpUseCase(
            userRepository = get(),
            authenticationRepository = get()
        )
    }
    factory {
        RefreshTokenUseCase(
            authenticationRepository = get()
        )
    }


    // Camada de API
    // -------------------------------------------------
    // Controllers (factories, pois o Ktor pode gerenciá-los por requisição)
    factory {
        AuthenticationController(
            authenticationWithGoogleUseCase = get(),
            signUpUseCase = get(),
            refreshTokenUseCase = get()
        )
    }
}
