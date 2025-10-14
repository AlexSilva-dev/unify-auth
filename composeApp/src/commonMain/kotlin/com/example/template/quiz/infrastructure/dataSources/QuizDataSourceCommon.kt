package com.example.template.quiz.infrastructure.dataSources

import com.example.template.quiz.api.dtos.TopicDtoApi
import com.example.template.quiz.domain.entities.Quiz
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface QuizDataSourceCommon {
    suspend fun generateQuizzes(topicDtoApi: TopicDtoApi): List<Quiz>
    @OptIn(ExperimentalUuidApi::class)
    suspend fun get(id: Uuid): Quiz?
    @OptIn(ExperimentalUuidApi::class)
    suspend fun getByTopic(idTopic: Uuid): List<Quiz>
}