package com.quizzer.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.quizzer.app.theme.ColorId
import com.quizzer.app.theme.DecorId
import com.quizzer.app.theme.LocalContentColors
import com.quizzer.app.theme.LocalQuizTheme
import com.quizzer.app.theme.ShapeId
import com.quizzer.app.theme.SurfaceId
import com.quizzer.app.theme.TextId
import com.quizzer.app.theme.ThemedBox
import com.quizzer.app.theme.ThemedIcon
import com.quizzer.app.theme.ThemedText
import com.quizzer.app.theme.decor
import com.quizzer.app.theme.themeBackground
import kotlinx.coroutines.delay

/** Full-screen theme background, the theme's "screen" decor overlay, and [content] on top. */
@Composable
fun ThemedScreen(
    modifier: Modifier = Modifier,
    backgroundDecor: DecorId? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val theme = LocalQuizTheme.current
    val colors = LocalContentColors.current
    Box(
        modifier
            .fillMaxSize()
            .themeBackground(theme, colors)
            .decor(backgroundDecor?.let { theme.decor(it) }, colors)
            .decor(theme.decor(DecorId.Screen), colors),
        content = content,
    )
}

/** Clickable without ripple; returns whether it's pressed. */
@Composable
fun rememberPressInteraction(): Pair<MutableInteractionSource, Boolean> {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    return source to pressed
}

/** Primary (gradient) or secondary button; height 56 / 52, label on one line, shrinks if needed. */
@Composable
fun ThemedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    primary: Boolean = true,
) {
    val (source, pressed) = rememberPressInteraction()
    val scale by animateFloatAsState(
        if (pressed) 0.97f else 1f,
        tween(if (pressed) 100 else 150, easing = FastOutSlowInEasing), label = "buttonScale",
    )
    val surface = when {
        !primary -> SurfaceId.ButtonSecondary
        !enabled -> SurfaceId.ButtonDisabled
        pressed -> SurfaceId.ButtonPrimaryPressed
        else -> SurfaceId.ButtonPrimary
    }
    val textStyle = if (primary) TextId.Button else TextId.ButtonSecondary
    ThemedBox(
        surface, ShapeId.Button,
        modifier
            .fillMaxWidth()
            .height(if (primary) 56.dp else 52.dp)
            .scale(scale)
            .clickable(source, indication = null, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        ThemedText(
            text, textStyle,
            modifier = Modifier.padding(horizontal = 20.dp),
            maxLines = 1, align = TextAlign.Center, autoSizeMin = 16f,
        )
    }
}

/** Top bar of quiz and picker: back/quit button left, optional small title right. */
@Composable
fun ThemedTopBar(label: String, icon: Int, iconSize: Dp, onClick: () -> Unit, title: String? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, top = 8.dp, end = 20.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val theme = LocalQuizTheme.current
        val color = theme.color(ColorId.TextSecondary)
        Row(
            Modifier
                .clickable(onClick = onClick)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            ThemedIcon(icon, iconSize, tint = color)
            ThemedText(label, TextId.Back)
        }
        Spacer(Modifier.weight(1f))
        if (title != null) {
            ThemedText(title, TextId.HeaderSmall, Modifier.weight(3f, fill = false), maxLines = 1, align = TextAlign.End)
        }
    }
}

/** A row in the topic or theme list: switches to the pressed surface while pressed. */
@Composable
fun PressableRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val (source, pressed) = rememberPressInteraction()
    ThemedBox(
        if (pressed) SurfaceId.TopicRowPressed else SurfaceId.TopicRow,
        ShapeId.TopicRow,
        modifier
            .fillMaxWidth()
            .clickable(source, indication = null, onClick = onClick),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, content = content)
    }
}

/** "+ Import …" row at the end of a list. */
@Composable
fun ImportRow(label: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ThemedText("+", TextId.ImportAction, Modifier.width(20.dp))
        ThemedText(label, TextId.ImportAction)
    }
}

/** Icon-only button (delete, theme picker). */
@Composable
fun IconAction(icon: Int, size: Dp, onClick: () -> Unit, modifier: Modifier = Modifier, alpha: Float = 1f, boxSize: Dp = 40.dp) {
    val color = LocalContentColors.current.content
    Box(
        modifier
            .size(boxSize)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        ThemedIcon(icon, size, tint = color.copy(alpha = color.alpha * alpha))
    }
}

/** Confirmation dialog: scrim + card, "Cancel" and a primary "Delete" action. */
@Composable
fun ThemedConfirmDialog(
    visible: Boolean,
    title: String,
    body: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val theme = LocalQuizTheme.current
    if (visible) BackHandler(onBack = onDismiss)
    AnimatedVisibility(visible, enter = fadeIn(tween(200)), exit = fadeOut(tween(200))) {
        Box(
            Modifier
                .fillMaxSize()
                .background(theme.color(ColorId.Scrim))
                .clickable(remember { MutableInteractionSource() }, indication = null, onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            ThemedBox(
                SurfaceId.Dialog, ShapeId.Dialog,
                Modifier
                    .animateEnterExit(enter = scaleIn(tween(200, easing = FastOutSlowInEasing), initialScale = 0.94f), exit = scaleOut(tween(200), targetScale = 0.94f))
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clickable(remember { MutableInteractionSource() }, indication = null) {},
            ) {
                Column(
                    Modifier.padding(start = 20.dp, top = 22.dp, end = 20.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    ThemedText(title, TextId.DialogTitle)
                    ThemedText(body, TextId.DialogBody)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ThemedText(
                            "Cancel", TextId.DialogAction,
                            Modifier
                                .clickable(onClick = onDismiss)
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                        )
                        ThemedBox(
                            SurfaceId.ButtonPrimary, ShapeId.Chip,
                            Modifier.clickable(onClick = onConfirm),
                        ) {
                            // Dialog action style, in the button's content color.
                            ThemedText(
                                confirmLabel, TextId.DialogAction,
                                Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                color = LocalContentColors.current.content,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Snackbar at the bottom; hides itself after 4 s. */
@Composable
fun ThemedSnackbar(message: String?, onShown: () -> Unit, modifier: Modifier = Modifier) {
    val state = remember { MutableTransitionState(false) }
    val slide = with(LocalDensity.current) { 24.dp.roundToPx() }
    val lastMessage = remember { androidx.compose.runtime.mutableStateOf("") }
    LaunchedEffect(message) {
        if (message != null) {
            lastMessage.value = message
            state.targetState = true
            delay(4000)
            state.targetState = false
            onShown()
        }
    }
    AnimatedVisibility(
        state,
        modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(start = 12.dp, end = 12.dp, bottom = 16.dp),
        enter = slideInVertically(tween(250, easing = FastOutSlowInEasing)) { slide } + fadeIn(tween(250)),
        exit = slideOutVertically(tween(250, easing = FastOutSlowInEasing)) { slide } + fadeOut(tween(250)),
    ) {
        ThemedBox(SurfaceId.Snackbar, ShapeId.Card, Modifier.fillMaxWidth()) {
            ThemedText(lastMessage.value, TextId.Snackbar, Modifier.padding(horizontal = 22.dp, vertical = 14.dp))
        }
    }
}

/** Padding for content below the status bar. */
fun Modifier.statusBarPadding(): Modifier = statusBarsPadding()

/** Padding for content above the gesture / navigation bar. */
fun Modifier.navigationBarPadding(): Modifier = navigationBarsPadding()
