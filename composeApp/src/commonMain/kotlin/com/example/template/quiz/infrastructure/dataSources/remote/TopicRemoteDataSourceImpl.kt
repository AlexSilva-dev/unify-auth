package com.example.template.quiz.infrastructure.dataSources.remote

import com.example.template.app.navigations.AuthenticationRequiredException
import com.example.template.app.utils.SERVER_HOST
import com.example.template.app.utils.SERVER_PORT
import com.example.template.app.utils.SERVER_PROTOCOL
import com.example.template.authentication.infrastructure.repositories.TokenProvider
import com.example.template.quiz.api.dtos.TopicDtoApi
import com.example.template.quiz.api.dtos.mappers.toDomain
import com.example.template.quiz.api.dtos.mappers.toDtoApi
import com.example.template.quiz.domain.entities.Topic
import com.example.template.quiz.infrastructure.dataSources.TopicDataSourceCommon
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path

class TopicRemoteDataSourceImpl(
    private val client: HttpClient,
    private val host: String = SERVER_HOST,
    private val port: Int = SERVER_PORT,
    private val protocol: String = SERVER_PROTOCOL,
) : TopicDataSourceCommon {

    override suspend fun save(topic: Topic): Topic {
        val topicDtoApi: TopicDtoApi = topic.toDtoApi()

        val result = this.client.post {
            url {
                protocol = if (this@TopicRemoteDataSourceImpl.protocol == "http") {
                    URLProtocol.HTTP
                } else {
                    URLProtocol.HTTPS
                }
                host = this@TopicRemoteDataSourceImpl.host
                port = this@TopicRemoteDataSourceImpl.port

                path("/topic")
            }
            contentType(type = ContentType.Application.Json)
            setBody(
                topicDtoApi
            )
        }
        val topicDtoApiResponse: TopicDtoApi = result.body()
        return topicDtoApiResponse.toDomain()
    }

    override suspend fun getAll(): List<Topic> {
        val httpResponse: HttpResponse = this.client.get {
            url {
                protocol = if (this@TopicRemoteDataSourceImpl.protocol == "http") {
                    URLProtocol.HTTP
                } else {
                    URLProtocol.HTTPS
                }
                host = this@TopicRemoteDataSourceImpl.host
                port = this@TopicRemoteDataSourceImpl.port
                path("/topic")
            }
            contentType(type = ContentType.Application.Json)
        }
        if (httpResponse.status == HttpStatusCode.Unauthorized) {
            throw AuthenticationRequiredException()
        }
        val topics: List<TopicDtoApi> = httpResponse.body<List<TopicDtoApi>>()
        return topics.map {
            it.toDomain()
        }
    }
}