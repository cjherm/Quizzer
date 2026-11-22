package com.quizzer.app.theme

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** A theme plus where it comes from. */
data class ThemeEntry(
    val theme: QuizTheme,
    val builtIn: Boolean,
    /** Original file name for imported themes. */
    val sourceFile: String? = null,
)

/**
 * Built-in themes ship in `assets/themes`; imported themes are stored as `files/themes/<id>.json`.
 * The selected theme ID is remembered in shared preferences.
 */
class ThemeRepository(private val context: Context) {

    private val dir = File(context.filesDir, "themes")
    private val prefs = context.getSharedPreferences("quizzer", Context.MODE_PRIVATE)

    /** Built-in themes first (in [BUILT_IN_ORDER]), then imported themes by name. */
    suspend fun loadAll(): List<ThemeEntry> = withContext(Dispatchers.IO) {
        val builtIn = context.assets.list(ASSET_DIR).orEmpty()
            .filter { it.endsWith(".json") }
            .mapNotNull { name ->
                try {
                    val text = context.assets.open("$ASSET_DIR/$name").use { it.bufferedReader().readText() }
                    ThemeEntry(ThemeParser.parse(text), builtIn = true)
                } catch (e: IllegalArgumentException) {
                    Log.e(TAG, "Bundled theme $name is invalid", e)
                    null
                }
            }
            .sortedBy { BUILT_IN_ORDER.indexOf(it.theme.id).let { i -> if (i < 0) Int.MAX_VALUE else i } }
        val builtInIds = builtIn.map { it.theme.id }.toSet()

        val imported = dir.listFiles { f -> f.isFile && f.extension == "json" }.orEmpty()
            .mapNotNull { file ->
                try {
                    val theme = ThemeParser.parse(file.readText())
                    if (theme.id in builtInIds) null
                    else ThemeEntry(theme, builtIn = false, sourceFile = prefs.getString(sourceKey(theme.id), file.name))
                } catch (e: Exception) {
                    Log.w(TAG, "Skipping invalid theme file ${file.name}", e)
                    null
                }
            }
            .sortedBy { it.theme.name.lowercase() }

        builtIn + imported
    }

    /** Validates the theme at [uri] and stores it. An imported theme with the same ID is replaced. */
    suspend fun import(uri: Uri, builtInIds: Set<String>): ThemeEntry = withContext(Dispatchers.IO) {
        val text = context.contentResolver.openInputStream(uri)?.use { it.bufferedReader().readText() }
            ?: throw IllegalArgumentException("Could not open the file")
        val theme = ThemeParser.parse(text)
        require(theme.id !in builtInIds) { "\"${theme.name}\" has the same id as a built-in theme (${theme.id})" }
        val fileName = displayName(uri) ?: "${theme.id}.json"
        dir.mkdirs()
        File(dir, "${theme.id}.json").writeText(text)
        prefs.edit { putString(sourceKey(theme.id), fileName) }
        ThemeEntry(theme, builtIn = false, sourceFile = fileName)
    }

    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        File(dir, "$id.json").delete()
        prefs.edit { remove(sourceKey(id)) }
    }

    var selectedId: String
        get() = prefs.getString(KEY_SELECTED, DEFAULT_THEME_ID) ?: DEFAULT_THEME_ID
        set(value) = prefs.edit { putString(KEY_SELECTED, value) }

    private fun displayName(uri: Uri): String? =
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            if (c.moveToFirst()) c.getString(0) else null
        }

    private fun sourceKey(id: String) = "theme_source_$id"

    companion object {
        private const val TAG = "ThemeRepository"
        private const val ASSET_DIR = "themes"
        private const val KEY_SELECTED = "selected_theme"
        const val DEFAULT_THEME_ID = "sunset-coast"
        private val BUILT_IN_ORDER = listOf("sunset-coast", "neon-night", "postcard", "pocket-phone")
    }
}
