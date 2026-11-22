package com.quizzer.app.data

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Stores topic files in the app's private storage as `files/topics/<topicId>.json`.
 * The file name is the topic ID, so an ID stays stable once assigned.
 *
 * Topics bundled in `assets/topics` are imported once per file, so they can be deleted like
 * any other topic. Topics added to the app later are imported on the next launch; deleted ones
 * don't come back.
 */
class TopicRepository(private val context: Context) {

    private val topicsDir = File(context.filesDir, "topics")
    private val prefs = context.getSharedPreferences("quizzer", Context.MODE_PRIVATE)

    suspend fun importNewBundledTopics() = withContext(Dispatchers.IO) {
        val imported = prefs.getStringSet(KEY_IMPORTED_ASSETS, emptySet()).orEmpty().toMutableSet()
        val new = context.assets.list(ASSET_DIR).orEmpty()
            .filter { it.endsWith(".json") && it !in imported }
        if (new.isEmpty()) return@withContext
        new.forEach { name ->
            val text = context.assets.open("$ASSET_DIR/$name").use { it.bufferedReader().readText() }
            try {
                importText(text)
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Bundled topic $name is invalid", e)
            }
            imported += name
        }
        prefs.edit { putStringSet(KEY_IMPORTED_ASSETS, imported) }
    }

    /** Loads all valid topics, sorted by title. Invalid files are skipped and logged. */
    suspend fun loadAll(): List<Topic> = withContext(Dispatchers.IO) { readAll() }

    /**
     * Validates the file at [uri] and copies it into the topic store.
     * A topic with the same title (ignoring case) replaces the existing one and keeps its ID.
     */
    suspend fun import(uri: Uri): Topic = withContext(Dispatchers.IO) {
        val text = context.contentResolver.openInputStream(uri)
            ?.use { it.bufferedReader().readText() }
            ?: throw IllegalArgumentException("Could not open the file")
        importText(text)
    }

    suspend fun delete(topic: Topic) = withContext(Dispatchers.IO) {
        fileFor(topic.id).delete()
    }

    private fun importText(text: String): Topic {
        val parsed = TopicParser.parse(text, topicId = "new")
        val existing = readAll()
        val id = existing.firstOrNull { it.title.equals(parsed.title, ignoreCase = true) }?.id
            ?: TopicIds.unique(TopicIds.fromTitle(parsed.title), existing.map { it.id }.toSet())
        topicsDir.mkdirs()
        fileFor(id).writeText(text)
        return parsed.withId(id)
    }

    private fun readAll(): List<Topic> =
        topicsDir.listFiles { f -> f.isFile && f.extension == "json" }.orEmpty()
            .mapNotNull { file ->
                try {
                    TopicParser.parse(file.readText(), topicId = file.nameWithoutExtension)
                } catch (e: Exception) {
                    Log.w(TAG, "Skipping invalid topic file ${file.name}", e)
                    null
                }
            }
            .sortedBy { it.title.lowercase() }

    private fun fileFor(topicId: String) = File(topicsDir, "$topicId.json")

    companion object {
        private const val TAG = "TopicRepository"
        private const val ASSET_DIR = "topics"
        private const val KEY_IMPORTED_ASSETS = "imported_topic_assets"
    }
}
