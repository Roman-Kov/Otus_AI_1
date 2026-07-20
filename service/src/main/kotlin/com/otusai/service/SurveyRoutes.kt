package com.otusai.service

import com.otusai.common.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/questions") {
            val questions = listOf(
                Question(1, "Как вас зовут?", QuestionType.TEXT),
                Question(2, "Ваш пол?", QuestionType.SINGLE_CHOICE),
                Question(3, "Какие языки программирования вы знаете?", QuestionType.MULTI_CHOICE),
                Question(4, "Сколько лет опыта?", QuestionType.TEXT),
                Question(5, "Что хотите изучить?", QuestionType.TEXT)
            )
            call.respond(questions)
        }

        post("/answers") {
            val request = call.receive<AnswersRequest>()
            if (request.answers.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "answers cannot be empty"))
                return@post
            }
            SurveyStorage.save(request)
            call.respond(mapOf("status" to "ok"))
        }
    }
}
