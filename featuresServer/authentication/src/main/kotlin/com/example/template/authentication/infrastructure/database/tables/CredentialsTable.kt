package com.example.template.authentication.infrastructure.database.tables

import com.example.template.user.database.tables.UsersTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone

object CredentialsTable: UUIDTable(
    name = "credentials"
) {

    val userId = uuid("user_id")
        .references(
            UsersTable.id,
            onDelete = ReferenceOption.CASCADE
        )

    val provider = varchar(
        "provider",
        50
    )

    val providerId = varchar(
        "provider_id",
        255
    ).nullable()

    val secret = text("secret")
        .nullable()

    val createAt = timestampWithTimeZone(
        "create_at"
    )

    val updateAt = timestampWithTimeZone(
        name = "update_at"
    )

    init {
        uniqueIndex(userId, provider)
    }

}