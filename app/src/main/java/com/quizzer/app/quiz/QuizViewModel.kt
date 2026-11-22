package com.quizzer.app.quiz

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quizzer.app.data.Stats
import com.quizzer.app.data.StatsRepository
import com.quizzer.app.data.Topic
import com.quizzer.app.data.TopicRepository
import com.quizzer.app.theme.ThemeEntry
import com.quizzer.app.theme.ThemeRepository
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val topicRepository = TopicRepository(application)
    private val statsRepository = StatsRepository(application)
    private val themeRepository = ThemeRepository(application)

    var topics by mutableStateOf<List<Topic>>(emptyList())
        private set
    var stats by mutableStateOf(Stats())
        private set
    var isLoading by mutableStateOf(true)
        private set
    var session by mutableStateOf<QuizSession?>(null)
        private set

    var themes by mutableStateOf<List<ThemeEntry>>(emptyList())
        private set
    var currentThemeId by mutableStateOf(themeRepository.selectedId)
        private set

    /** The active theme, or `null` while themes are still loading. */
    val currentTheme: ThemeEntry?
        get() = themes.firstOrNull { it.theme.id == currentThemeId } ?: themes.firstOrNull()

    var showThemePicker by mutableStateOf(false)
        private set

    /** One-off message for the snackbar; call [messageShown] after displaying it. */
    var message by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            themes = themeRepository.loadAll()
            topicRepository.seedSamplesIfNeeded()
            stats = statsRepository.load()
            reloadTopics()
        }
    }

    private suspend fun reloadTopics() {
        topics = topicRepository.loadAll()
        isLoading = false
    }

    /** Updates the in-memory stats immediately and persists them in the background. */
    private fun updateStats(transform: (Stats) -> Stats) {
        stats = transform(stats)
        val snapshot = stats
        viewModelScope.launch { statsRepository.save(snapshot) }
    }

    fun importTopic(uri: Uri) {
        viewModelScope.launch {
            message = try {
                val topic = topicRepository.import(uri)
                reloadTopics()
                "Imported \"${topic.title}\" (${topic.questions.size} questions)"
            } catch (e: Exception) {
                "Import failed: ${e.message}"
            }
        }
    }

    fun deleteTopic(topic: Topic) {
        viewModelScope.launch {
            topicRepository.delete(topic)
            updateStats { it.withoutTopic(topic.id) }
            reloadTopics()
            message = "Deleted \"${topic.title}\""
        }
    }

    fun messageShown() {
        message = null
    }

    fun startQuiz(topic: Topic) {
        session = QuizSession.start(topic, stats.questions)
    }

    fun toggleAnswer(index: Int) {
        session = session?.toggle(index)
    }

    fun checkAnswer() {
        val before = session ?: return
        val after = before.check()
        session = after
        val result = after.currentResult
        if (!before.isChecked && result != null) {
            // Recorded per question, so the history also counts when a game is quit early.
            updateStats { it.withAnswer(after.current.id, result) }
        }
    }

    fun nextQuestion() {
        val before = session ?: return
        val after = before.next()
        session = after
        if (after.isFinished && !before.isFinished) {
            updateStats { it.withGame(after.topic.id, after.isWon) }
        }
    }

    fun restartQuiz() {
        session = session?.let { QuizSession.start(it.topic, stats.questions) }
    }

    fun exitQuiz() {
        session = null
    }

    // ---- Themes ----

    fun openThemePicker() {
        showThemePicker = true
    }

    fun closeThemePicker() {
        showThemePicker = false
    }

    fun selectTheme(id: String) {
        currentThemeId = id
        themeRepository.selectedId = id
    }

    fun importTheme(uri: Uri) {
        viewModelScope.launch {
            message = try {
                val builtInIds = themes.filter { it.builtIn }.map { it.theme.id }.toSet()
                val entry = themeRepository.import(uri, builtInIds)
                themes = themeRepository.loadAll()
                "Imported theme \"${entry.theme.name}\""
            } catch (e: Exception) {
                "Import failed: ${e.message}"
            }
        }
    }

    fun deleteTheme(entry: ThemeEntry) {
        if (entry.builtIn || entry.theme.id == currentTheme?.theme?.id) return
        viewModelScope.launch {
            themeRepository.delete(entry.theme.id)
            themes = themeRepository.loadAll()
            message = "Deleted theme \"${entry.theme.name}\""
        }
    }
}
