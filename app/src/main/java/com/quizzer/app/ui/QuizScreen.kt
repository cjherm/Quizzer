package com.quizzer.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quizzer.app.data.Answer
import com.quizzer.app.data.Question
import com.quizzer.app.data.Topic
import com.quizzer.app.quiz.QuizSession
import com.quizzer.app.ui.theme.QuizColors
import com.quizzer.app.ui.theme.QuizzerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    session: QuizSession,
    onToggleAnswer: (Int) -> Unit,
    onCheck: () -> Unit,
    onNext: () -> Unit,
    onQuit: () -> Unit,
) {
    val question = session.current
    val total = session.questions.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(session.topic.title) },
                actions = { TextButton(onClick = onQuit) { Text("Quit") } },
            )
        },
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Question ${session.currentIndex + 1} of $total", style = MaterialTheme.typography.labelLarge)
            LinearProgressIndicator(
                progress = { (session.currentIndex + if (session.isChecked) 1 else 0) / total.toFloat() },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(question.text, style = MaterialTheme.typography.headlineSmall)
            Text(
                "Select all correct answers.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                question.answers.forEachIndexed { index, answer ->
                    AnswerOption(
                        answer = answer,
                        isSelected = index in session.selected,
                        isChecked = session.isChecked,
                        onClick = { onToggleAnswer(index) },
                    )
                }
            }

            if (session.isChecked) {
                Feedback(isCorrect = session.isCurrentAnswerCorrect, explanation = question.explanation)
            }

            Button(
                onClick = if (session.isChecked) onNext else onCheck,
                enabled = session.isChecked || session.selected.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    when {
                        !session.isChecked -> "Check answer"
                        session.isLastQuestion -> "Show result"
                        else -> "Next question"
                    },
                )
            }
        }
    }
}

@Composable
private fun AnswerOption(answer: Answer, isSelected: Boolean, isChecked: Boolean, onClick: () -> Unit) {
    // After checking: correct answers turn green, wrongly selected ones red.
    val (container, marker) = when {
        !isChecked && isSelected -> MaterialTheme.colorScheme.secondaryContainer to null
        !isChecked -> MaterialTheme.colorScheme.surfaceVariant to null
        answer.correct -> QuizColors.correctContainer to "✓"
        isSelected -> MaterialTheme.colorScheme.errorContainer to "✗"
        else -> MaterialTheme.colorScheme.surfaceVariant to null
    }

    Surface(
        onClick = onClick,
        enabled = !isChecked,
        shape = MaterialTheme.shapes.medium,
        color = container,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isSelected, onCheckedChange = null, modifier = Modifier.padding(12.dp))
            Text(answer.text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            if (marker != null) {
                Text(
                    marker,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun Feedback(isCorrect: Boolean, explanation: String?) {
    val color: Color = if (isCorrect) QuizColors.correctContainer else MaterialTheme.colorScheme.errorContainer
    Surface(color = color, shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                if (isCorrect) "Correct!" else "Not quite. The correct answers are marked with ✓.",
                style = MaterialTheme.typography.titleMedium,
            )
            if (explanation != null) {
                Text(explanation, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizScreenPreview() {
    val topic = Topic(
        "demo.json", "Demo", "",
        listOf(
            Question(
                "Which of these are fruits?",
                listOf(Answer("Apple", true), Answer("Carrot", false), Answer("Banana", true)),
                "Apples and bananas are fruits; carrots are root vegetables.",
            ),
        ),
    )
    val session = QuizSession(topic, topic.questions).toggle(0).toggle(1).check()
    QuizzerTheme {
        QuizScreen(session, onToggleAnswer = {}, onCheck = {}, onNext = {}, onQuit = {})
    }
}
