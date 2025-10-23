package com.example.template.app.plugins

import com.example.template.app.di.serverModule
import com.example.template.authentication.di.authenticationModule
import com.example.template.user.di.userModule
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.koinPlugin(database: Database) {
    install(Koin) {
        slf4jLogger()
        modules(
            serverModule(database = database),
            authenticationModule,
            userModule
        )
    }
}
