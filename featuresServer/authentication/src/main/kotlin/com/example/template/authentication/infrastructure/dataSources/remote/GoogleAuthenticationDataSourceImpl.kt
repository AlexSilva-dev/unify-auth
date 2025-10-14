package com.example.template.authentication.infrastructure.dataSources.remote

import com.example.template.authentication.GOOGLE_CLIENT_ID
import com.example.template.authentication.GOOGLE_KEY_SECRET
import com.example.template.authentication.domain.entities.AuthenticationCredentialsProvider
import com.example.template.authentication.domain.entities.AuthorizationCode
import com.example.template.authentication.domain.entities.UserInfo
import com.example.template.authentication.infrastructure.dataSources.GoogleAuthenticationDataSource
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GoogleAuthenticationDataSourceImpl(
    private val googleClientId: String? = GOOGLE_CLIENT_ID,
    private val googleClientSecret: String? = GOOGLE_KEY_SECRET,
    private val verifier: GoogleIdTokenVerifier = GoogleIdTokenVerifier.Builder(
        NetHttpTransport(),
        GsonFactory()
    )
        .setAudience(listOf(googleClientId))
        .build()
) : GoogleAuthenticationDataSource {
    override suspend fun buildUserInfoByToken(token: String): UserInfo? {
        if (googleClientId.isNullOrEmpty()) throw IllegalArgumentException(
            "Google Client Id not found"
        )
        val data: GoogleIdToken = this.verifier.verify(token) ?: return null

        val payload = data.payload
        val userInfo = UserInfo(
            providerId = payload.subject,
            name = payload.get("name") as String,
            email = payload.email,
            profilePictureUrl = payload.get("picture") as String,
            provider = AuthenticationCredentialsProvider.GOOGLE,
        )
        return userInfo
    }


    // A função agora recebe o CÓDIGO, não o token
    override suspend fun buildUserInfoByAuthorizationCode(authorizationCode: AuthorizationCode): UserInfo? {
        return withContext(Dispatchers.IO) { // Mover operações de rede para o dispatcher de I/O
            try {
                val tokenResponse = GoogleAuthorizationCodeTokenRequest(
                    /* transport = */ NetHttpTransport(),
                    /* jsonFactory = */ GsonFactory.getDefaultInstance(),
                    /* tokenServerEncodedUrl = */ "https://oauth2.googleapis.com/token",
                    /* clientId = */ googleClientId,
                    /* clientSecret = */ googleClientSecret, // O segredo do seu cliente web
                    /* code = */ authorizationCode.token,
                    /* redirectUri = */ authorizationCode.url,
                ).execute()

                val idTokenString = tokenResponse.idToken

                val idToken: GoogleIdToken? = this@GoogleAuthenticationDataSourceImpl.verifier.verify(idTokenString)

                if (idToken != null) {
                    val payload = idToken.payload
                    UserInfo(
                        providerId = payload.subject,
                        name = payload["name"] as? String ?: "",
                        email = payload.email,
                        profilePictureUrl = payload["picture"] as? String,
                        provider = AuthenticationCredentialsProvider.GOOGLE,
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                println("Falha no fluxo de autenticação do Google: ${e.message}")
                println(e.cause)
                null
            }
        }
    }

}