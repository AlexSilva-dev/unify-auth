package com.example.template.quiz.domain.useCases

import com.example.template.quiz.domain.entities.Quiz
import com.example.template.quiz.domain.entities.Topic
import com.example.template.quiz.domain.ports.QuizRepository
import com.example.template.quiz.domain.ports.TopicRepository
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

class AiGenerateQuizzesUseCase(
    private val quizRepository: QuizRepository,
    private val topicRepository: TopicRepository
) {
    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    suspend fun execute(topic: Topic): Topic {
        val topicSaved: Topic = this.topicRepository.save(
            topic = topic
        )

        val quizzes: List<Quiz> = this.quizRepository.aiCreateQuizzes(
            topic = topicSaved
        )

        val topicResult: Topic = topic.copy(quizzes = quizzes)
        return topicResult
    }
}