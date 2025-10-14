package com.example.template.app.utils

import io.github.cdimascio.dotenv.dotenv


val dotenv = dotenv()

val IS_DEVELOPMENT: Boolean = System.getProperty("is_development").toBoolean()

val DATABASE_DRIVER: String? = dotenv["DATABASE_DRIVER"]
val DATABASE_URL: String? = dotenv["DATABASE_URL"]
val DATABASE_USER: String? = dotenv["DATABASE_USER"]
val DATABASE_PASSWORD: String? = dotenv["DATABASE_PASSWORD"]

val GEMINI_API_KEY: String = dotenv["GEMINI_API_KEY"]

val GOOGLE_CLIENT_ID: String = dotenv["GOOGLE_CLIENT_ID"]
val GOOGLE_KEY_SECRET: String = dotenv["GOOGLE_KEY_SECRET"]

val JWT_SECRET: String = dotenv["JWT_SECRET"]
val JWT_ISSUER: String = dotenv["JWT_ISSUER"]
val JWT_AUDIENCE: String = dotenv["JWT_AUDIENCE"]
