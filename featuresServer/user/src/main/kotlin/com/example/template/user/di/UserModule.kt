package com.example.template.user.di

import com.example.template.user.domain.ports.UserRepository
import com.example.template.user.infrastructure.repositories.UserRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val userModule: Module = module {
    single<UserRepository> {
        UserRepositoryImpl(
        )
    }
}