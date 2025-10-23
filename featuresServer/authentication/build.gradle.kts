plugins {
    alias(libs.plugins.kotlinJvm)
}

group = "com.example.template.authentication"
version = "1.0.0"
dependencies {
    implementation(libs.server.google.api.client)
    implementation(libs.server.spring.security.crypto)
    implementation(libs.server.ktor.auth.jwt)
    implementation(libs.server.ktor.auth)

    implementation(libs.server.dotenv)
    implementation(libs.server.koin)

    implementation(libs.server.exposed.datetime)
    implementation(libs.server.expose.core)
    implementation(libs.server.expose.jdbc)
    implementation(libs.server.expose.dao)

    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)

    implementation(projects.shared)
    implementation(project(":featuresServer:user"))

    testImplementation(libs.test.mockk)
    testImplementation(libs.test.coroutines)
    testImplementation(libs.test.junit.jupiter.api)
    testRuntimeOnly(libs.test.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
}