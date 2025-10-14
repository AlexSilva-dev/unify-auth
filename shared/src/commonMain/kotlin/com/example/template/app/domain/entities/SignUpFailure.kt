package com.example.template.app.domain.entities

sealed class SignUpFailure: Failure() {
    // Erros de Validação Adicionados
    data object InvalidPassword : Failure()
    data object InvalidName : Failure()
    data object EmailAlreadyExists : SignUpFailure()
    data object InvalidEmail : SignUpFailure()
}