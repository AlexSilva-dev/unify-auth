package com.example.template.authentication.infrastructure.dataSources.remote

import com.example.template.app.domain.entities.Failure
import com.example.template.app.domain.entities.Result
import com.example.template.authentication.domain.entities.AuthenticationCredentialsProvider
import com.example.template.authentication.domain.entities.AuthorizationCode
import com.example.template.authentication.domain.entities.UserInfo
import com.example.template.authentication.infrastructure.dataSources.GoogleAuthenticationDataSource
import com.example.template.authentication.utils.GOOGLE_CLIENT_ID
import com.example.template.authentication.utils.GOOGLE_KEY_SECRET
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory


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


    override suspend fun getIdTokenByAuthorizationCode(authorizationCode: AuthorizationCode): Result<String, Failure> {
        try {
            val tokenResponse = GoogleAuthorizationCodeTokenRequest(
                /* transport = */ NetHttpTransport(),
                /* jsonFactory = */ GsonFactory.getDefaultInstance(),
                /* tokenServerEncodedUrl = */ "https://oauth2.googleapis.com/token",
                /* clientId = */ googleClientId,
                /* clientSecret = */ googleClientSecret, // O segredo do seu cliente web
                /* code = */ authorizationCode.authorizationCode,
                /* redirectUri = */ authorizationCode.url,
            ).execute()

            val idTokenString = tokenResponse.idToken
            return Result.Success(idTokenString)

        } catch (e: Exception) {
            println("Falha no fluxo de autenticação do Google: ${e.message}")
            println(e.cause)
            return Result.Failure(Failure.UnknownError(e))
        }
    }

}