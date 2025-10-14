package com.example.template.app.domain.entities

/**
 * Uma classe wrapper genérica e padronizada para representar operações que podem
 * resultar em sucesso ou falha.
 *
 * @param T O tipo do dado em caso de sucesso.
 * @param E O tipo do erro em caso de falha.
 */
sealed class Result<out T, out E> {
    /**
     * Representa um resultado de sucesso, contendo os dados da operação.
     * @property data Os dados retornados pela operação.
     */
    data class Success<out T>(val data: T) : Result<T, Nothing>()

    /**
     * Representa um resultado de falha, contendo informações sobre o erro.
     * @property error O objeto de erro que descreve a falha.
     */
    data class Failure<out E>(val error: E) : Result<Nothing, E>()
}
