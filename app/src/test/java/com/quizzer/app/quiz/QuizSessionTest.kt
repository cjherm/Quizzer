package com.quizzer.app.quiz

import com.quizzer.app.data.Answer
import com.quizzer.app.data.Question
import com.quizzer.app.data.QuestionResult
import com.quizzer.app.data.Topic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizSessionTest {

    private val multi = Question("t-0", "multi", listOf(Answer("a", true), Answer("b", false), Answer("c", true)))
    private val single = Question("t-1", "single", listOf(Answer("x", false), Answer("y", true)))
    private val topic = Topic("t", "T", "", listOf(multi, single))

    // Not shuffled, so indices are predictable.
    private fun session() = QuizSession(topic, topic.questions)

    private fun topicWith(n: Int) = Topic(
        "big", "Big", "",
        List(n) { i -> Question("big-$i", "q$i", listOf(Answer("yes", true), Answer("no", false))) },
    )

    @Test
    fun `results are recorded per question`() {
        assertEquals(QuestionResult.CORRECT, session().toggle(0).toggle(2).check().currentResult)
        assertEquals(QuestionResult.PARTIAL, session().toggle(0).check().currentResult)
        assertEquals(QuestionResult.WRONG, session().toggle(1).check().currentResult)
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

        val second = session().toggle(0).check().next()
        assertEquals(1, second.currentIndex)
        assertTrue(second.selected.isEmpty())
        assertFalse(second.isChecked)

        val done = second.toggle(1).check().next()
        assertTrue(done.isFinished)
        assertEquals(1, done.correctCount)
        assertEquals(1, done.partialCount)
        assertEquals(0, done.wrongCount)
    }

    @Test
    fun `required to win is 75 percent rounded up`() {
        assertEquals(12, QuizSession.requiredToWin(16))
        assertEquals(7, QuizSession.requiredToWin(9))
        assertEquals(4, QuizSession.requiredToWin(5))
        assertEquals(3, QuizSession.requiredToWin(4))
        assertEquals(2, QuizSession.requiredToWin(2))
        assertEquals(1, QuizSession.requiredToWin(1))
    }

    @Test
    fun `win needs enough fully correct answers - partial does not count`() {
        // 2 questions -> 2 needed. One correct + one partial = lose.
        val lost = session().toggle(0).toggle(2).check().next().toggle(0).toggle(1).check().next()
        assertEquals(QuestionResult.PARTIAL, lost.results[1])
        assertFalse(lost.isWon)

        val won = session().toggle(0).toggle(2).check().next().toggle(1).check().next()
        assertTrue(won.isWon)
    }

    @Test
    fun `game has 16 questions when the topic has more`() {
        val s = QuizSession.start(topicWith(40))
        assertEquals(16, s.questions.size)
        assertEquals(16, s.questions.map { it.id }.toSet().size)
        assertFalse(s.isShortGame)
    }

    @Test
    fun `short game uses all questions`() {
        val s = QuizSession.start(topicWith(9))
        assertEquals(9, s.questions.size)
        assertTrue(s.isShortGame)
        assertEquals(7, s.requiredToWin)
    }

    @Test
    fun `start shuffles but keeps all answers`() {
        val s = QuizSession.start(topic)
        s.questions.forEach { q ->
            val original = topic.questions.first { it.id == q.id }
            assertEquals(original.answers.toSet(), q.answers.toSet())
        }
    }
}
