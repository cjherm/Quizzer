package com.quizzer.app.data

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Parses and validates a topic file. See README.md for the format.
 *
 * Throws [IllegalArgumentException] with a human-readable message if the file is invalid.
 */
object TopicParser {

    const val MIN_ANSWERS = 2
    const val MAX_ANSWERS = 4

    fun parse(json: String, id: String): Topic {
        val root = try {
            JSONObject(json)
        } catch (e: JSONException) {
            throw IllegalArgumentException("File is not valid JSON (${e.message})")
        }

        val title = root.requireText("title", "Topic")
        val description = root.optText("description").orEmpty()
        val questionsJson = root.optJSONArray("questions")
            ?: throw IllegalArgumentException("Topic is missing the \"questions\" list")
        require(questionsJson.length() > 0) { "Topic has no questions" }

        val questions = questionsJson.objects("Question").mapIndexed { i, q -> parseQuestion(q, i + 1) }
        return Topic(id = id, title = title, description = description, questions = questions)
    }

    private fun parseQuestion(json: JSONObject, number: Int): Question {
        val where = "Question $number"
        val text = json.requireText("question", where)
        val answersJson = json.optJSONArray("answers")
            ?: throw IllegalArgumentException("$where is missing the \"answers\" list")
        require(answersJson.length() in MIN_ANSWERS..MAX_ANSWERS) {
            "$where must have $MIN_ANSWERS to $MAX_ANSWERS answers (has ${answersJson.length()})"
        }
        val answers = answersJson.objects("$where, answer").mapIndexed { i, a ->
            Answer(
                text = a.requireText("text", "$where, answer ${i + 1}"),
                correct = a.optBoolean("correct", false),
            )
        }
        require(answers.any { it.correct }) { "$where needs at least one correct answer" }
        return Question(text = text, answers = answers, explanation = json.optText("explanation"))
    }

    private fun JSONObject.optText(key: String): String? =
        if (isNull(key)) null else optString(key).trim().ifEmpty { null }

    private fun JSONObject.requireText(key: String, where: String): String =
        optText(key) ?: throw IllegalArgumentException("$where is missing \"$key\"")

    private fun JSONArray.objects(what: String): List<JSONObject> =
        (0 until length()).map { i ->
            optJSONObject(i) ?: throw IllegalArgumentException("$what ${i + 1} is not a JSON object")
        }
}
