package com.example.template.app.plugins

import com.example.template.authentication.api.authenticationRouting
import com.example.template.authentication.api.controllers.AuthenticationController
import io.ktor.server.application.*
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject


fun Application.configureRoutingPlugin() {
    val authenticationController by inject<AuthenticationController>()
    routing {
        route("/api") {
            authenticationRouting(authenticationController)
        }
    }
}