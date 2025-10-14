package com.example.template.authentication.infrastructure.dataSources.local

import com.example.template.authentication.infrastructure.database.tables.CredentialsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class CredentialsDao(
    id: EntityID<UUID>
): UUIDEntity(id) {
    companion object: UUIDEntityClass<CredentialsDao>(CredentialsTable)

    var userId by CredentialsTable.userId
    var provider by CredentialsTable.provider
    var providerId by CredentialsTable.providerId
    var secret by CredentialsTable.secret
    var createAt by CredentialsTable.createAt
    var updateAt by CredentialsTable.updateAt
}