package com.otusai.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otusai.android.network.SurveyApi
import com.otusai.common.AnswersRequest
import com.otusai.common.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SurveyUiState {
    data object Loading : SurveyUiState
    data class Questions(val list: List<Question>) : SurveyUiState
    data object Submitted : SurveyUiState
    data class Error(val message: String) : SurveyUiState
}

class SurveyViewModel : ViewModel() {

    private val api = SurveyApi()

    private val _state = MutableStateFlow<SurveyUiState>(SurveyUiState.Loading)
    val state: StateFlow<SurveyUiState> = _state.asStateFlow()

    private val answers = mutableMapOf<Int, MutableSet<String>>()

    init {
        loadQuestions()
    }

    fun loadQuestions() {
        answers.clear()
        viewModelScope.launch {
            _state.value = SurveyUiState.Loading
            try {
                val questions = api.getQuestions()
                _state.value = SurveyUiState.Questions(questions)
            } catch (e: Exception) {
                _state.value = SurveyUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateAnswer(questionId: Int, value: String) {
        answers[questionId] = mutableSetOf(value)
    }

    fun toggleMultiAnswer(questionId: Int, value: String) {
        val current = answers.getOrPut(questionId) { mutableSetOf() }
        if (current.contains(value)) current.remove(value) else current.add(value)
    }

    fun submit() {
        viewModelScope.launch {
            try {
                val request = AnswersRequest(answers.mapValues { it.value.toList() })
                api.submitAnswers(request)
                _state.value = SurveyUiState.Submitted
            } catch (e: Exception) {
                _state.value = SurveyUiState.Error(e.message ?: "Submit failed")
            }
        }
    }
}
