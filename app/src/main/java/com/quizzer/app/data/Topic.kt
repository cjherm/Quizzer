package com.quizzer.app.data

/** A quiz topic, loaded from one JSON file. [id] is the file name it is stored under. */
data class Topic(
    val id: String,
    val title: String,
    val description: String,
    val questions: List<Question>,
)

/** A question with 2–4 answers, of which at least one is correct. */
data class Question(
    val text: String,
    val answers: List<Answer>,
    val explanation: String? = null,
) {
    val correctAnswerIndices: Set<Int>
        get() = answers.indices.filter { answers[it].correct }.toSet()
}

data class Answer(
    val text: String,
    val correct: Boolean,
)
