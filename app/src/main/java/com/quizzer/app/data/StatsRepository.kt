package com.quizzer.app.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

/**
 * Persists [Stats] as `files/stats.json`:
 *
 * ```
 * { "topics":    { "aak":   { "gamesPlayed": 8, "gamesWon": 5 } },
 *   "questions": { "aak-0": { "lastResult": "WRONG", "timesAsked": 3, "timesCorrect": 1 } } }
 * ```
 */
class StatsRepository(context: Context) {

    private val file = File(context.filesDir, "stats.json")
    private val mutex = Mutex()

    suspend fun load(): Stats = withContext(Dispatchers.IO) {
        if (!file.exists()) return@withContext Stats()
        try {
            decode(JSONObject(file.readText()))
        } catch (e: Exception) {
            Log.e(TAG, "Could not read statistics, starting fresh", e)
            Stats()
        }
    }

    /** Saves a snapshot. Calls are written in the order they were made. */
    suspend fun save(stats: Stats) = mutex.withLock {
        withContext(Dispatchers.IO) {
            val tmp = File(file.parentFile, "${file.name}.tmp")
            tmp.writeText(encode(stats).toString())
            if (!tmp.renameTo(file)) {
                file.delete()
                tmp.renameTo(file)
            }
        }
    }

    private fun encode(stats: Stats) = JSONObject().apply {
        put("topics", JSONObject().apply {
            stats.topics.forEach { (id, t) ->
                put(id, JSONObject().put("gamesPlayed", t.gamesPlayed).put("gamesWon", t.gamesWon))
            }
        })
        put("questions", JSONObject().apply {
            stats.questions.forEach { (id, q) ->
                put(id, JSONObject().apply {
                    q.lastResult?.let { put("lastResult", it.name) }
                    put("timesAsked", q.timesAsked)
                    put("timesCorrect", q.timesCorrect)
                })
            }
        })
    }

    private fun decode(json: JSONObject): Stats {
        val topics = json.optJSONObject("topics")?.let { obj ->
            obj.keys().asSequence().associateWith { id ->
                val t = obj.getJSONObject(id)
                TopicStats(gamesPlayed = t.optInt("gamesPlayed"), gamesWon = t.optInt("gamesWon"))
            }
        }.orEmpty()
        val questions = json.optJSONObject("questions")?.let { obj ->
            obj.keys().asSequence().associateWith { id ->
                val q = obj.getJSONObject(id)
                QuestionStats(
                    lastResult = q.optString("lastResult").let { name ->
                        QuestionResult.entries.firstOrNull { it.name == name }
                    },
                    timesAsked = q.optInt("timesAsked"),
                    timesCorrect = q.optInt("timesCorrect"),
                )
            }
        }.orEmpty()
        return Stats(topics, questions)
    }

    companion object {
        private const val TAG = "StatsRepository"
    }
}
