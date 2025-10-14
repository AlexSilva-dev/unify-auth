package com.example.template.authentication.infrastructure.dataSources.local

import com.example.template.authentication.infrastructure.database.tables.UserSessionsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class UserSessionDao(
    id: EntityID<UUID>
): UUIDEntity(id) {
    companion object : UUIDEntityClass<UserSessionDao>(UserSessionsTable)

    var userId by UserSessionsTable.userId
    var accessToken by UserSessionsTable.accessToken
    var refreshToken by UserSessionsTable.refreshToken
    var createAt by UserSessionsTable.createAt
    var updateAt by UserSessionsTable.updateAt
}