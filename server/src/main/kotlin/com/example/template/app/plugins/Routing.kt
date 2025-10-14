package com.example.template.app.plugins

import com.example.template.authentication.api.authenticationRouting
import io.ktor.server.application.*


fun Application.configureRoutingPlugin() {
    authenticationRouting("auth-jwt") {
    }
}