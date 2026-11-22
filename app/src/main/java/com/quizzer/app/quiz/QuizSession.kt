package com.quizzer.app.quiz

import com.quizzer.app.data.Question
import com.quizzer.app.data.Topic
import kotlin.random.Random

/**
 * Immutable state of one quiz run. Each action returns a new session.
 *
 * Flow per question: toggle answers -> [check] -> [next].
 * A question counts as correct only if exactly the correct answers were selected.
 */
data class QuizSession(
    val topic: Topic,
    val questions: List<Question>,
    val currentIndex: Int = 0,
    val selected: Set<Int> = emptySet(),
    val isChecked: Boolean = false,
    val correctCount: Int = 0,
    val isFinished: Boolean = false,
) {
    val current: Question get() = questions[currentIndex]
    val isLastQuestion: Boolean get() = currentIndex == questions.lastIndex
    val isCurrentAnswerCorrect: Boolean get() = selected == current.correctAnswerIndices

    fun toggle(answerIndex: Int): QuizSession =
        if (isChecked) this
        else copy(selected = if (answerIndex in selected) selected - answerIndex else selected + answerIndex)

    fun check(): QuizSession =
        if (isChecked || selected.isEmpty()) this
        else copy(isChecked = true, correctCount = correctCount + if (isCurrentAnswerCorrect) 1 else 0)

    fun next(): QuizSession = when {
        !isChecked -> this
        isLastQuestion -> copy(isFinished = true)
        else -> copy(currentIndex = currentIndex + 1, selected = emptySet(), isChecked = false)
    }

    companion object {
        /** Starts a quiz with questions and answers shuffled. */
        fun start(topic: Topic, random: Random = Random.Default): QuizSession =
            QuizSession(
                topic = topic,
                questions = topic.questions
                    .shuffled(random)
                    .map { it.copy(answers = it.answers.shuffled(random)) },
            )
    }
}
