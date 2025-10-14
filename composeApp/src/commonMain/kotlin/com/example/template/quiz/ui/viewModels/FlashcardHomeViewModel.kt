package com.example.template.quiz.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template.app.ui.viewModels.BaseState
import com.example.template.quiz.domain.entities.Topic
import com.example.template.quiz.domain.useCases.AiGenerateQuizzesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

data class FlashcardHomeData @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class) constructor(
    val topic: Topic = Topic(
        id = null,
        text = "",
        quizzes = listOf()
    )
)

class FlashcardHomeViewModel(
    private val iaGenerateQuizzesUseCase: AiGenerateQuizzesUseCase
) : ViewModel(), KoinComponent {
    private val _flashcardHomeData = MutableStateFlow<FlashcardHomeData>(FlashcardHomeData())
    val flashcardHomeData: StateFlow<FlashcardHomeData> = _flashcardHomeData.asStateFlow()

    private val _createQuizzesState = MutableStateFlow<BaseState<Unit>>(BaseState.Idle)
    val createQuizzesState = this._createQuizzesState.asStateFlow()

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    fun onChangeStudyTopic(topicText: String) {
        viewModelScope.launch {
            this@FlashcardHomeViewModel._flashcardHomeData.update {
                it.copy(
                    topic = Topic(
                        id = null,
                        text = topicText,
                        quizzes = listOf()
                    )
                )
            }
        }
    }

    /**
     * Create quizzes based on the topic
     */
    fun onCreateQuizzes() {
        if (this._createQuizzesState.value is BaseState.Loading) return

        viewModelScope.launch {
            this@FlashcardHomeViewModel._createQuizzesState.value = BaseState.Loading
            try {
                val result: Topic = this@FlashcardHomeViewModel.iaGenerateQuizzesUseCase.execute(
                    topic = this@FlashcardHomeViewModel._flashcardHomeData.value.topic
                )
                this@FlashcardHomeViewModel._createQuizzesState.value = BaseState.Success(Unit)
            } catch (e: Exception) {
                this@FlashcardHomeViewModel._createQuizzesState.value = BaseState.Error(e.message)
            }
        }
    }

    /**
     * Action for state reset
     */
    fun onCreateQuizzesActionCompleted() {
        this._createQuizzesState.value = BaseState.Idle
    }
}