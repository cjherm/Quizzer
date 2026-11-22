package com.quizzer.app.data

/** Learning history: per question (by question ID) and per topic (by topic ID). */
data class Stats(
    val topics: Map<String, TopicStats> = emptyMap(),
    val questions: Map<String, QuestionStats> = emptyMap(),
) {
    fun withAnswer(questionId: String, result: QuestionResult): Stats {
        val old = questions[questionId] ?: QuestionStats()
        val new = old.copy(
            lastResult = result,
            timesAsked = old.timesAsked + 1,
            timesCorrect = old.timesCorrect + if (result == QuestionResult.CORRECT) 1 else 0,
        )
        return copy(questions = questions + (questionId to new))
    }

    fun withGame(topicId: String, won: Boolean): Stats {
        val old = topics[topicId] ?: TopicStats()
        val new = old.copy(gamesPlayed = old.gamesPlayed + 1, gamesWon = old.gamesWon + if (won) 1 else 0)
        return copy(topics = topics + (topicId to new))
    }

    /** Removes the topic's statistics and the history of all its questions. */
    fun withoutTopic(topicId: String): Stats =
        Stats(
            topics = topics - topicId,
            questions = questions.filterKeys { !it.startsWith("$topicId-") },
        )
}

data class TopicStats(
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
)

data class QuestionStats(
    val lastResult: QuestionResult? = null,
    val timesAsked: Int = 0,
    val timesCorrect: Int = 0,
)
