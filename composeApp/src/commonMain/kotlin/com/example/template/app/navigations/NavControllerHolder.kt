package com.example.template.app.navigations

import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface NavControllerHolder {
    val navController: NavController?
    fun update(navController: NavController)
    fun clear()
}

class AppNavControllerHolder : NavControllerHolder {
    private val _navControllerFlow = MutableStateFlow<NavController?>(null)
    val navControllerState: StateFlow<NavController?> = _navControllerFlow

    override val navController: NavController?
        get() = _navControllerFlow.value

    override fun update(navController: NavController) {
        this._navControllerFlow.value = navController
    }

    override fun clear() {
        this._navControllerFlow.value = null
    }
}

