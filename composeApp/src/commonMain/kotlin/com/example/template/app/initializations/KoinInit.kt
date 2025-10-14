package com.example.template.app.initializations

import com.example.template.app.di.moduleCompose
import com.example.template.app.di.moduleCore
import com.example.template.app.di.moduleTarget
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

/**
 * Inicia pelo TargetInitializations
 */
object KoinInit {
    fun execute(
        config: (KoinApplication.() -> Unit)? = null
        ) {
            startKoin {
                config?.invoke(this)
                modules(
                        moduleCompose,
                        moduleCore,
                    moduleTarget
                )
            }
        }
}