package com.example.template.quiz.infrastructure.dataSources

import com.example.template.quiz.domain.entities.Topic

interface TopicDataSourceCommon {
    suspend fun save(topic: Topic): Topic
    suspend fun getAll(): List<Topic>
}