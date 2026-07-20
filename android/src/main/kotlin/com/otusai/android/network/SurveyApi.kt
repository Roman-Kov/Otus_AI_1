package com.otusai.android.network

import com.otusai.common.AnswersRequest
import com.otusai.common.Question
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class SurveyApi(private val baseUrl: String = "http://10.0.2.2:8080") {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getQuestions(): List<Question> {
        return client.get("$baseUrl/questions").body()
    }

    suspend fun submitAnswers(request: AnswersRequest) {
        client.post("$baseUrl/answers") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
