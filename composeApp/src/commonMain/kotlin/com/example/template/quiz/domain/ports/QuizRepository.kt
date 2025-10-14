package com.example.template.quiz.domain.ports

import com.example.template.quiz.domain.entities.Quiz
import com.example.template.quiz.domain.entities.Topic
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface QuizRepository {
    suspend fun aiCreateQuizzes(topic: Topic): List<Quiz>
    @OptIn(ExperimentalUuidApi::class)
    suspend fun get(id: Uuid): Quiz?

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getByTopic(idTopic: Uuid): List<Quiz>
}