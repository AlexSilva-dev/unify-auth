package com.example.template.app.di

import com.example.template.app.navigations.AppNavControllerHolder
import com.example.template.app.navigations.AuthNavigationHandler
import com.example.template.app.navigations.CommonNavControllerAuthHandler
import com.example.template.app.navigations.NavControllerHolder
import com.example.template.app.ui.viewModels.AppViewModel
import com.example.template.authentication.ui.viewModels.LoginViewModel
import com.example.template.authentication.ui.viewModels.SignUpViewModel
import com.example.template.quiz.ui.viewModels.FlashcardHomeViewModel
import com.example.template.quiz.ui.viewModels.FlashcardViewModel
import com.example.template.quiz.ui.viewModels.TopicsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val moduleCompose: Module = module {
    viewModel {
        AppViewModel(
            setLocaleSettingsUseCase = get(),
            getLocaleSettingsUseCase = get()
        )
    }

    viewModel {
        FlashcardHomeViewModel(
            iaGenerateQuizzesUseCase = get()
        )
    }

    viewModel {
        TopicsViewModel(
            getAllTopicsUseCase = get(),
            authNavigationHandler = get()
        )
    }

    viewModel {
        FlashcardViewModel(
            getQuizzesByTopic = get()
        )
    }

    viewModel {
        SignUpViewModel(
            signUpUseCase = get(),
        )
    }

    viewModel {
        LoginViewModel(
            googleAuthenticateUseCaseCommon = get()
        )
    }

    single {
        CommonNavControllerAuthHandler(
            navController = get(),
            mainScope = get()
        )
    }

    single<NavControllerHolder> { AppNavControllerHolder() }

    factory<AuthNavigationHandler> {
        val holder = get<NavControllerHolder>()
        val currentNavController = holder.navController
        if (currentNavController != null) {
            CommonNavControllerAuthHandler(currentNavController)
        } else {
            // Pode ser chamado antes do NavController estar pronto.
            // Ou você pode lançar um erro, ou retornar um "NoOp" handler
            // e confiar que o ViewModel só usará quando o NavController estiver pronto.
            error("NavController or MainScope not yet available in NavControllerHolder for AuthNavigationHandler")
            // Ou uma implementação "NoOp" para evitar crash na inicialização:
            // object : AuthNavigationHandler { override fun onAuthenticationRequired() { println("WARN: AuthNavigationHandler called before NavController was ready.") } }
        }
    }
}
