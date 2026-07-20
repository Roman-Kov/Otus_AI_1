package com.otusai.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.otusai.android.ui.QuestionsScreen
import com.otusai.android.ui.ThankYouScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SurveyApp()
            }
        }
    }
}

@Composable
fun SurveyApp() {
    val navController = rememberNavController()
    val viewModel: SurveyViewModel = viewModel()

    NavHost(navController, startDestination = "questions") {
        composable("questions") {
            val state by viewModel.state.collectAsState()
            LaunchedEffect(state) {
                if (state is SurveyUiState.Submitted) {
                    navController.navigate("thankyou") {
                        popUpTo("questions") { inclusive = true }
                    }
                }
            }
            QuestionsScreen(viewModel)
        }
        composable("thankyou") {
            ThankYouScreen(onRestart = {
                viewModel.loadQuestions()
                navController.navigate("questions") {
                    popUpTo("thankyou") { inclusive = true }
                }
            })
        }
    }
}
