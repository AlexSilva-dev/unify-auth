package com.example.template.quiz.domain.useCases.interfaces

import com.example.template.quiz.domain.entities.Quiz
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface GetQuizUseCaseCommon {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun execute(id: Uuid): Quiz
}