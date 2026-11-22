package com.quizzer.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quizzer.app.quiz.QuizSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(session: QuizSession, onRetry: () -> Unit, onBackToTopics: () -> Unit) {
    val total = session.questions.size
    val percent = session.correctCount * 100 / total

    Scaffold(topBar = { TopAppBar(title = { Text(session.topic.title) }) }) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        ) {
            Text("Quiz complete!", style = MaterialTheme.typography.headlineMedium)
            Text("${session.correctCount} / $total", style = MaterialTheme.typography.displayMedium)
            Text("$percent % correct", style = MaterialTheme.typography.titleMedium)
            Text(
                when {
                    percent == 100 -> "Perfect!"
                    percent >= 70 -> "Well done!"
                    else -> "Keep practising!"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) { Text("Try again") }
            OutlinedButton(onClick = onBackToTopics, modifier = Modifier.fillMaxWidth()) { Text("Back to topics") }
        }
    }
}
