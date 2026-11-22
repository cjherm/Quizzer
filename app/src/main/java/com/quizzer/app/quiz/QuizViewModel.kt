package com.quizzer.app.quiz

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quizzer.app.data.Topic
import com.quizzer.app.data.TopicRepository
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TopicRepository(application)

    var topics by mutableStateOf<List<Topic>>(emptyList())
        private set
    var isLoading by mutableStateOf(true)
        private set
    var session by mutableStateOf<QuizSession?>(null)
        private set

    /** One-off message for the snackbar; call [messageShown] after displaying it. */
    var message by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            repository.seedSamplesIfNeeded()
            reloadTopics()
        }
    }

    private suspend fun reloadTopics() {
        topics = repository.loadAll()
        isLoading = false
    }

    fun importTopic(uri: Uri) {
        viewModelScope.launch {
            message = try {
                val topic = repository.import(uri)
                reloadTopics()
                "Imported \"${topic.title}\" (${topic.questions.size} questions)"
            } catch (e: Exception) {
                "Import failed: ${e.message}"
            }
        }
    }

    fun deleteTopic(topic: Topic) {
        viewModelScope.launch {
            repository.delete(topic)
            reloadTopics()
            message = "Deleted \"${topic.title}\""
        }
    }

    fun messageShown() {
        message = null
    }

    fun startQuiz(topic: Topic) {
        session = QuizSession.start(topic)
    }

    fun toggleAnswer(index: Int) {
        session = session?.toggle(index)
    }

    fun checkAnswer() {
        session = session?.check()
    }

    fun nextQuestion() {
        session = session?.next()
    }

    fun restartQuiz() {
        session = session?.let { QuizSession.start(it.topic) }
    }

    fun exitQuiz() {
        session = null
    }
}
