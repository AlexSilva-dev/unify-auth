package com.example.template.authentication.domain.entities

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object AuthConfig {
    val passwordEncoder = BCryptPasswordEncoder(12) // Work factor 12, ajustável (10-14 é comum)
}