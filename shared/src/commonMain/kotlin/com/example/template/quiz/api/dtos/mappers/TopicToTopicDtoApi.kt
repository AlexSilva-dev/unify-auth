package com.example.template.quiz.api.dtos.mappers

import com.example.template.quiz.api.dtos.TopicDtoApi
import com.example.template.quiz.domain.entities.Topic
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
fun Topic.toDtoApi(): TopicDtoApi {
    return TopicDtoApi(
        id = this.id,
        text = this.text ?: throw IllegalArgumentException("Text cannot be null"),
        quizzes = this.quizzes.map {
            it.toDtoApi()
        },
        createdAt = this.createdAt?.toString(),
        updatedAt = this.updatedAt?.toString()
    )
}

