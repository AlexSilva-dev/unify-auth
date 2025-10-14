package com.example.template.quiz.domain.useCases

import com.example.template.quiz.domain.entities.Quiz
import com.example.template.quiz.domain.ports.QuizRepository
import com.example.template.quiz.domain.useCases.interfaces.GetQuizUseCaseCommon
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class GetQuizUseCaseImpl(
    private val quizRepository: QuizRepository
) : GetQuizUseCaseCommon {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun execute(id: Uuid): Quiz {
        val quiz: Quiz? = this.quizRepository.get(id)

        if (quiz == null) throw IllegalStateException("Quiz not found")

        return quiz
    }
}