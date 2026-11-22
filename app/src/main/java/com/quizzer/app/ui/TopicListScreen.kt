package com.quizzer.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quizzer.app.R
import com.quizzer.app.data.Topic
import com.quizzer.app.data.TopicStats
import com.quizzer.app.theme.DecorId
import com.quizzer.app.theme.LocalContentColors
import com.quizzer.app.theme.LocalQuizTheme
import com.quizzer.app.theme.TextId
import com.quizzer.app.theme.ThemedSpinner
import com.quizzer.app.theme.ThemedText
import com.quizzer.app.theme.decor
import com.quizzer.app.theme.resolve

@Composable
fun TopicListScreen(
    topics: List<Topic>,
    topicStats: Map<String, TopicStats>,
    isLoading: Boolean,
    onStartQuiz: (Topic) -> Unit,
    onDeleteTopic: (Topic) -> Unit,
    onImportTopic: () -> Unit,
    onOpenThemes: () -> Unit,
) {
    var topicToDelete by remember { mutableStateOf<Topic?>(null) }
    val theme = LocalQuizTheme.current
    val colors = LocalContentColors.current

    ThemedScreen {
        Column(Modifier.fillMaxSize()) {
            // Header: decor behind status bar, logo bottom-left, theme button top-right.
            val statusBar = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(212.dp + statusBar)
                    .decor(theme.decor(DecorId.ListHeader), colors),
            ) {
                ThemedText(
                    "Quizzer", TextId.Logo,
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp, bottom = 16.dp),
                    maxLines = 1,
                )
                IconAction(
                    R.drawable.ic_palette, 24.dp, onOpenThemes,
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = statusBar + 4.dp, end = 12.dp),
                    boxSize = 44.dp,
                )
            }

            SectionRow("Topics")

            when {
                isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) { ThemedSpinner() }
                else -> LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, top = 6.dp,
                        bottom = 24.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (topics.isEmpty()) {
                        item {
                            ThemedText(
                                "No topics yet.\nImport a topic file (.json) to get started.",
                                TextId.Body,
                                Modifier.padding(start = 6.dp, top = 12.dp, end = 6.dp, bottom = 6.dp),
                            )
                        }
                    }
                    itemsIndexed(topics, key = { _, t -> t.id }) { index, topic ->
                        TopicRow(
                            index = index + 1,
                            topic = topic,
                            stats = topicStats[topic.id],
                            onClick = { onStartQuiz(topic) },
                            onDelete = { topicToDelete = topic },
                        )
                    }
                    item { ImportRow("Import topic", onImportTopic) }
                }
            }
        }

        ThemedConfirmDialog(
            visible = topicToDelete != null,
            title = "Delete topic?",
            body = "\"${topicToDelete?.title.orEmpty()}\" will be removed from the app, together with its statistics. You can import it again later.",
            confirmLabel = "Delete",
            onConfirm = {
                topicToDelete?.let(onDeleteTopic)
                topicToDelete = null
            },
            onDismiss = { topicToDelete = null },
        )
    }
}

/** Section label followed by a line fading out to the right. */
@Composable
fun SectionRow(label: String) {
    val theme = LocalQuizTheme.current
    val lineColor = theme.text(TextId.SectionLabel).color.resolve(LocalContentColors.current)
    Row(
        Modifier.padding(start = 22.dp, top = 16.dp, end = 22.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ThemedText(label, TextId.SectionLabel)
        Box(
            Modifier
                .weight(1f)
                .height(2.dp)
                .background(Brush.horizontalGradient(listOf(lineColor, Color.Transparent))),
        )
    }
}

@Composable
private fun TopicRow(index: Int, topic: Topic, stats: TopicStats?, onClick: () -> Unit, onDelete: () -> Unit) {
    PressableRow(onClick, Modifier) {
        Row(
            Modifier
                .weight(1f)
                .padding(start = 18.dp, top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ThemedText(
                index.toString().padStart(2, '0'), TextId.TopicIndex,
                // 20 dp per the handoff; wider display fonts (e.g. Shrikhand) may need more.
                Modifier
                    .widthIn(min = 20.dp)
                    .padding(top = 3.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                ThemedText(topic.title, TextId.TopicTitle, maxLines = 2)
                if (topic.description.isNotEmpty()) {
                    ThemedText(topic.description, TextId.TopicDescription, maxLines = 1)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemedText("${topic.questions.size} questions", TextId.TopicMeta, maxLines = 1)
                    val content = LocalContentColors.current.content
                    Box(
                        Modifier
                            .size(3.dp)
                            .background(content.copy(alpha = content.alpha * 0.6f), CircleShape),
                    )
                    ThemedText(statsText(stats), TextId.TopicStats, maxLines = 1)
                }
            }
        }
        Spacer(Modifier.width(4.dp))
        IconAction(R.drawable.ic_trash, 20.dp, onDelete, Modifier.padding(end = 12.dp), alpha = 0.75f)
    }
}

private fun statsText(stats: TopicStats?): String {
    val won = stats?.gamesWon ?: 0
    val played = stats?.gamesPlayed ?: 0
    return "$won ${if (won == 1) "win" else "wins"} / $played ${if (played == 1) "game" else "games"}"
}
