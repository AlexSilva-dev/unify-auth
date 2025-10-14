package com.example.template.authentication

import io.github.cdimascio.dotenv.Dotenv


var dotenv = Dotenv.configure()
    .directory("./server")
    .ignoreIfMalformed()
    .load()

val GOOGLE_CLIENT_ID: String = dotenv["GOOGLE_CLIENT_ID"]
val GOOGLE_KEY_SECRET: String = dotenv["GOOGLE_KEY_SECRET"]
val JWT_SECRET: String = dotenv["JWT_SECRET"]
val JWT_ISSUER: String = dotenv["JWT_ISSUER"]
val JWT_AUDIENCE: String = dotenv["JWT_AUDIENCE"]
