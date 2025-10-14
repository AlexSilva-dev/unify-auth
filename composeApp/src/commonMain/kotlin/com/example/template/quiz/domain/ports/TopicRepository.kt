package com.example.template.quiz.domain.ports

import com.example.template.quiz.domain.entities.Topic

interface TopicRepository {
    suspend fun save(topic: Topic): Topic

    suspend fun getAll(): List<Topic>
}