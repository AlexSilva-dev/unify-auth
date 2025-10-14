package com.example.template.quiz.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template.app.navigations.AuthNavigationHandler
import com.example.template.app.navigations.AuthenticationRequiredException
import com.example.template.app.navigations.executeAuthProtected
import com.example.template.app.ui.viewModels.BaseState
import com.example.template.quiz.domain.entities.Topic
import com.example.template.quiz.domain.useCases.interfaces.GetAllTopicsUseCaseCommon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

data class TopicsData @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class) constructor(
    val topics: List<Topic> = listOf<Topic>()
)
class TopicsViewModel(
    private val getAllTopicsUseCase: GetAllTopicsUseCaseCommon,
    private val authNavigationHandler: AuthNavigationHandler,
): ViewModel(), KoinComponent {

    private val _topicsData: MutableStateFlow<TopicsData> =
        MutableStateFlow<TopicsData>(TopicsData())
    val topicsData: StateFlow<TopicsData> = this._topicsData.asStateFlow()

    private val _onGetAllTopicsState = MutableStateFlow<BaseState<Unit>>(BaseState.Idle)
    val onGetAllTopicsState = this._onGetAllTopicsState.asStateFlow()

    init {
        this.onGetAllTopics()
    }


    fun onGetAllTopics() {
        viewModelScope.launch {
            this@TopicsViewModel._onGetAllTopicsState.value = BaseState.Loading

            val topicsResult: Result<List<Topic>> = executeAuthProtected(
                authNavigationHandler = this@TopicsViewModel.authNavigationHandler
            ) {
                this@TopicsViewModel.getAllTopicsUseCase.execute()
            }

            topicsResult.fold(
                onSuccess = { topicsList ->
                    this@TopicsViewModel._topicsData.update { currentData ->
                        currentData.copy(topics = topicsList)
                    }
                    this@TopicsViewModel._onGetAllTopicsState.value = BaseState.Success(Unit)
                },
                onFailure = { exception ->
                    if (exception is AuthenticationRequiredException) {
                        println("Redirecionando para login devido a: ${exception.message}")
                    } else {
                        this@TopicsViewModel._onGetAllTopicsState.value =
                            BaseState.Error(exception.message ?: "Erro desconhecido")
                    }
                }
            )
        }
    }
}