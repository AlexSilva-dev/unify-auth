package com.example.template.authentication.api

import com.example.template.authentication.api.controllers.AuthenticationController
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.authenticationRouting(authenticationController: AuthenticationController) {
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
                path = "/google"
            ) {
                post {
                    authenticationController.tokenAuthenticationWithGoogle(call)
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