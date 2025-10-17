package com.example.template.authentication.utils

import io.github.cdimascio.dotenv.dotenv


var dotenv = dotenv()

val GOOGLE_CLIENT_ID: String = dotenv["GOOGLE_CLIENT_ID"]
val GOOGLE_KEY_SECRET: String = dotenv["GOOGLE_KEY_SECRET"]
val JWT_SECRET: String = dotenv["JWT_SECRET"]
val JWT_ISSUER: String = dotenv["JWT_ISSUER"]
val JWT_AUDIENCE: String = dotenv["JWT_AUDIENCE"]
