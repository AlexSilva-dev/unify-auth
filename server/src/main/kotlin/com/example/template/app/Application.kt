package com.example.template.app

import com.example.template.app.plugins.authenticationPlugin
import com.example.template.app.plugins.configureDatabasePlugin
import com.example.template.app.plugins.configureRoutingPlugin
import com.example.template.app.plugins.contentNegotiationPlugin
import com.example.template.app.plugins.corsPlugin
import com.example.template.app.plugins.koinPlugin
import com.example.template.app.utils.SERVER_PORT
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

/**
 * A ordem da inicialização importa
 */
fun Application.module() {
    corsPlugin()
    koinPlugin()
    authenticationPlugin()
    contentNegotiationPlugin()
    configureRoutingPlugin()
    configureDatabasePlugin()
}