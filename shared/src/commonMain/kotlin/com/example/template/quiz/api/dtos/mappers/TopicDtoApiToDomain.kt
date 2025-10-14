package com.example.template.quiz.api.dtos.mappers

import com.example.template.quiz.api.dtos.TopicDtoApi
import com.example.template.quiz.domain.entities.Topic
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
fun TopicDtoApi.toDomain(): Topic {
    return Topic(
        id = this.id,
        text = this.text,
        quizzes = this.quizzes.map {
            it.toDomain()
        },
        createdAt = this.createdAt?.let { Instant.parse(it) },
        updatedAt = this.updatedAt?.let { Instant.parse(it) },
    )
}

