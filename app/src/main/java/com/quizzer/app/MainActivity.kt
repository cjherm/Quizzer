package com.quizzer.app

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quizzer.app.appicon.AppIcons
import com.quizzer.app.quiz.QuizViewModel
import com.quizzer.app.theme.ProvideQuizTheme
import com.quizzer.app.theme.QuizTheme
import com.quizzer.app.ui.LicensesScreen
import com.quizzer.app.ui.QuizScreen
import com.quizzer.app.ui.ResultScreen
import com.quizzer.app.ui.ThemePickerScreen
import com.quizzer.app.ui.ThemedSnackbar
import com.quizzer.app.ui.TopicListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { QuizzerApp() }
    }

    override fun onStop() {
        super.onStop()
        // Switch the launcher icon only while the app isn't visible (see AppIcons).
        if (!isChangingConfigurations) AppIcons.applyChosen(this)
    }
}

/** Screens in navigation order; a higher rank slides in from the right. */
private enum class Screen(val rank: Int) { Topics(0), Themes(1), Licenses(2), Quiz(1), Result(2) }

@Composable
fun QuizzerApp(viewModel: QuizViewModel = viewModel()) {
    val entry = viewModel.currentTheme
    if (entry == null) {
        // Themes are still loading (a few ms at startup).
        Box(Modifier.fillMaxSize().background(Color(0xFF140A24)))
        return
    }

    SystemBarIcons(entry.theme)

    // Any file type is allowed: many file managers don't label .json files correctly.
    // The content is validated on import anyway.
    val topicImport = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) viewModel.importTopic(uri)
    }
    val themeImport = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) viewModel.importTheme(uri)
    }

    val session = viewModel.session
    val screen = when {
        session?.isFinished == true -> Screen.Result
        session != null -> Screen.Quiz
        viewModel.showThemePicker && viewModel.showLicenses -> Screen.Licenses
        viewModel.showThemePicker -> Screen.Themes
        else -> Screen.Topics
    }
    when (screen) {
        Screen.Quiz, Screen.Result -> BackHandler(onBack = viewModel::exitQuiz)
        Screen.Themes -> BackHandler(onBack = viewModel::closeThemePicker)
        Screen.Licenses -> BackHandler(onBack = viewModel::closeLicenses)
        Screen.Topics -> Unit
    }

    Crossfade(entry.theme, animationSpec = tween(300, easing = LinearEasing), label = "theme") { theme ->
        ProvideQuizTheme(theme) {
            Box(Modifier.fillMaxSize()) {
                val slide = with(LocalDensity.current) { 24.dp.roundToPx() }
                AnimatedContent(
                    targetState = screen,
                    transitionSpec = {
                        val dir = if (targetState.rank >= initialState.rank) 1 else -1
                        val spec = tween<androidx.compose.ui.unit.IntOffset>(250, easing = FastOutSlowInEasing)
                        (fadeIn(tween(250)) + slideInHorizontally(spec) { slide * dir })
                            .togetherWith(fadeOut(tween(250)) + slideOutHorizontally(spec) { -slide * dir })
                    },
                    label = "screen",
                ) { target ->
                    val s = viewModel.session
                    when (target) {
                        Screen.Topics -> TopicListScreen(
                            topics = viewModel.topics,
                            topicStats = viewModel.stats.topics,
                            isLoading = viewModel.isLoading,
                            onStartQuiz = viewModel::startQuiz,
                            onDeleteTopic = viewModel::deleteTopic,
                            onImportTopic = { topicImport.launch(arrayOf("*/*")) },
                            onOpenThemes = viewModel::openThemePicker,
                        )
                        Screen.Themes -> ThemePickerScreen(
                            themes = viewModel.themes,
                            currentId = viewModel.currentTheme?.theme?.id,
                            appIcon = viewModel.appIcon,
                            onSelectAppIcon = viewModel::selectAppIcon,
                            onOpenLicenses = viewModel::openLicenses,
                            onSelect = { viewModel.selectTheme(it.theme.id) },
                            onDelete = viewModel::deleteTheme,
                            onImport = { themeImport.launch(arrayOf("*/*")) },
                            onBack = viewModel::closeThemePicker,
                        )
                        Screen.Licenses -> LicensesScreen(onBack = viewModel::closeLicenses)
                        // The session may already be gone while this screen animates out.
                        Screen.Quiz -> if (s != null && !s.isFinished) QuizScreen(
                            session = s,
                            onToggleAnswer = viewModel::toggleAnswer,
                            onCheck = viewModel::checkAnswer,
                            onNext = viewModel::nextQuestion,
                            onQuit = viewModel::exitQuiz,
                        )
                        Screen.Result -> if (s != null && s.isFinished) ResultScreen(
                            s, onRetry = viewModel::restartQuiz, onBackToTopics = viewModel::exitQuiz,
                        )
                    }
                }

                ThemedSnackbar(viewModel.message, viewModel::messageShown, Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}

/** Light or dark status/navigation bar icons, as the theme requests. */
@Composable
private fun SystemBarIcons(theme: QuizTheme) {
    val view = LocalView.current
    if (view.isInEditMode) return
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = theme.darkSystemBarIcons
            isAppearanceLightNavigationBars = theme.darkSystemBarIcons
        }
    }
}
