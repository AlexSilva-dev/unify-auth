package com.example.template.quiz.domain.useCases

import com.example.template.quiz.domain.entities.Topic
import com.example.template.quiz.domain.ports.TopicRepository
import com.example.template.quiz.domain.useCases.interfaces.GetAllTopicsUseCaseCommon

class GetAllTopicsUseCase(
    private val topicRepository: TopicRepository
): GetAllTopicsUseCaseCommon {

    override suspend fun execute(): List<Topic> {
        val topics = this.topicRepository.getAll()
        return topics
    }
}