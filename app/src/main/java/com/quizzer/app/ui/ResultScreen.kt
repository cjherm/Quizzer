package com.quizzer.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.quizzer.app.quiz.QuizSession
import com.quizzer.app.theme.ColorId
import com.quizzer.app.theme.DecorId
import com.quizzer.app.theme.LocalQuizTheme
import com.quizzer.app.theme.ShapeId
import com.quizzer.app.theme.SurfaceId
import com.quizzer.app.theme.TextId
import com.quizzer.app.theme.ThemedBox
import com.quizzer.app.theme.ThemedText
import com.quizzer.app.theme.drawPie
import com.quizzer.app.theme.toShape
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(session: QuizSession, onRetry: () -> Unit, onBackToTopics: () -> Unit) {
    val theme = LocalQuizTheme.current
    val total = session.questions.size
    val percent = session.correctCount * 100 / total

    // Count-up of the score and sweep of the pie chart.
    val count = remember(session) { Animatable(0f) }
    val sweep = remember(session) { Animatable(0f) }
    val showHeadline = remember(session) { MutableTransitionState(false) }
    LaunchedEffect(session) {
        launch { count.animateTo(session.correctCount.toFloat(), tween(800, easing = LinearOutSlowInEasing)) }
        launch { sweep.animateTo(1f, tween(900, easing = FastOutSlowInEasing)) }
        delay(150)
        showHeadline.targetState = true
    }

    ThemedScreen(backgroundDecor = if (session.isWon) DecorId.ResultWin else DecorId.ResultLose) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarPadding()
                .navigationBarPadding(),
        ) {
            ThemedText(session.topic.title, TextId.HeaderSmall, Modifier.padding(start = 22.dp, top = 14.dp, end = 22.dp), maxLines = 1)

            val slide = with(LocalDensity.current) { 8.dp.roundToPx() }
            AnimatedVisibility(
                showHeadline,
                enter = fadeIn(tween(250)) + slideInVertically(tween(250, easing = FastOutSlowInEasing)) { slide },
            ) {
                Column(
                    Modifier.padding(start = 22.dp, top = 18.dp, end = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    ThemedBox(if (session.isWon) SurfaceId.ChipWin else SurfaceId.ChipLose, ShapeId.Chip) {
                        ThemedText(
                            if (session.isWon) "Win" else "Lose", TextId.Chip,
                            Modifier.padding(start = 14.dp, top = 4.dp, end = 16.dp, bottom = 4.dp),
                        )
                    }
                    ThemedText(
                        when {
                            percent == 100 -> "Perfect!"
                            session.isWon -> "Well done!"
                            else -> "Keep practising!"
                        },
                        TextId.Headline,
                    )
                }
            }

            Row(
                Modifier.padding(start = 22.dp, top = 4.dp, end = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ThemedText(count.value.toInt().toString(), TextId.Score, Modifier.alignByBaseline(), maxLines = 1)
                ThemedText("/ $total", TextId.ScoreTotal, Modifier.alignByBaseline(), maxLines = 1)
            }
            ThemedText("$percent % correct", TextId.Percent, Modifier.padding(horizontal = 22.dp))
            if (!session.isWon) {
                ThemedText(
                    "${session.requiredToWin} correct needed to win",
                    TextId.Body,
                    Modifier.padding(start = 22.dp, top = 4.dp, end = 22.dp),
                )
            }

            Spacer(Modifier.weight(1f))

            // Chart panel: pie + legend.
            ThemedBox(
                SurfaceId.LegendPanel, ShapeId.Card,
                Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            ) {
                Row(
                    Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val slices = listOf(
                        session.correctCount to theme.color(ColorId.ChartCorrect),
                        session.partialCount to theme.color(ColorId.ChartPartial),
                        session.wrongCount to theme.color(ColorId.ChartWrong),
                    )
                    Canvas(Modifier.size(128.dp)) { drawPie(slices, theme.color(ColorId.ChartGap), sweep.value) }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        LegendRow("Correct", session.correctCount, slices[0].second)
                        LegendRow("Partially correct", session.partialCount, slices[1].second)
                        LegendRow("Wrong", session.wrongCount, slices[2].second)
                    }
                }
            }

            Column(
                Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ThemedButton("Try again", onRetry)
                ThemedButton("Back to topics", onBackToTopics, primary = false)
            }
        }
    }
}

@Composable
private fun LegendRow(label: String, value: Int, color: Color) {
    val swatchShape = LocalQuizTheme.current.shape(ShapeId.Checkbox).toShape()
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            Modifier
                .size(12.dp)
                .background(color, swatchShape),
        )
        ThemedText(label, TextId.LegendLabel, Modifier.weight(1f), maxLines = 1)
        ThemedText(value.toString(), TextId.LegendValue, maxLines = 1)
    }
}
