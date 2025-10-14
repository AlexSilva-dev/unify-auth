package com.example.template.quiz.domain.useCases.interfaces

import com.example.template.quiz.domain.entities.Quiz
import com.example.template.quiz.domain.entities.Topic
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface GetQuizzesByTopicUseCaseCommon {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun execute(idTopic: Uuid): List<Quiz>
}