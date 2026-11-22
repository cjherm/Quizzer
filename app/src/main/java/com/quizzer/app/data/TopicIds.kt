package com.quizzer.app.data

import java.text.Normalizer

/**
 * Generates topic IDs from titles.
 *
 * - Several words: lowercase initials. "Android Automotive Knowledge" -> `aak`
 * - One word: the word itself (max. 8 characters). "ASPICE" -> `aspice`
 * - Accents are removed, other characters than a–z/0–9 separate words.
 *
 * IDs only contain a–z and 0–9, so `-` can safely separate topic and question index.
 */
object TopicIds {

    private const val MAX_SINGLE_WORD_LENGTH = 8
    private const val FALLBACK = "topic"

    fun fromTitle(title: String): String {
        val plain = Normalizer.normalize(title, Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .lowercase()
        val words = plain.split(Regex("[^a-z0-9]+")).filter { it.isNotEmpty() }
        return when (words.size) {
            0 -> FALLBACK
            1 -> words.single().take(MAX_SINGLE_WORD_LENGTH)
            else -> words.joinToString("") { it.first().toString() }
        }
    }

    /** Returns [base], or `base2`, `base3`, … if it's already taken. */
    fun unique(base: String, taken: Set<String>): String {
        if (base !in taken) return base
        var n = 2
        while ("$base$n" in taken) n++
        return "$base$n"
    }
}
