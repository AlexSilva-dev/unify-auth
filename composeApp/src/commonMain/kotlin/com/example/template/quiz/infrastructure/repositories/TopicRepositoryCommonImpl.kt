package com.example.template.quiz.infrastructure.repositories

import com.example.template.quiz.domain.entities.Topic
import com.example.template.quiz.domain.ports.TopicRepository
import com.example.template.quiz.infrastructure.dataSources.TopicDataSourceCommon

class TopicRepositoryCommonImpl(
    private val topicDataSourceCommon: TopicDataSourceCommon
): TopicRepository {
    override suspend fun save(topic: Topic): Topic {
        return this.topicDataSourceCommon.save(
            topic = topic
        )
    }

    override suspend fun getAll(): List<Topic> {
        val topics: List<Topic> = this.topicDataSourceCommon.getAll()
        return topics
    }
}