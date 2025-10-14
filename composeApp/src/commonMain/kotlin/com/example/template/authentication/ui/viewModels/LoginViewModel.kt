package com.example.template.authentication.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template.app.navigations.Screen
import com.example.template.app.ui.viewModels.BaseState
import com.example.template.authentication.domain.useCases.interfaces.GoogleAuthenticateUseCaseCommon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

data class LoginData(
    val email: String = "",
    val password: String = "",

)

class LoginViewModel(
    private val googleAuthenticateUseCaseCommon: GoogleAuthenticateUseCaseCommon,
) : ViewModel(), KoinComponent {

    private val _loginData: MutableStateFlow<LoginData> = MutableStateFlow(LoginData())
    val loginData: StateFlow<LoginData> = this._loginData.asStateFlow()

    private val _onLoginState = MutableStateFlow<BaseState<Unit>>(BaseState.Idle)
    val onLoginState = this._onLoginState.asStateFlow()

    fun onLoginWithGoogle(any: Any) {
        viewModelScope.launch {
            try {
                val result: StateFlow<BaseState<GoogleAuthorization>> = MutableStateFlow(BaseState.Idle)

                result.collect { state ->
                    when (state) {
                        is BaseState.Error -> {
                            this@LoginViewModel._onLoginState.value =
                                BaseState.Error(state.message)
                            return@collect
                        }
                        is BaseState.Success -> {

                            val googleAuthorization: GoogleAuthorization = state.data

                            this@LoginViewModel._onLoginState.value = BaseState.Loading
                            val isAuthenticated: Boolean = this@LoginViewModel.googleAuthenticateUseCaseCommon.execute(
                                googleAuthorization = googleAuthorization
                            )

                            if (!isAuthenticated) {
                                this@LoginViewModel._onLoginState.value =
                                    BaseState.Error("Deu errado, não autenticado")
                                return@collect
                            }

                            this@LoginViewModel._onLoginState.value = BaseState.Success<Unit>(
                                data = Unit
                            )
                        }
                        else -> {
                            this@LoginViewModel._onLoginState.value = BaseState.Loading
                        }
                    }
                }

            } catch (e: Exception) {
                println(e.message)
                this@LoginViewModel._onLoginState.value = BaseState.Error(
                    e.message ?: "Erro inesperado"
                )
                return@launch
            }
        }
    }

    fun onResetLoginState() {
        this._onLoginState.value = BaseState.Idle
    }
}