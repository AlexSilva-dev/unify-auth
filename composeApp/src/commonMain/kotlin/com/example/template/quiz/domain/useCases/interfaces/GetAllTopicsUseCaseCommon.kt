package com.example.template.quiz.domain.useCases.interfaces

import com.example.template.quiz.domain.entities.Topic

interface GetAllTopicsUseCaseCommon {
    suspend fun execute(): List<Topic>
}