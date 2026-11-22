package com.quizzer.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StatsTest {

    private val question = Question("t-0", "q", listOf(Answer("a", true), Answer("b", true), Answer("c", false)))

    @Test
    fun `question result is correct, partial or wrong`() {
        assertEquals(QuestionResult.CORRECT, QuestionResult.of(question, setOf(0, 1)))
        assertEquals(QuestionResult.PARTIAL, QuestionResult.of(question, setOf(0)))
        assertEquals(QuestionResult.PARTIAL, QuestionResult.of(question, setOf(0, 1, 2)))
        assertEquals(QuestionResult.WRONG, QuestionResult.of(question, setOf(2)))
    }

    @Test
    fun `answers update the question history`() {
        val stats = Stats()
            .withAnswer("t-0", QuestionResult.CORRECT)
            .withAnswer("t-0", QuestionResult.WRONG)
        assertEquals(QuestionStats(QuestionResult.WRONG, timesAsked = 2, timesCorrect = 1), stats.questions["t-0"])
    }

    @Test
    fun `games are counted per topic`() {
        val stats = Stats().withGame("t", won = true).withGame("t", won = false).withGame("u", won = true)
        assertEquals(TopicStats(gamesPlayed = 2, gamesWon = 1), stats.topics["t"])
        assertEquals(TopicStats(gamesPlayed = 1, gamesWon = 1), stats.topics["u"])
    }

    @Test
    fun `removing a topic only removes its own data`() {
        val stats = Stats()
            .withGame("aak", won = true).withGame("aak2", won = true)
            .withAnswer("aak-0", QuestionResult.CORRECT).withAnswer("aak2-0", QuestionResult.CORRECT)
            .withoutTopic("aak")
        assertNull(stats.topics["aak"])
        assertNull(stats.questions["aak-0"])
        assertEquals(setOf("aak2"), stats.topics.keys)
        assertEquals(setOf("aak2-0"), stats.questions.keys)
    }
}
