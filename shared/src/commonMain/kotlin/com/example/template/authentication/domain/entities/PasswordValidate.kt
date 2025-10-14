package com.example.template.authentication.domain.entities

object PasswordValidate {
    /**
     * Regex para validação de senha.
     * Requisitos:
     * - ^: Início da string.
     * - (?=.*[0-9]): Pelo menos um dígito.
     * - (?=.*[a-z]): Pelo menos uma letra minúscula.
     * - (?=.*[A-Z]): Pelo menos uma letra maiúscula.
     * - (?=.*[@#$%^&+=!]): Pelo menos um caractere especial.
     * - (?=\S+$): Sem espaços em branco.
     * - .{8,}: Mínimo de 8 caracteres.
     * - $: Fim da string.
     */
    private val PASSWORD_REGEX = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$")

    fun isValid(password: String): Boolean {
        return PASSWORD_REGEX.matches(password)
    }
}