package com.example.template.authentication.infrastructure.database

import com.example.template.authentication.domain.ports.TransactionManager
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction

class ExposedTransactionManager(
    private val database: Database
) : TransactionManager {
    override suspend fun <T> invoke(block: suspend () -> T): T {
        return newSuspendedTransaction(db = this@ExposedTransactionManager.database) {
            block()
        }
    }
}