package com.quizzer.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quizzer.app.data.Answer
import com.quizzer.app.data.Question
import com.quizzer.app.data.Topic
import com.quizzer.app.ui.theme.QuizzerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicListScreen(
    topics: List<Topic>,
    isLoading: Boolean,
    snackbarHostState: SnackbarHostState,
    onStartQuiz: (Topic) -> Unit,
    onDeleteTopic: (Topic) -> Unit,
    onImportTopic: () -> Unit,
) {
    var topicToDelete by remember { mutableStateOf<Topic?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Quizzer") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onImportTopic) { Text("Import topic") }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        when {
            isLoading -> Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) {
                CircularProgressIndicator()
            }

            topics.isEmpty() -> Box(Modifier.fillMaxSize().padding(innerPadding).padding(32.dp), Alignment.Center) {
                Text(
                    "No topics yet.\nImport a topic file (.json) to get started.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                // Extra bottom padding so the last card isn't hidden behind the FAB.
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(topics, key = { it.id }) { topic ->
                    TopicCard(
                        topic = topic,
                        onClick = { onStartQuiz(topic) },
                        onDelete = { topicToDelete = topic },
                    )
                }
            }
        }
    }

    topicToDelete?.let { topic ->
        AlertDialog(
            onDismissRequest = { topicToDelete = null },
            title = { Text("Delete topic?") },
            text = { Text("\"${topic.title}\" will be removed from the app. You can import it again later.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteTopic(topic)
                    topicToDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { topicToDelete = null }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun TopicCard(topic: Topic, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(topic.title, style = MaterialTheme.typography.titleMedium)
                if (topic.description.isNotEmpty()) {
                    Text(
                        topic.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    "${topic.questions.size} questions",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            TextButton(onClick = onDelete) { Text("Delete") }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TopicListPreview() {
    val q = Question("Q?", listOf(Answer("A", true), Answer("B", false)))
    QuizzerTheme {
        TopicListScreen(
            topics = listOf(
                Topic("a.json", "ASPICE (Demo)", "Automotive SPICE basics", listOf(q, q, q)),
                Topic("b.json", "General Knowledge (Demo)", "", listOf(q)),
            ),
            isLoading = false,
            snackbarHostState = remember { SnackbarHostState() },
            onStartQuiz = {}, onDeleteTopic = {}, onImportTopic = {},
        )
    }
}
