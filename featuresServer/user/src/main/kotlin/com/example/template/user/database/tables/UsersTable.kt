package com.example.template.user.database.tables

import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone

object UsersTable: UUIDTable() {
    val fullName = varchar(
        "full_name",
        255
    )
    val email = varchar(
        "email",
        255
    )
    val profilePictureUrl = text(
        "profile_picture_url"
    ).nullable()

    val createAt = timestampWithTimeZone(
        "create_at"
    )

    val updateAt = timestampWithTimeZone(
        name = "update_at"
    )

    val isActive = bool(
        "is_active"
    ).default(true)

}