package com.example.template.authentication.domain.entities

object EmailValidator {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")

    fun isValid(email: String): Boolean {
        return EMAIL_REGEX.matches(email)
    }
}