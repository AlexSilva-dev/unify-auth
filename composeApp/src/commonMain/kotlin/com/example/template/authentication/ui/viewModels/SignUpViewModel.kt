package com.example.template.authentication.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template.app.domain.entities.Failure
import com.example.template.app.domain.entities.Result
import com.example.template.app.ui.viewModels.BaseState
import com.example.template.authentication.domain.entities.EmailValidator
import com.example.template.authentication.domain.entities.SignUpData
import com.example.template.authentication.domain.useCases.SignUpUseCase
import com.example.template.user.domain.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

data class SignUpViewModelData(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isPasswordStrong: Boolean = false,
    val isFormValid: Boolean = false,
    val signInState: BaseState<Unit> = BaseState.Idle
)

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val _signUpViewModelData: MutableStateFlow<SignUpViewModelData>
    = MutableStateFlow(SignUpViewModelData())
) : ViewModel(), KoinComponent {
    val signInViewModelData = this._signUpViewModelData.asStateFlow()

    private fun validateData() {
        val data = _signUpViewModelData.value
        val nameError = if (data.name.length < 2) "Nome muito curto" else null
        val emailError = if (!EmailValidator.isValid(data.email)) "Email inválido" else null
        val passwordError = if (!data.isPasswordStrong) "Senha fraca" else null
        val confirmPasswordError = if (data.password != data.confirmPassword) "As senhas não coincidem" else null

        _signUpViewModelData.value = data.copy(
            nameError = nameError,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            isFormValid = nameError == null && emailError == null && passwordError == null && confirmPasswordError == null && data.confirmPassword.isNotEmpty()
        )
    }

    fun onUpdateName(name: String) {
        if (name.length > 80 || name.contains(Regex("[^a-zA-Z0-9 _-]"))) return

        _signUpViewModelData.value = _signUpViewModelData.value.copy(name = name)
        validateData()
    }

    fun onUpdateEmail(email: String) {
        val charSpecialRegex = Regex("[^a-zA-Z0-9._%+-@]")
        if (email.contains(charSpecialRegex)) return

        _signUpViewModelData.value = _signUpViewModelData.value.copy(email = email)
        validateData()
    }

    fun onUpdatePassword(password: String) {
        _signUpViewModelData.value = _signUpViewModelData.value.copy(password = password)
        validateData()
    }

    fun onUpdatePasswordStrength(isStrong: Boolean) {
        _signUpViewModelData.value = _signUpViewModelData.value.copy(isPasswordStrong = isStrong)
        validateData()
    }

    fun onUpdateConfirmPassword(confirmPassword: String) {
        _signUpViewModelData.value = _signUpViewModelData.value.copy(confirmPassword = confirmPassword)
        validateData()
    }

    fun onSignIn() {
        // Uma última validação antes de tentar o cadastro
        validateData()
        if (!_signUpViewModelData.value.isFormValid) return

        viewModelScope.launch {
            this@SignUpViewModel._signUpViewModelData.value =
                this@SignUpViewModel._signUpViewModelData.value.copy(
                    signInState = BaseState.Loading
                )

            val data: SignUpData = SignUpData(
                name = _signUpViewModelData.value.name,
                email = _signUpViewModelData.value.email,
                password = _signUpViewModelData.value.password,
            )
            val result: Result<User, Failure> = this@SignUpViewModel.signUpUseCase.execute(
                signUpData = data
            )
            when (result) {
                is Result.Success -> {
                    this@SignUpViewModel._signUpViewModelData.value =
                        this@SignUpViewModel._signUpViewModelData.value.copy(
                            signInState = BaseState.Success(Unit)
                        )
                }

                is Result.Failure<*> -> {
                    this@SignUpViewModel._signUpViewModelData.value =
                        this@SignUpViewModel._signUpViewModelData.value.copy(
                            signInState = BaseState.Error(
                                message = null
                            )
                        )
                }
            }

        }
    }

    fun onResetSignInState() {
        _signUpViewModelData.value = _signUpViewModelData.value.copy(signInState = BaseState.Idle)
    }
}

