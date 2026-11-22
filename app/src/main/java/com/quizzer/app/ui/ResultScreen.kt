package com.quizzer.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quizzer.app.quiz.QuizSession
import com.quizzer.app.ui.theme.QuizColors

// TODO: replace the breakdown rows with the pie chart once the design is chosen (docs/PLAN.md).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(session: QuizSession, onRetry: () -> Unit, onBackToTopics: () -> Unit) {
    val total = session.questions.size

    Scaffold(topBar = { TopAppBar(title = { Text(session.topic.title) }) }) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        ) {
            Text(
                if (session.isWon) "You win!" else "Not this time",
                style = MaterialTheme.typography.headlineLarge,
            )
            Text("${session.correctCount} / $total", style = MaterialTheme.typography.displayMedium)
            Text(
                "${session.requiredToWin} correct needed to win (${QuizSession.WIN_PERCENT} %)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (session.isShortGame) {
                Text(
                    "Short game: this topic has only $total questions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(8.dp))
            BreakdownRow("Correct", session.correctCount, QuizColors.correctContainer)
            BreakdownRow("Partially correct", session.partialCount, QuizColors.partialContainer)
            BreakdownRow("Wrong", session.wrongCount, MaterialTheme.colorScheme.errorContainer)

            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) { Text("Play again") }
            OutlinedButton(onClick = onBackToTopics, modifier = Modifier.fillMaxWidth()) { Text("Back to topics") }
        }
    }
}

@Composable
private fun BreakdownRow(label: String, count: Int, color: Color) {
    Surface(color = color, shape = MaterialTheme.shapes.small, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Text("$count", style = MaterialTheme.typography.titleMedium)
        }
    }
}
