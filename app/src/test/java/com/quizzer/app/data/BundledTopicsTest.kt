package com.quizzer.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Checks the topics shipped in assets/topics with the app's own parser. */
class BundledTopicsTest {

    private val topics = File("src/main/assets/topics")
        .listFiles { f -> f.extension == "json" }.orEmpty()
        .map { it.name to TopicParser.parse(it.readText(), topicId = "t") }

    @Test
    fun `all bundled topics parse and have enough questions for a full game`() {
        assertTrue("no bundled topics found", topics.isNotEmpty())
        topics.forEach { (file, topic) ->
            assertTrue("$file has only ${topic.questions.size} questions", topic.questions.size >= 16)
        }
    }

    @Test
    fun `bundled topics get distinct IDs`() {
        val ids = topics.map { (_, topic) -> TopicIds.fromTitle(topic.title) }
        assertEquals(ids.toString(), ids.size, ids.toSet().size)
    }

    @Test
    fun `every question has an explanation`() {
        topics.forEach { (file, topic) ->
            topic.questions.forEach { q -> assertTrue("$file: '${q.text}' has no explanation", q.explanation != null) }
        }
    }
}
