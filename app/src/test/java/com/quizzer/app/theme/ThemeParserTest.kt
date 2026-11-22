package com.quizzer.app.theme

import androidx.compose.ui.graphics.Color
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File

class ThemeParserTest {

    private val themeDir = File("src/main/assets/themes")
    private fun bundled(id: String) = File(themeDir, "theme-$id.json").readText()

    @Test
    fun `all bundled themes parse`() {
        val files = themeDir.listFiles { f -> f.extension == "json" }.orEmpty()
        assertEquals(4, files.size)
        files.forEach { f ->
            val theme = ThemeParser.parse(f.readText())
            assertEquals("theme-${theme.id}.json", f.name)
            assertEquals(SurfaceId.entries.size, theme.surfaces.size)
            assertEquals(TextId.entries.size, theme.text.size)
        }
    }

    @Test
    fun `values are read correctly`() {
        val sunset = ThemeParser.parse(bundled("sunset-coast"))
        assertEquals("Sunset Coast", sunset.name)
        assertFalse(sunset.darkSystemBarIcons)
        assertEquals(Color(0xFFFFC65C), sunset.color(ColorId.Accent))
        assertEquals(Color(0xB80C0614), sunset.color(ColorId.Scrim))
        assertEquals(ThemeShape.Slanted(12f), sunset.shape(ShapeId.Answer))
        assertTrue(sunset.decor(DecorId.ListHeader) is ThemeDecor.Sunset)
        assertEquals(null, sunset.decor(DecorId.Screen))
        // "accent" reference resolved to the color; "content" kept as a reference.
        assertEquals(ColorSpec.Fixed(Color(0xFFFFC65C)), sunset.text(TextId.SectionLabel).color)
        assertEquals(ColorSpec.Content, sunset.text(TextId.TopicTitle).color)
        assertEquals(-9f, sunset.text(TextId.Logo).skewX)

        val postcard = ThemeParser.parse(bundled("postcard"))
        assertTrue(postcard.darkSystemBarIcons)
        assertTrue(postcard.surface(SurfaceId.AnswerMissed).border!!.dashed)
        assertTrue(postcard.decor(DecorId.ListHeader) is ThemeDecor.Bands)

        val neon = ThemeParser.parse(bundled("neon-night"))
        assertTrue(neon.surface(SurfaceId.AnswerSelected).effect is ThemeEffect.Glow)
        assertEquals(1.6f, neon.text(TextId.Logo).stroke!!.width)
        assertTrue(neon.text(TextId.Logo).shadow!!.color is ColorSpec.Fixed)
    }

    private fun assertRejected(json: String, messagePart: String) {
        try {
            ThemeParser.parse(json)
            fail("Expected the theme to be rejected")
        } catch (e: IllegalArgumentException) {
            assertTrue("'${e.message}' should contain '$messagePart'", e.message!!.contains(messagePart))
        }
    }

    @Test
    fun `incompatible themes are rejected with a clear message`() {
        assertRejected("not json", "not valid JSON")
        assertRejected("{}", "schemaVersion")

        val missingSurface = JSONObject(bundled("sunset-coast")).apply { getJSONObject("surfaces").remove("answerWrong") }
        assertRejected(missingSurface.toString(), "surfaces.answerWrong")

        val badColor = JSONObject(bundled("sunset-coast")).apply { getJSONObject("colors").put("accent", "orange") }
        assertRejected(badColor.toString(), "colors.accent")

        val badId = JSONObject(bundled("sunset-coast")).apply { put("id", "My Theme") }
        assertRejected(badId.toString(), "may only contain")

        val newer = JSONObject(bundled("sunset-coast")).apply { put("schemaVersion", 2) }
        assertRejected(newer.toString(), "newer version")
    }
}
