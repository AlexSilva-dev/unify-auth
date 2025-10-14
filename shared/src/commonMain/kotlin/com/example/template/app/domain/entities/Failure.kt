package com.example.template.app.domain.entities

/**
 * Representa os tipos de falhas padronizadas que podem ocorrer na aplicação.
 * Esta classe selada permite um tratamento de erro explícito e type-safe.
 */
sealed class Failure {
    /**
     * Indica uma falha de conectividade de rede (ex: sem internet, DNS não resolvido).
     */
    data object NetworkError : Failure()

    /**
     * Indica um erro que ocorreu no servidor (ex: status HTTP 5xx ou 4xx não tratados especificamente).
     * @property code O código de status HTTP do erro.
     * @property message Uma mensagem opcional vinda do servidor.
     */
    data class ServerError(val code: Int, val message: String?) : Failure()

    /**
     * Indica um erro de autorização (status HTTP 401), significando que a sessão do usuário
     * é inválida ou expirou e uma nova autenticação é necessária.
     */
    data object UnauthorizedError : Failure()

    /**
     * Representa uma exceção inesperada ou não mapeada que ocorreu durante a operação.
     * @property exception A exceção original capturada, útil para logging e debug.
     */
    data class UnknownError(val exception: Throwable) : Failure()
}
