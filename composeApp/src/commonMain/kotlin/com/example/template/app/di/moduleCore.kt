package com.example.template.app.di

import com.example.template.app.domain.ports.LocaleSettingsRepository
import com.example.template.app.domain.useCases.GetLocaleSettingsUseCase
import com.example.template.app.domain.useCases.SetLocaleSettingsUseCase
import com.example.template.app.infrastructure.dataSources.SettingsDataStore
import com.example.template.app.infrastructure.dataSources.SettingsDataStoreImpl
import com.example.template.app.infrastructure.repositories.LocaleSettingsRepositoryImpl
import com.example.template.app.infrastructure.services.LocaleSettingsService
import com.example.template.authentication.domain.ports.GoogleAuthRepositoryCommon
import com.example.template.authentication.domain.ports.SignUpRepository
import com.example.template.authentication.domain.useCases.GoogleAuthenticateUseCase
import com.example.template.authentication.domain.useCases.SignUpUseCase
import com.example.template.authentication.domain.useCases.interfaces.GoogleAuthenticateUseCaseCommon
import com.example.template.authentication.infrastructure.dataSources.GoogleAuthDataSourceCommon
import com.example.template.authentication.infrastructure.dataSources.SignInDataSource
import com.example.template.authentication.infrastructure.dataSources.remote.GoogleAuthDataSourceRemote
import com.example.template.authentication.infrastructure.dataSources.remote.SignInDataSourceRemote
import com.example.template.authentication.infrastructure.repositories.GoogleAuthRepository
import com.example.template.authentication.infrastructure.repositories.SingUpRepositoryImpl
import com.example.template.authentication.infrastructure.repositories.TokenProvider
import com.example.template.authentication.infrastructure.repositories.TokenProviderImpl
import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

object HttpClients {
    val Authenticated = named("AuthenticatedHttpClient")
    val Public = named("RefreshTokenHttpClient")
}

val moduleCore: Module = module {
    factory<HttpClient>(HttpClients.Authenticated) {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true // Formata o JSON para ser mais legível no log
                        ignoreUnknownKeys = true // Ignora campos JSON que não estão nas suas data classes
                    })
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val tokenProvider by inject<TokenProvider>()

                        val accessToken: String = tokenProvider.getAccessToken()
                        val refreshToken: String = tokenProvider.getRefreshToken()
                        BearerTokens(
                            accessToken = accessToken,
                            refreshToken = refreshToken
                        )
                    }

                    refreshTokens {
                        val currentRefreshToken: String? = oldTokens?.refreshToken
                        if (currentRefreshToken.isNullOrEmpty()) {
                            return@refreshTokens null
                        }
                        val googleAuthRepository by inject<GoogleAuthRepositoryCommon>()
                        val newRefreshToken = try {
                            googleAuthRepository.refreshSession(
                                refreshToken = currentRefreshToken
                            )
                        } catch (e: Exception) {
                            return@refreshTokens null
                        }
                        if (newRefreshToken.accessToken == null || newRefreshToken.refreshToken == null) {
                            return@refreshTokens null
                        }
                        return@refreshTokens BearerTokens(
                            accessToken = newRefreshToken.accessToken!!,
                            refreshToken = newRefreshToken.refreshToken!!
                        )
                    }
                }
            }
        }
    }

    factory<HttpClient>(HttpClients.Public) {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true // Formata o JSON para ser mais legível no log
                        ignoreUnknownKeys = true // Ignora campos JSON que não estão nas suas data classes
                    }
                )
            }
        }
    }

    single {
        SetLocaleSettingsUseCase(
            repository = get()
        )
    }

    single {
        GetLocaleSettingsUseCase(
            repository = get()
        )
    }

    single<LocaleSettingsRepository> {
        LocaleSettingsRepositoryImpl(
            localeSettingsService = get()
        )
    }

    single {
        LocaleSettingsService(settingsDataStore = get())
    }

    single<SettingsDataStore> {
        SettingsDataStoreImpl(
            createSettingsLibrary = get()
        )
    }
    single<GoogleAuthenticateUseCaseCommon> {
        GoogleAuthenticateUseCase(
            googleAuthRepository = get(),
        )
    }

    single<GoogleAuthRepositoryCommon> {
        GoogleAuthRepository(
            googleAuthDataSource = get(),
            settingsDataStore = get(),
        )
    }

    single {
        SignUpUseCase(
            signUpRepository = get()
        )
    }

    single<SignUpRepository> {
        SingUpRepositoryImpl(
            signInDataSourceRemote = get()
        )
    }

    single<SignInDataSource> {
        SignInDataSourceRemote(
            client = get(HttpClients.Authenticated),
            unauthenticatedClient = get(HttpClients.Public),
        )
    }

    single<GoogleAuthDataSourceCommon> {
        GoogleAuthDataSourceRemote(
            client = get(HttpClients.Authenticated),
            unauthenticatedClient = get(HttpClients.Public)
        )
    }

    single<TokenProvider> {
        TokenProviderImpl(
            googleAuthRepository = get()
        )
    }
}
