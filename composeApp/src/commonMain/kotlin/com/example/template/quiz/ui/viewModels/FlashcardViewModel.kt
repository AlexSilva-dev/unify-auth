package com.example.template.quiz.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.template.app.ui.viewModels.BaseState
import com.example.template.quiz.domain.entities.Quiz
import com.example.template.quiz.domain.useCases.interfaces.GetQuizzesByTopicUseCaseCommon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class FlashcardData @OptIn(ExperimentalUuidApi::class) constructor(
    val quizzes: List<Quiz> = listOf(),
    val quizCurrent: Quiz? = quizzes.firstOrNull(),
    val quizzesToReview: List<Quiz> = listOf(),
    val seeResult: Boolean = false,
    val selectedIndex: Int = -1,
)

class FlashcardViewModel(
    private val getQuizzesByTopic: GetQuizzesByTopicUseCaseCommon
) : ViewModel(), KoinComponent {
    private val _flashcardData: MutableStateFlow<FlashcardData> = MutableStateFlow(FlashcardData())
    val flashcardData: StateFlow<FlashcardData> = this._flashcardData.asStateFlow()

    private val _onGetQuizzesUiState: MutableStateFlow<BaseState<Unit>> = MutableStateFlow(BaseState.Idle)
    val onGetQuizzesUiState: StateFlow<BaseState<Unit>> = this._onGetQuizzesUiState.asStateFlow()


    @OptIn(ExperimentalUuidApi::class)
    fun onGetQuizzesByTopic(idTopic: String) {
        val id: Uuid = Uuid.parse(idTopic)

        viewModelScope.launch {
            this@FlashcardViewModel._onGetQuizzesUiState.value = BaseState.Loading

            try {
                val quizzes: List<Quiz> = this@FlashcardViewModel
                    .getQuizzesByTopic.execute(idTopic = id)

                this@FlashcardViewModel._flashcardData.update {
                    it.copy(
                        quizzes = quizzes,
                        quizzesToReview = quizzes
                    )
                }
                this@FlashcardViewModel._onGetQuizzesUiState.value = BaseState.Success(Unit)
            } catch (e: Exception) {
                println(e.message)
                this@FlashcardViewModel._onGetQuizzesUiState.value = BaseState.Error(e.message)
            }
        }
    }

    fun onGetRandomQuiz() {
        viewModelScope.launch {
            if (this@FlashcardViewModel._flashcardData.value.quizzesToReview.isEmpty()) {
                this@FlashcardViewModel._flashcardData.update {
                    it.copy(
                        quizzesToReview = it.quizzes
                    )
                }
            }
            val randomQuiz: Quiz = this@FlashcardViewModel._flashcardData.value.quizzesToReview.random()
            this@FlashcardViewModel._flashcardData.update { it ->
                it.copy(
                    quizCurrent = randomQuiz,
                    quizzesToReview = it.quizzesToReview.filter { it != randomQuiz }
                )
            }
        }
    }

    fun onSeeResult() {
        viewModelScope.launch {
            this@FlashcardViewModel._flashcardData.update {
                it.copy(
                    seeResult = true
                )
            }
        }
    }

    fun onNextQuiz() {
        viewModelScope.launch {
            this@FlashcardViewModel._flashcardData.update {
                it.copy(
                    seeResult = false,
                    selectedIndex = -1
                )
            }
        }
    }

    fun onSelectedIndex(index: Int) {
        viewModelScope.launch {
            this@FlashcardViewModel._flashcardData.update {
                it.copy(
                    selectedIndex = index
                )
            }
        }
    }
}