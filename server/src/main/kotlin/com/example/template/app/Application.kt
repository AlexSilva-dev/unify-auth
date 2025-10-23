package com.example.template.app

import com.example.template.app.plugins.*
import com.example.template.app.utils.SERVER_PORT
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.v1.jdbc.Database

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

/**
 * A ordem da inicialização importa
 */
fun Application.module() {
    corsPlugin()
    val database: Database = configureDatabasePlugin()
    koinPlugin(database = database)
    authenticationPlugin()
    contentNegotiationPlugin()
    configureRoutingPlugin()
}