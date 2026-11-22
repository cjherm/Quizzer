package com.quizzer.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.quizzer.app.R
import com.quizzer.app.data.Answer
import com.quizzer.app.data.QuestionResult
import com.quizzer.app.quiz.QuizSession
import com.quizzer.app.theme.LocalContentColors
import com.quizzer.app.theme.LocalQuizTheme
import com.quizzer.app.theme.ShapeId
import com.quizzer.app.theme.SurfaceId
import com.quizzer.app.theme.TextId
import com.quizzer.app.theme.ThemePaint
import com.quizzer.app.theme.ThemedBox
import com.quizzer.app.theme.ThemedIcon
import com.quizzer.app.theme.ThemedText
import com.quizzer.app.theme.resolve

@Composable
fun QuizScreen(
    session: QuizSession,
    onToggleAnswer: (Int) -> Unit,
    onCheck: () -> Unit,
    onNext: () -> Unit,
    onQuit: () -> Unit,
) {
    ThemedScreen {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarPadding()
                .navigationBarPadding(),
        ) {
            ThemedTopBar("Quit", R.drawable.ic_x, 14.dp, onQuit, title = session.topic.title)
            CounterRow(session)

            Box(Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = session,
                    contentKey = { it.currentIndex },
                    transitionSpec = {
                        (fadeIn(tween(200, delayMillis = 120)) + slideInVertically(tween(200, delayMillis = 120, easing = FastOutSlowInEasing)) { it / 40 })
                            .togetherWith(fadeOut(tween(120)))
                    },
                    label = "question",
                ) { s ->
                    QuestionContent(s, onToggleAnswer)
                }
            }

            Box(Modifier.padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 14.dp)) {
                ThemedButton(
                    text = actionLabel(session),
                    onClick = if (session.isChecked) onNext else onCheck,
                    enabled = session.isChecked || session.selected.isNotEmpty(),
                )
            }
        }
    }
}

@Composable
private fun CounterRow(session: QuizSession) {
    val total = session.questions.size
    val progress by animateFloatAsState(
        (session.currentIndex + if (session.isChecked) 1 else 0) / total.toFloat(),
        tween(350, easing = FastOutSlowInEasing), label = "progress",
    )
    Row(
        Modifier.padding(horizontal = 20.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ThemedText((session.currentIndex + 1).toString().padStart(2, '0'), TextId.Counter, maxLines = 1)
        ThemedText("/ ${total.toString().padStart(2, '0')}", TextId.CounterTotal, Modifier.padding(bottom = 2.dp), maxLines = 1)
        ThemedBox(
            SurfaceId.ProgressTrack, ShapeId.Progress,
            Modifier
                .weight(1f)
                .padding(bottom = 9.dp)
                .height(8.dp),
        ) {
            if (progress > 0f) {
                ThemedBox(
                    SurfaceId.ProgressFill, ShapeId.Progress,
                    Modifier
                        .fillMaxWidth(progress)
                        .height(8.dp),
                ) {}
            }
        }
    }
}

@Composable
private fun QuestionContent(session: QuizSession, onToggleAnswer: (Int) -> Unit) {
    val scroll = rememberScrollState()
    Box(Modifier.fillMaxSize()) {
        QuestionColumn(session, onToggleAnswer, Modifier.verticalScroll(scroll))
        if (scroll.canScrollForward) BottomFade(Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun QuestionColumn(session: QuizSession, onToggleAnswer: (Int) -> Unit, modifier: Modifier) {
    val question = session.current
    Column(
        modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 18.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ThemedBox(SurfaceId.QuestionCard, ShapeId.Card, Modifier.fillMaxWidth()) {
            ThemedText(question.text, TextId.Question, Modifier.padding(horizontal = 16.dp, vertical = 14.dp))
        }
        ThemedText("Select all correct answers.", TextId.Hint, Modifier.padding(start = 6.dp, top = 2.dp, end = 6.dp, bottom = 4.dp))

        question.answers.forEachIndexed { index, answer ->
            AnswerRow(
                letter = "ABCD"[index].toString(),
                answer = answer,
                isSelected = index in session.selected,
                isChecked = session.isChecked,
                onClick = { onToggleAnswer(index) },
            )
        }

        val result = session.currentResult
        val slide = with(LocalDensity.current) { 16.dp.roundToPx() }
        AnimatedVisibility(
            visible = result != null,
            enter = slideInHorizontally(tween(220, easing = LinearOutSlowInEasing)) { -slide } + fadeIn(tween(220)),
            exit = fadeOut(tween(0)),
        ) {
            if (result != null) Feedback(result, question.explanation)
        }
    }
}

private enum class AnswerState(val surface: SurfaceId) {
    Idle(SurfaceId.AnswerIdle), Selected(SurfaceId.AnswerSelected),
    Correct(SurfaceId.AnswerCorrect), Missed(SurfaceId.AnswerMissed),
    Wrong(SurfaceId.AnswerWrong), Dimmed(SurfaceId.AnswerDimmed),
}

@Composable
private fun AnswerRow(letter: String, answer: Answer, isSelected: Boolean, isChecked: Boolean, onClick: () -> Unit) {
    val state = when {
        !isChecked -> if (isSelected) AnswerState.Selected else AnswerState.Idle
        answer.correct -> if (isSelected) AnswerState.Correct else AnswerState.Missed
        isSelected -> AnswerState.Wrong
        else -> AnswerState.Dimmed
    }
    ThemedBox(
        state.surface, ShapeId.Answer,
        Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clickable(enabled = !isChecked, onClick = onClick),
    ) {
        Row(
            Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ThemedText(letter, TextId.AnswerLetter, Modifier.width(16.dp), maxLines = 1)
            ThemedText(answer.text, TextId.Answer, Modifier.weight(1f))
            Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                when (state) {
                    AnswerState.Idle -> Checkbox(checked = false)
                    AnswerState.Selected -> Checkbox(checked = true)
                    AnswerState.Correct, AnswerState.Missed -> ThemedIcon(R.drawable.ic_check, 20.dp)
                    AnswerState.Wrong -> ThemedIcon(R.drawable.ic_x, 20.dp)
                    AnswerState.Dimmed -> Unit
                }
            }
        }
    }
}

@Composable
private fun Checkbox(checked: Boolean) {
    ThemedBox(
        if (checked) SurfaceId.CheckboxChecked else SurfaceId.Checkbox,
        ShapeId.Checkbox,
        Modifier.size(18.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) ThemedIcon(R.drawable.ic_check, 12.dp)
    }
}

@Composable
private fun Feedback(result: QuestionResult, explanation: String?) {
    val (surface, text) = when (result) {
        QuestionResult.CORRECT -> SurfaceId.FeedbackCorrect to "Correct!"
        QuestionResult.PARTIAL -> SurfaceId.FeedbackPartial to "Partly right. The correct answers are marked with ✓."
        QuestionResult.WRONG -> SurfaceId.FeedbackWrong to "Not quite. The correct answers are marked with ✓."
    }
    Column(Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ThemedBox(surface, ShapeId.Feedback, Modifier.widthIn(max = 300.dp)) {
            ThemedText(text, TextId.Feedback, Modifier.padding(start = 18.dp, top = 8.dp, end = 20.dp, bottom = 8.dp))
        }
        if (explanation != null) {
            ThemedText(explanation, TextId.Explanation, Modifier.padding(horizontal = 6.dp))
        }
    }
}

/** Fades the scroll content out towards the docked button while there's more to scroll. */
@Composable
private fun BottomFade(modifier: Modifier) {
    val theme = LocalQuizTheme.current
    val last = when (val bg = theme.background) {
        is ThemePaint.Solid -> bg.color.resolve(LocalContentColors.current)
        is ThemePaint.Linear -> bg.stops.last().second.resolve(LocalContentColors.current)
    }
    Box(
        modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(Brush.verticalGradient(listOf(Color.Transparent, last))),
    )
}

/** Random label per question and state; kept across recompositions and rotation. */
@Composable
private fun actionLabel(session: QuizSession): String {
    val submitLabels = stringArrayResource(R.array.submit_labels)
    val nextLabels = stringArrayResource(R.array.next_labels)
    val showScore = stringResource(R.string.show_score_label)
    return rememberSaveable(session.topic.id, session.currentIndex, session.isChecked) {
        when {
            !session.isChecked -> submitLabels.random()
            session.isLastQuestion -> showScore
            else -> nextLabels.random()
        }
    }
}
