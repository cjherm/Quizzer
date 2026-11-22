package com.quizzer.app.data

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Stores topic files in the app's private storage (one JSON file per topic in `files/topics`).
 *
 * On first launch the sample topics bundled in `assets/topics` are copied there,
 * so they can be deleted like any imported topic.
 */
class TopicRepository(private val context: Context) {

    private val topicsDir = File(context.filesDir, "topics")
    private val prefs = context.getSharedPreferences("quizzer", Context.MODE_PRIVATE)

    suspend fun seedSamplesIfNeeded() = withContext(Dispatchers.IO) {
        if (prefs.getBoolean(KEY_SAMPLES_SEEDED, false)) return@withContext
        topicsDir.mkdirs()
        context.assets.list(ASSET_DIR).orEmpty()
            .filter { it.endsWith(".json") }
            .forEach { name ->
                context.assets.open("$ASSET_DIR/$name").use { input ->
                    File(topicsDir, name).outputStream().use { input.copyTo(it) }
                }
            }
        prefs.edit { putBoolean(KEY_SAMPLES_SEEDED, true) }
    }

    /** Loads all valid topics, sorted by title. Invalid files are skipped and logged. */
    suspend fun loadAll(): List<Topic> = withContext(Dispatchers.IO) {
        topicsDir.listFiles { f -> f.isFile && f.extension == "json" }.orEmpty()
            .mapNotNull { file ->
                try {
                    TopicParser.parse(file.readText(), file.name)
                } catch (e: Exception) {
                    Log.w(TAG, "Skipping invalid topic file ${file.name}", e)
                    null
                }
            }
            .sortedBy { it.title.lowercase() }
    }

    /**
     * Validates the file at [uri] and copies it into the topic store.
     * A topic with the same title replaces the existing one.
     */
    suspend fun import(uri: Uri): Topic = withContext(Dispatchers.IO) {
        val text = context.contentResolver.openInputStream(uri)
            ?.use { it.bufferedReader().readText() }
            ?: throw IllegalArgumentException("Could not open the file")
        val topic = TopicParser.parse(text, id = "")
        val fileName = fileNameFor(topic.title)
        topicsDir.mkdirs()
        File(topicsDir, fileName).writeText(text)
        topic.copy(id = fileName)
    }

    suspend fun delete(topic: Topic) = withContext(Dispatchers.IO) {
        File(topicsDir, topic.id).delete()
    }

    companion object {
        private const val TAG = "TopicRepository"
        private const val ASSET_DIR = "topics"
        private const val KEY_SAMPLES_SEEDED = "samples_seeded"

        fun fileNameFor(title: String): String =
            title.lowercase()
                .replace(Regex("[^a-z0-9]+"), "_")
                .trim('_')
                .ifEmpty { "topic" } + ".json"
    }
}
