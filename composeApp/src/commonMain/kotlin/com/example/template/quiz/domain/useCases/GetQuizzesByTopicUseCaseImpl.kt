package com.example.template.quiz.domain.useCases

import com.example.template.quiz.domain.entities.Quiz
import com.example.template.quiz.domain.ports.QuizRepository
import com.example.template.quiz.domain.useCases.interfaces.GetQuizzesByTopicUseCaseCommon
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class GetQuizzesByTopicUseCaseImpl(
    private val quizRepository: QuizRepository,
): GetQuizzesByTopicUseCaseCommon {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun execute(idTopic: Uuid): List<Quiz> {
        val quizzes: List<Quiz> = this.quizRepository.getByTopic(idTopic = idTopic)
        return quizzes
    }

}