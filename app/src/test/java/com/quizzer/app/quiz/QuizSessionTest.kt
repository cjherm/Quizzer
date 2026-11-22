package com.quizzer.app.quiz

import com.quizzer.app.data.Answer
import com.quizzer.app.data.Question
import com.quizzer.app.data.Topic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizSessionTest {

    private val multi = Question("multi", listOf(Answer("a", true), Answer("b", false), Answer("c", true)))
    private val single = Question("single", listOf(Answer("x", false), Answer("y", true)))
    private val topic = Topic("t.json", "T", "", listOf(multi, single))

    // Not shuffled, so indices are predictable.
    private fun session() = QuizSession(topic, topic.questions)

    @Test
    fun `exact selection of all correct answers counts as correct`() {
        val s = session().toggle(0).toggle(2).check()
        assertTrue(s.isCurrentAnswerCorrect)
        assertEquals(1, s.correctCount)
    }

    @Test
    fun `partial selection counts as wrong`() {
        val s = session().toggle(0).check()
        assertFalse(s.isCurrentAnswerCorrect)
        assertEquals(0, s.correctCount)
    }

    @Test
    fun `selecting an extra wrong answer counts as wrong`() {
        val s = session().toggle(0).toggle(1).toggle(2).check()
        assertEquals(0, s.correctCount)
    }

    @Test
    fun `toggling twice deselects`() {
        assertEquals(emptySet<Int>(), session().toggle(1).toggle(1).selected)
    }

    @Test
    fun `cannot check without a selection or change answers after checking`() {
        assertFalse(session().check().isChecked)
        val checked = session().toggle(0).check()
        assertEquals(checked, checked.toggle(1))
        assertEquals(checked, checked.check())
    }

    @Test
    fun `next requires check and finishes after last question`() {
        assertEquals(0, session().toggle(0).next().currentIndex)

        val second = session().toggle(0).toggle(2).check().next()
        assertEquals(1, second.currentIndex)
        assertTrue(second.selected.isEmpty())
        assertFalse(second.isChecked)

        val done = second.toggle(1).check().next()
        assertTrue(done.isFinished)
        assertEquals(2, done.correctCount)
    }

    @Test
    fun `start shuffles but keeps all questions and answers`() {
        val s = QuizSession.start(topic)
        assertEquals(topic.questions.map { it.text }.toSet(), s.questions.map { it.text }.toSet())
        s.questions.forEach { q ->
            val original = topic.questions.first { it.text == q.text }
            assertEquals(original.answers.toSet(), q.answers.toSet())
        }
    }
}
