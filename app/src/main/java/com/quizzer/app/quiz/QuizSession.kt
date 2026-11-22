package com.quizzer.app.quiz

import com.quizzer.app.data.Question
import com.quizzer.app.data.QuestionResult
import com.quizzer.app.data.QuestionStats
import com.quizzer.app.data.Topic
import kotlin.random.Random

/**
 * Immutable state of one game. Each action returns a new session.
 *
 * Flow per question: toggle answers -> [check] -> [next].
 */
data class QuizSession(
    val topic: Topic,
    val questions: List<Question>,
    val currentIndex: Int = 0,
    val selected: Set<Int> = emptySet(),
    /** One result per checked question, in question order. */
    val results: List<QuestionResult> = emptyList(),
    val isFinished: Boolean = false,
) {
    val current: Question get() = questions[currentIndex]
    val isLastQuestion: Boolean get() = currentIndex == questions.lastIndex
    val isChecked: Boolean get() = results.size > currentIndex
    val currentResult: QuestionResult? get() = results.getOrNull(currentIndex)

    val correctCount: Int get() = results.count { it == QuestionResult.CORRECT }
    val partialCount: Int get() = results.count { it == QuestionResult.PARTIAL }
    val wrongCount: Int get() = results.count { it == QuestionResult.WRONG }

    val isShortGame: Boolean get() = questions.size < QuestionPicker.GAME_SIZE
    val requiredToWin: Int get() = requiredToWin(questions.size)
    val isWon: Boolean get() = correctCount >= requiredToWin

    fun toggle(answerIndex: Int): QuizSession =
        if (isChecked) this
        else copy(selected = if (answerIndex in selected) selected - answerIndex else selected + answerIndex)

    fun check(): QuizSession =
        if (isChecked || selected.isEmpty()) this
        else copy(results = results + QuestionResult.of(current, selected))

    fun next(): QuizSession = when {
        !isChecked -> this
        isLastQuestion -> copy(isFinished = true)
        else -> copy(currentIndex = currentIndex + 1, selected = emptySet())
    }

    companion object {
        const val WIN_PERCENT = 75

        /** Correct answers needed to win: [WIN_PERCENT] % of [questionCount], rounded up. */
        fun requiredToWin(questionCount: Int): Int = (questionCount * WIN_PERCENT + 99) / 100

        /** Starts a game: up to 16 questions picked by [QuestionPicker], answers shuffled. */
        fun start(
            topic: Topic,
            history: Map<String, QuestionStats> = emptyMap(),
            random: Random = Random.Default,
        ): QuizSession =
            QuizSession(
                topic = topic,
                questions = QuestionPicker.pick(topic.questions, history, random = random)
                    .map { it.copy(answers = it.answers.shuffled(random)) },
            )
    }
}
