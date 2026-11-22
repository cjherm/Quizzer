package com.quizzer.app.quiz

import com.quizzer.app.data.Answer
import com.quizzer.app.data.Question
import com.quizzer.app.data.QuestionResult
import com.quizzer.app.data.QuestionStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class QuestionPickerTest {

    private fun questions(n: Int) =
        List(n) { i -> Question("t-$i", "q$i", listOf(Answer("a", true), Answer("b", false))) }

    @Test
    fun `picks distinct questions up to count`() {
        val picked = QuestionPicker.pick(questions(30), emptyMap(), count = 16, random = Random(1))
        assertEquals(16, picked.size)
        assertEquals(16, picked.map { it.id }.toSet().size)
    }

    @Test
    fun `picks all when fewer than count`() {
        val picked = QuestionPicker.pick(questions(5), emptyMap(), count = 16, random = Random(1))
        assertEquals(5, picked.size)
    }

    @Test
    fun `weights follow the plan`() {
        fun w(r: QuestionResult?) = QuestionPicker.weight(r?.let { QuestionStats(lastResult = it) })
        assertEquals(4.0, w(QuestionResult.WRONG), 0.0)
        assertEquals(3.0, w(QuestionResult.PARTIAL), 0.0)
        assertEquals(2.0, QuestionPicker.weight(null), 0.0)
        assertEquals(1.0, w(QuestionResult.CORRECT), 0.0)
    }

    @Test
    fun `previously wrong questions are picked more often than correct ones`() {
        // 20 questions: 0–9 last answered wrong (weight 4), 10–19 correct (weight 1). Pick 5.
        val qs = questions(20)
        val history = qs.associate { q ->
            val index = q.id.substringAfter('-').toInt()
            q.id to QuestionStats(lastResult = if (index < 10) QuestionResult.WRONG else QuestionResult.CORRECT)
        }
        val random = Random(42)
        var wrongPicks = 0
        var correctPicks = 0
        repeat(2000) {
            QuestionPicker.pick(qs, history, count = 5, random = random).forEach { q ->
                if (q.id.substringAfter('-').toInt() < 10) wrongPicks++ else correctPicks++
            }
        }
        assertTrue("wrong=$wrongPicks correct=$correctPicks", wrongPicks > correctPicks * 3)
    }
}
