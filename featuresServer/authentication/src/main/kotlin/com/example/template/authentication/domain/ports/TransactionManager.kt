package com.example.template.authentication.domain.ports

interface TransactionManager {
    suspend operator fun <T> invoke(block: suspend () -> T): T
}