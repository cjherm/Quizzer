package com.quizzer.app.data

import org.junit.Assert.assertEquals
import org.junit.Test

class TopicIdsTest {

    @Test
    fun `several words become initials`() {
        assertEquals("aak", TopicIds.fromTitle("Android Automotive Knowledge"))
        assertEquals("gkd", TopicIds.fromTitle("General Knowledge (Demo)"))
        assertEquals("i2b", TopicIds.fromTitle("ISO 26262 Basics"))
    }

    @Test
    fun `single word is used as a whole, max 8 characters`() {
        assertEquals("aspice", TopicIds.fromTitle("ASPICE"))
        assertEquals("kubernet", TopicIds.fromTitle("Kubernetes"))
    }

    @Test
    fun `accents are removed and symbols separate words`() {
        assertEquals("ufs", TopicIds.fromTitle("Übungen für Säure-"))
        assertEquals("cs", TopicIds.fromTitle("C++ / Swift"))
    }

    @Test
    fun `falls back when there are no usable characters`() {
        assertEquals("topic", TopicIds.fromTitle("!!!"))
        assertEquals("topic", TopicIds.fromTitle("行くぞ"))
    }

    @Test
    fun `collisions get a number`() {
        assertEquals("aak", TopicIds.unique("aak", setOf("abc")))
        assertEquals("aak2", TopicIds.unique("aak", setOf("aak")))
        assertEquals("aak3", TopicIds.unique("aak", setOf("aak", "aak2")))
    }
}
