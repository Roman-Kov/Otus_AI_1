package com.otusai.common

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializationTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testQuestionSerialization() {
        val question = Question(1, "Test?", QuestionType.TEXT)
        val encoded = json.encodeToString(Question.serializer(), question)
        val decoded = json.decodeFromString(Question.serializer(), encoded)
        assertEquals(question, decoded)
    }

    @Test
    fun testAnswersRequestSerialization() {
        val request = AnswersRequest(mapOf(1 to listOf("A"), 2 to listOf("B", "C")))
        val encoded = json.encodeToString(AnswersRequest.serializer(), request)
        val decoded = json.decodeFromString(AnswersRequest.serializer(), encoded)
        assertEquals(request, decoded)
    }
}
