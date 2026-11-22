package com.quizzer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quizzer.app.quiz.QuizViewModel
import com.quizzer.app.ui.QuizScreen
import com.quizzer.app.ui.ResultScreen
import com.quizzer.app.ui.TopicListScreen
import com.quizzer.app.ui.theme.QuizzerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizzerTheme {
                QuizzerApp()
            }
        }
    }
}

@Composable
fun QuizzerApp(viewModel: QuizViewModel = viewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Any file type is allowed: many file managers don't label .json files correctly.
    // The content is validated on import anyway.
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) viewModel.importTopic(uri)
    }

    LaunchedEffect(viewModel.message) {
        viewModel.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.messageShown()
        }
    }

    val session = viewModel.session
    when {
        session == null -> TopicListScreen(
            topics = viewModel.topics,
            topicStats = viewModel.stats.topics,
            isLoading = viewModel.isLoading,
            snackbarHostState = snackbarHostState,
            onStartQuiz = viewModel::startQuiz,
            onDeleteTopic = viewModel::deleteTopic,
            onImportTopic = { importLauncher.launch(arrayOf("*/*")) },
        )

        session.isFinished -> {
            BackHandler(onBack = viewModel::exitQuiz)
            ResultScreen(session, onRetry = viewModel::restartQuiz, onBackToTopics = viewModel::exitQuiz)
        }

        else -> {
            BackHandler(onBack = viewModel::exitQuiz)
            QuizScreen(
                session = session,
                onToggleAnswer = viewModel::toggleAnswer,
                onCheck = viewModel::checkAnswer,
                onNext = viewModel::nextQuestion,
                onQuit = viewModel::exitQuiz,
            )
        }
    }
}
