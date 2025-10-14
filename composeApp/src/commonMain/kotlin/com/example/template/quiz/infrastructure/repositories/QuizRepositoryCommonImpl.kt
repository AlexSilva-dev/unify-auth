package com.example.template.quiz.infrastructure.repositories

import com.example.template.quiz.api.dtos.mappers.toDtoApi
import com.example.template.quiz.domain.entities.Quiz
import com.example.template.quiz.domain.ports.QuizRepository
import com.example.template.quiz.infrastructure.dataSources.QuizDataSourceCommon
import com.example.template.quiz.domain.entities.Topic
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class QuizRepositoryCommonImpl(
    val quizDataSource: QuizDataSourceCommon
): QuizRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun aiCreateQuizzes(topic: Topic): List<Quiz> {
        this.quizDataSource.generateQuizzes(
            topicDtoApi = topic.toDtoApi()
        )

        return listOf()
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun get(id: Uuid): Quiz? {
        val quiz: Quiz? = this.quizDataSource.get(id = id)
        return quiz
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getByTopic(idTopic: Uuid): List<Quiz> {
        return this.quizDataSource.getByTopic(idTopic = idTopic)
    }
}