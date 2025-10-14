package com.example.template.quiz.infrastructure.dataSources.remote

import com.example.template.app.utils.SERVER_HOST
import com.example.template.app.utils.SERVER_PORT
import com.example.template.app.utils.SERVER_PROTOCOL
import com.example.template.quiz.api.dtos.QuizDtoApi
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.URLProtocol
import io.ktor.http.path
import com.example.template.quiz.api.dtos.TopicDtoApi
import com.example.template.quiz.api.dtos.mappers.toDomain
import com.example.template.quiz.domain.entities.Quiz
import com.example.template.quiz.infrastructure.dataSources.QuizDataSourceCommon
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class QuizRemoteDataSourceImpl(
    private val client: HttpClient,
    private val host: String = SERVER_HOST,
    private val port: Int = SERVER_PORT,
    private val protocol: String = SERVER_PROTOCOL
) : QuizDataSourceCommon {

    override suspend fun generateQuizzes(topicDtoApi: TopicDtoApi): List<Quiz> {
        val quizzesDtoApi: List<QuizDtoApi> = this.client.post {
            url {
                protocol = if (this@QuizRemoteDataSourceImpl.protocol == "http") {
                    URLProtocol.HTTP
                } else {
                    URLProtocol.HTTPS
                }
                host = this@QuizRemoteDataSourceImpl.host
                port = this@QuizRemoteDataSourceImpl.port
                path("/quiz/ai-create/topic")
            }
            contentType(type = ContentType.Application.Json)
            setBody(
                topicDtoApi
            )
        }.body()

        return quizzesDtoApi.map { it.toDomain() }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun get(id: Uuid): Quiz? {
        val quiz: QuizDtoApi = this.client.post {
            url {
                protocol = if (this@QuizRemoteDataSourceImpl.protocol == "http") {
                    URLProtocol.HTTP
                } else {
                    URLProtocol.HTTPS
                }
                host = this@QuizRemoteDataSourceImpl.host
                port = this@QuizRemoteDataSourceImpl.port
                path("/quiz/${id.toString()}")
            }
            contentType(type = ContentType.Application.Json)
        }.body()

        return quiz.toDomain()
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getByTopic(idTopic: Uuid): List<Quiz> {
        val quiz: List<QuizDtoApi> = this.client.get {
            url {
                protocol = if (this@QuizRemoteDataSourceImpl.protocol == "http") {
                    URLProtocol.HTTP
                } else {
                    URLProtocol.HTTPS
                }
                host = this@QuizRemoteDataSourceImpl.host
                port = this@QuizRemoteDataSourceImpl.port
                path("/quiz/topic/${idTopic.toString()}")
            }
            contentType(type = ContentType.Application.Json)
        }.body()

        return quiz.map { it.toDomain() }
    }
}