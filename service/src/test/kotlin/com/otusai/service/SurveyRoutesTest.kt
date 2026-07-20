package com.otusai.service

import com.otusai.common.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json

class SurveyRoutesTest {

    @BeforeTest
    fun setup() {
        SurveyStorage.reset()
    }

    @Test
    fun `GET questions returns list`() = testApplication {
        application { module() }
        val response = client.get("/questions")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        val questions = Json.decodeFromString<List<Question>>(body)
        assertEquals(5, questions.size)
        assertEquals(1, questions[0].id)
        assertEquals("Как вас зовут?", questions[0].text)
        assertEquals(QuestionType.TEXT, questions[0].type)
    }

    @Test
    fun `POST answers returns ok`() = testApplication {
        application { module() }
        val response = client.post("/answers") {
            contentType(ContentType.Application.Json)
            setBody("""{"answers":{"1":["Test"]}}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val saved = SurveyStorage.all()
        assertEquals(1, saved.size)
        assertEquals(mapOf(1 to listOf("Test")), saved[0].answers)
    }
}
