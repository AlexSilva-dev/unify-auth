package com.example.template.user.infrastructure.dataSource.local

import com.example.template.user.database.tables.UsersTable
import com.example.template.user.domain.entities.User
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class UserDao(
    id: EntityID<UUID>
): UUIDEntity(id) {

    companion object : UUIDEntityClass<UserDao>(UsersTable)
    var fullName by UsersTable.fullName
    var email by UsersTable.email
    var profilePictureUrl by UsersTable.profilePictureUrl
    var createAt by UsersTable.createAt
    var updateAt by UsersTable.updateAt
}