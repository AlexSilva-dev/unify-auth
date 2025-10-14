package com.example.template.app.ui.viewModels

sealed class BaseState<out T> {
    /**
     * Init state
     */
    data object Idle : BaseState<Nothing>()

    data object Loading : BaseState<Nothing>()

    data class Success<T>(val data: T) : BaseState<T>()

    data class Error(val message: String?) : BaseState<Nothing>()
}