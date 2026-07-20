package com.otusai.service

import com.otusai.common.AnswersRequest

object SurveyStorage {
    private val answers = mutableListOf<AnswersRequest>()

    fun save(request: AnswersRequest) {
        answers.add(request)
    }

    fun all(): List<AnswersRequest> = answers.toList()

    internal fun reset() {
        answers.clear()
    }
}
