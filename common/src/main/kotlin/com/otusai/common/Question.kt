package com.otusai.common

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: Int,
    val text: String,
    val type: QuestionType
)

@Serializable
enum class QuestionType {
    TEXT,
    SINGLE_CHOICE,
    MULTI_CHOICE
}
