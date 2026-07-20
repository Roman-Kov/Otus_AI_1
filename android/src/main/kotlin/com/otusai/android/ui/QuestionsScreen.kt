package com.otusai.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.otusai.android.SurveyViewModel
import com.otusai.android.SurveyUiState
import com.otusai.common.Question
import com.otusai.common.QuestionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsScreen(viewModel: SurveyViewModel) {
    val state by viewModel.state.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Мини-анкета") })
    }) { padding ->
        when (val s = state) {
            is SurveyUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SurveyUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ошибка: ${s.message}")
                        Button(onClick = { viewModel.loadQuestions() }) {
                            Text("Повторить")
                        }
                    }
                }
            }
            is SurveyUiState.Questions -> {
                QuestionsForm(s.list, viewModel, padding)
            }
            is SurveyUiState.Submitted -> { /* handled by navigation */ }
        }
    }
}

@Composable
private fun QuestionsForm(
    questions: List<Question>,
    viewModel: SurveyViewModel,
    padding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        questions.forEach { q ->
            QuestionCard(q, viewModel)
        }
        Button(
            onClick = { viewModel.submit() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Отправить")
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun QuestionCard(question: Question, viewModel: SurveyViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(question.text, style = MaterialTheme.typography.titleSmall)

            when (question.type) {
                QuestionType.TEXT -> {
                    var text by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            text = it
                            viewModel.updateAnswer(question.id, it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                QuestionType.SINGLE_CHOICE -> {
                    val options = listOf("Мужской", "Женский", "Не указано")
                    var selectedOption by remember { mutableStateOf("") }
                    options.forEach { option ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = (option == selectedOption),
                                onClick = {
                                    selectedOption = option
                                    viewModel.updateAnswer(question.id, option)
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(option)
                        }
                    }
                }
                QuestionType.MULTI_CHOICE -> {
                    val options = listOf("Kotlin", "Java", "Python", "JavaScript", "C++")
                    var selectedOptions by remember { mutableStateOf(setOf<String>()) }
                    options.forEach { option ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = option in selectedOptions,
                                onCheckedChange = { checked ->
                                    selectedOptions = if (checked) selectedOptions + option else selectedOptions - option
                                    viewModel.toggleMultiAnswer(question.id, option)
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(option)
                        }
                    }
                }
            }
        }
    }
}
