package com.example.template.app.navigations

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.template.app.ui.layouts.CleanLayout
import com.example.template.authentication.ui.screens.LoginScreen
import com.example.template.authentication.ui.screens.SignUpScreen
import org.koin.compose.koinInject
import kotlin.uuid.ExperimentalUuidApi


@OptIn(ExperimentalUuidApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val navBlackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute: String? = navBlackStackEntry?.destination?.route

    val onNavigate: (route: String) -> Unit = { route ->
        navController.navigate(route)
    }
    // Este efeito garante que o listener seja configurado uma vez e removido quando a tela sair
    DisposableEffect(Unit) {
        ExternalUriHandler.listener = { uri ->
            navController.navigate(NavUri(uri))
        }

        // Limpa o listener para evitar memory leaks
        onDispose {
            ExternalUriHandler.listener = null
        }
    }

    val navControllerHolder = koinInject<NavControllerHolder>() // Injeta o holder

    DisposableEffect(navController, navControllerHolder) {
        navControllerHolder.update(navController)
        onDispose {
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.AuthLogin.route,
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {

        composable(
            route = Screen.SignIn.route
        ) {
            CleanLayout(
                onNavigate = onNavigate,
                currentRoute = currentRoute
            ) {
                SignUpScreen(
                    onLoginNavigate = {
                        navController.navigate(Screen.AuthLogin.route)
                    },
                )
            }
        }

        composable(
            route = Screen.AuthLogin.route
        ) { backStackEntry ->
            CleanLayout(
                onNavigate = onNavigate,
                currentRoute = currentRoute
            ) {
                LoginScreen(
                    onHomeNavigate = {
                        navController.navigate(Screen.Topics.route)
                    },
                    onSignInNavigate = {
                        navController.navigate(Screen.SignIn.route)
                    }
                )
            }
        }

    }
}