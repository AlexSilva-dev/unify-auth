@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.kotlin.dsl.implementation
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.serialization)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
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
            implementation(libs.core.multiplatform.settings)
            implementation(libs.core.multiplatform.settings.serialization)
            implementation(libs.core.kotlinx.datetime)
            implementation(libs.composeapp.common.icons.tlaster.eva)
            implementation(libs.composeapp.common.icons.extended)


//            implementation(libs.composeapp.common.icons.tlaster.eva)
            implementation(libs.composeapp.common.androidx.navigation)
            implementation(libs.composeapp.common.koin)
            implementation(libs.composeapp.common.koin.viewmodel)
            implementation(libs.composeapp.common.koin.viewmodel.navigation)
            implementation(libs.composeapp.common.window)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.shared)


        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }


        wasmJsMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-browser:0.3")
            implementation(libs.wasmjs.ktor.client)
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}
val envShared = gradleLocalProperties(
    projectRootDir = rootDir,
    providers = providers
)

android {
    namespace = "com.example.unify_auth"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlin {
        jvmToolchain(18)
    }
}

