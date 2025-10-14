package com.example.template.authentication.api

import com.example.template.authentication.api.controllers.AuthenticationController
import io.ktor.server.application.Application
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.authenticationRouting(string: String, function: () -> Unit) {
    val authenticationController by inject<AuthenticationController>()

    routing(

    ) {
        route(
            path = "/auth"
        ) {

            route(
                path = "/login"
            ) {
                route(
                    path = "/refresh-token"
                ) {
                    post {
                        authenticationController.refreshToken(call)
                    }
                }

                route(
                    path = "/google/token"
                ) {
                    post {
                        authenticationController.tokenAuthenticationWithGoogle(call)
                    }
                }

                route(
                    path = "/google/authorization-code"
                ) {
                    post {
                        authenticationController.codeAuthenticationWithGoogle(call)
                    }
                }
            }

            route(
                path = "signup"
            ) {
                post {
                    authenticationController.signUp(call)
                }
            }

        }
    }
}