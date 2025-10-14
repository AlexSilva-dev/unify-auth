package com.example.template.app.navigations

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


interface AuthNavigationHandler {
    fun onAuthenticationRequired()
}

// Sua exceção personalizada (também no commonMain)
class AuthenticationRequiredException(message: String = "Authentication required, please login again.") : Exception(message)

// Sua função wrapper executeAuthProtected (também no commonMain)
suspend fun <T> executeAuthProtected(
    authNavigationHandler: AuthNavigationHandler,
    block: suspend () -> T
): Result<T> {
    try {
        return Result.success(block())
    } catch (e: AuthenticationRequiredException) {
        authNavigationHandler.onAuthenticationRequired()
        return Result.failure(e)
    } catch (e: Exception) {
        return Result.failure(e)
    }
}


/**
 * Implementação de [AuthNavigationHandler] que usa o [NavController] comum
 * do `org.jetbrains.androidx.navigation:navigation-compose`.
 *
 * @param navController A instância do NavController comum.
 * @param mainScope Um CoroutineScope para garantir que a navegação ocorra na thread principal,
 *                  especialmente importante para Android. Para Desktop, pode não ser estritamente
 *                  necessário, mas não prejudica.
 */
class CommonNavControllerAuthHandler(
    private val navController: NavController,
    private val mainScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : AuthNavigationHandler {

    override fun onAuthenticationRequired() {
        CoroutineScope(Dispatchers.Main).launch {

        }
        mainScope.launch(Dispatchers.Main) { // Ou um dispatcher de UI específico da plataforma se necessário
            try {
                val startDestinationId = navController.graph.startDestinationId

                navController.navigate(Screen.AuthLogin.route) {
                    popUpTo(startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            } catch (e: IllegalStateException) {
                navController.navigate(Screen.AuthLogin.route) {
                    launchSingleTop = true
                }
            }
        }
    }
}