package com.example.template.authentication.infrastructure.database.tables

import com.example.template.user.database.tables.UsersTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone

object UserSessionsTable: UUIDTable(
    name = "user_sessions"
) {

    val userId = uuid("user_id")
        .references(
            UsersTable.id,
            onDelete = ReferenceOption.CASCADE
        )

    val accessToken = text("access_token")

    val refreshToken = text("refresh_token")

    val createAt = timestampWithTimeZone(
        "create_at"
    )

    val updateAt = timestampWithTimeZone(
        name = "update_at"
    )
}