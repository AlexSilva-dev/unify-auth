package com.example.template.app.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.template.app.utils.JWT_AUDIENCE
import com.example.template.app.utils.JWT_ISSUER
import com.example.template.app.utils.JWT_SECRET
import com.example.template.user.domain.entities.User
import com.example.template.user.domain.useCases.FindUserByIdUseCase
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import org.koin.ktor.ext.inject

fun Application.authenticationPlugin() {
    val findUserByIdUseCase by inject<FindUserByIdUseCase>()

    install(Authentication) {
        jwt(
            name = "auth-jwt"
        ) {
            realm = "Access to authentication"
            verifier(
                JWT.require(Algorithm.HMAC256(JWT_SECRET))
                    .withIssuer(JWT_ISSUER)
                    .withAudience(JWT_AUDIENCE)
                    .build()
            )
            validate { credential ->

                try {

                    val idUser: String = credential.payload.getClaim("userId").asString()
                    if (idUser.isEmpty()) return@validate null

                    val user: User? = findUserByIdUseCase.execute(
                        id = idUser
                    )
                    if (user == null || user.isActive == false)
                        return@validate null

                    // TODO: criar uma verificaçao se o usersession está ativo ou se foi revogado, para isso precisa criar uma nova coluna e add o id do usersession no jwt para recuperar o registro na tabela

                    JWTPrincipal(credential.payload)
                } catch (e: Exception) {
                    return@validate null
                }
            }
        }
    }
}
