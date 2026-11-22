package com.quizzer.app.quiz

import com.quizzer.app.data.Question
import com.quizzer.app.data.QuestionResult
import com.quizzer.app.data.QuestionStats
import kotlin.math.pow
import kotlin.random.Random

/**
 * Picks the questions for a game, favouring questions that were answered wrong before.
 *
 * Weighted random sampling without replacement (Efraimidis–Spirakis):
 * each question gets the key `u^(1/weight)` with `u` uniform in [0, 1); the highest keys win.
 */
object QuestionPicker {

    const val GAME_SIZE = 16

    fun weight(stats: QuestionStats?): Double = when (stats?.lastResult) {
        QuestionResult.WRONG -> 4.0
        QuestionResult.PARTIAL -> 3.0
        null -> 2.0
        QuestionResult.CORRECT -> 1.0
    }

    /** Returns up to [count] distinct questions in random order. */
    fun pick(
        questions: List<Question>,
        history: Map<String, QuestionStats>,
        count: Int = GAME_SIZE,
        random: Random = Random.Default,
    ): List<Question> =
        questions
            .map { q -> q to random.nextDouble().pow(1.0 / weight(history[q.id])) }
            .sortedByDescending { it.second }
            .take(count)
            .map { it.first }
            // Sorting by key puts likely-wrong questions first; shuffle so the order doesn't give that away.
            .shuffled(random)
}
