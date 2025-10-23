package com.example.template.app.di

import org.jetbrains.exposed.v1.jdbc.Database
import org.koin.core.module.Module
import org.koin.dsl.module

fun serverModule(database: Database): Module {
    return module {
        single<Database> { database }
    }
}
