package com.example.template.authentication.ui.viewModels

sealed class GoogleSignInResult {
    data class Success(val userInfo: GoogleAuthorization): GoogleSignInResult()
    data class Error(val message: String): GoogleSignInResult()
}