package com.otusai.common

import kotlinx.serialization.Serializable

@Serializable
data class AnswersRequest(
    val answers: Map<Int, List<String>>
)
