import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.serialization)
}

kotlin {

    jvm()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.composeapp.common.ktor.client.auth)
            implementation(libs.core.multiplatform.settings)
            implementation(libs.core.multiplatform.settings.serialization)
            implementation(libs.core.kotlinx.datetime)
            implementation(libs.core.ktor.client.logging)
            implementation(libs.core.ktor.client.serialization)
            implementation(libs.core.ktor.client.content.negotiation)
            implementation(libs.core.ktor.serialization.kotlinx.json)
            implementation(libs.core.ktor.client)
            implementation(libs.core.kotlinx.coroutines)
            implementation(libs.common.koin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmMain.dependencies {
        }


        wasmJsMain.dependencies {
        }


    }
}


buildkonfig {
    packageName = "com.example.template.app"

    val envShared = gradleLocalProperties(
        projectRootDir = rootDir,
        providers = providers
    )

    defaultConfigs {

        buildConfigField(
            type = FieldSpec.Type.STRING,
            name = "SERVER_PORT",
            value = envShared.getProperty("dev.server.port")
        )

        buildConfigField(
            type = FieldSpec.Type.STRING,
            name = "SERVER_HOST",
            value = envShared.getProperty("dev.server.host")
        )

        buildConfigField(
            type = FieldSpec.Type.STRING,
            name = "SERVER_PROTOCOL",
            value = envShared.getProperty("dev.server.protocol")
        )


    }

    targetConfigs {
        create("release") {

            buildConfigField(
                type = FieldSpec.Type.STRING,
                name = "SERVER_PORT",
                value = envShared.getProperty("server.port")
            )

            buildConfigField(
                type = FieldSpec.Type.STRING,
                name = "SERVER_HOST",
                value = envShared.getProperty("server.host")
            )

            buildConfigField(
                type = FieldSpec.Type.STRING,
                name = "SERVER_PROTOCOL",
                value = envShared.getProperty("server.protocol")
            )
        }
    }
}