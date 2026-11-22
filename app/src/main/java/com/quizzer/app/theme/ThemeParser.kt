package com.quizzer.app.theme

import androidx.compose.ui.graphics.Color
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Parses and validates a theme file (schema v1).
 *
 * A theme is "compatible" if it contains every token the app uses. Missing or invalid values
 * throw [IllegalArgumentException] with a message that names the offending token.
 */
object ThemeParser {

    const val SCHEMA_VERSION = 1
    private val ID_PATTERN = Regex("[a-z0-9][a-z0-9-]{0,39}")

    fun parse(json: String): QuizTheme {
        val root = try {
            JSONObject(json)
        } catch (e: JSONException) {
            throw IllegalArgumentException("File is not valid JSON (${e.message})")
        }

        val version = root.optInt("schemaVersion", -1)
        require(version != -1) { "Not a Quizzer theme file (missing \"schemaVersion\")" }
        require(version <= SCHEMA_VERSION) { "Theme needs a newer version of Quizzer (schema $version)" }

        val id = root.requireString("id")
        require(ID_PATTERN.matches(id)) { "Theme id \"$id\" may only contain a–z, 0–9 and -" }
        val name = root.requireString("name")

        val colorsJson = root.requireObject("colors")
        val colors = ColorId.entries.associateWith { cid ->
            parseHex(colorsJson.optString(cid.key, ""), "colors.${cid.key}")
        }
        val ctx = Context(colors)

        val surfacesJson = root.requireObject("surfaces")
        val shapesJson = root.requireObject("shapes")
        val textJson = root.requireObject("typography")
        val decorJson = root.requireObject("decor")

        return QuizTheme(
            id = id,
            name = name,
            author = root.optString("author"),
            darkSystemBarIcons = root.optString("statusBarIcons", "light") == "dark",
            colors = colors,
            background = ctx.paint(root.opt("background"), "background"),
            surfaces = SurfaceId.entries.associateWith { sid ->
                ctx.surface(surfacesJson.requireObject(sid.key, "surfaces"), "surfaces.${sid.key}")
            },
            shapes = ShapeId.entries.associateWith { sid ->
                shape(shapesJson.requireObject(sid.key, "shapes"), "shapes.${sid.key}")
            },
            text = TextId.entries.associateWith { tid ->
                ctx.textStyle(textJson.requireObject(tid.key, "typography"), "typography.${tid.key}")
            },
            decor = DecorId.entries.associateWith { did ->
                require(decorJson.has(did.key)) { "Theme is missing decor.${did.key} (use null for none)" }
                decorJson.optJSONObject(did.key)?.let { ctx.decor(it, "decor.${did.key}") }
            },
        )
    }

    private class Context(val colors: Map<ColorId, Color>) {

        fun colorSpec(value: Any?, where: String): ColorSpec {
            val s = value as? String ?: throw IllegalArgumentException("$where must be a color")
            return when {
                s.startsWith("#") -> ColorSpec.Fixed(parseHex(s, where))
                s == "content" -> ColorSpec.Content
                s == "contentAccent" -> ColorSpec.ContentAccent
                else -> ColorSpec.Fixed(
                    colors[ColorId.entries.firstOrNull { it.key == s }]
                        ?: throw IllegalArgumentException("$where: unknown color \"$s\""),
                )
            }
        }

        /** A color that must not depend on the surface (decor, effects, shadows). */
        fun fixedColor(value: Any?, where: String): Color =
            (colorSpec(value, where) as? ColorSpec.Fixed)?.color
                ?: throw IllegalArgumentException("$where can't use \"content\" here")

        fun paint(value: Any?, where: String): ThemePaint = when (value) {
            is JSONObject -> {
                val stops = value.optJSONArray("stops")
                    ?: throw IllegalArgumentException("$where: gradient is missing \"stops\"")
                require(stops.length() >= 2) { "$where: gradient needs at least 2 stops" }
                ThemePaint.Linear(
                    angle = value.optDouble("angle", 180.0).toFloat(),
                    stops = (0 until stops.length()).map { i ->
                        val stop = stops.getJSONObject(i)
                        stop.optDouble("offset", 0.0).toFloat() to colorSpec(stop.opt("color"), "$where.stops[$i]")
                    },
                )
            }
            else -> ThemePaint.Solid(colorSpec(value, where))
        }

        fun border(json: JSONObject?, where: String): ThemeBorder? = json?.let {
            ThemeBorder(
                width = it.optDouble("width", 1.0).toFloat(),
                color = colorSpec(it.opt("color"), "$where.color"),
                dashed = it.optString("style") == "dashed",
                topOnly = it.optString("sides") == "top",
            )
        }

        fun effect(json: JSONObject?, where: String): ThemeEffect? = json?.let {
            when (val type = it.optString("type")) {
                "glow" -> ThemeEffect.Glow(fixedColor(it.opt("color"), "$where.color"), it.float("radius"))
                "hardShadow" -> ThemeEffect.HardShadow(it.float("dx"), it.float("dy"), fixedColor(it.opt("color"), "$where.color"))
                else -> throw IllegalArgumentException("$where: unknown effect \"$type\"")
            }
        }

        fun surface(json: JSONObject, where: String) = ThemeSurface(
            fill = paint(json.opt("fill") ?: throw IllegalArgumentException("$where is missing \"fill\""), "$where.fill"),
            content = colorSpec(json.opt("content"), "$where.content"),
            contentAccent = if (json.has("contentAccent")) colorSpec(json.opt("contentAccent"), "$where.contentAccent") else null,
            border = border(json.optJSONObject("border"), "$where.border"),
            effect = effect(json.optJSONObject("effect"), "$where.effect"),
        )

        fun textStyle(json: JSONObject, where: String): ThemeTextStyle {
            val fontKey = json.optString("font")
            return ThemeTextStyle(
                font = FontId.entries.firstOrNull { it.key == fontKey }
                    ?: throw IllegalArgumentException("$where: unknown font \"$fontKey\""),
                weight = json.optInt("weight", 400).coerceIn(100, 900),
                size = json.float("size", where),
                lineHeight = json.optDouble("lineHeight", 0.0).toFloat(),
                letterSpacing = json.optDouble("letterSpacing", 0.0).toFloat(),
                uppercase = json.optBoolean("uppercase", false),
                skewX = json.optDouble("skewX", 0.0).toFloat(),
                color = colorSpec(json.opt("color"), "$where.color"),
                opacity = json.optDouble("opacity", 1.0).toFloat(),
                shadow = json.optJSONObject("shadow")?.let {
                    TextShadowSpec(colorSpec(it.opt("color"), "$where.shadow"), it.float("dx"), it.float("dy"), it.float("blur"))
                },
                stroke = json.optJSONObject("stroke")?.let {
                    TextStrokeSpec(it.float("width"), colorSpec(it.opt("color"), "$where.stroke"))
                },
            )
        }

        fun decor(json: JSONObject, where: String): ThemeDecor = when (val type = json.optString("type")) {
            "sunset" -> ThemeDecor.Sunset(
                sky = paint(json.opt("sky"), "$where.sky"),
                sun = paint(json.opt("sun"), "$where.sun"),
                sunDiameter = json.float("sunDiameter", where),
                sunCenterX = json.float("sunCenterX", where),
                sunCenterY = json.float("sunCenterY", where),
                sunOpacity = json.optDouble("sunOpacity", 1.0).toFloat(),
                stripes = json.optJSONObject("stripes")?.let {
                    ThemeDecor.Stripes(
                        count = it.optInt("count"), height = it.float("height"), gap = it.float("gap"),
                        color = fixedColor(it.opt("color"), "$where.stripes"), bottom = it.float("bottom"),
                    )
                },
                fade = json.optJSONObject("fade")?.let {
                    ThemeDecor.Fade(it.float("height"), it.float("from"), fixedColor(it.opt("color"), "$where.fade"))
                },
            )
            "glow" -> ThemeDecor.Glow(
                base = if (json.isNull("base")) null else paint(json.opt("base"), "$where.base"),
                color = fixedColor(json.opt("color"), "$where.color"),
                centerX = json.float("centerX", where),
                centerY = json.float("centerY", where),
                radius = json.float("radius", where),
                rain = json.optJSONObject("rain")?.let {
                    ThemeDecor.Rain(fixedColor(it.opt("color"), "$where.rain"), it.float("angle"), it.float("spacing"))
                },
            )
            "bands" -> {
                val colorsJson = json.optJSONArray("colors") ?: throw IllegalArgumentException("$where is missing \"colors\"")
                val bandColors = (0 until colorsJson.length()).map { fixedColor(colorsJson.get(it), "$where.colors[$it]") }
                val weights = json.optJSONArray("weights")?.floats()
                    ?: List(bandColors.size) { 1f / bandColors.size }
                require(weights.size == bandColors.size) { "$where: \"weights\" must match \"colors\"" }
                val inset = json.optJSONObject("inset")
                ThemeDecor.Bands(
                    colors = bandColors,
                    weights = weights,
                    inset = ThemeDecor.Insets(
                        top = inset?.float("top") ?: 0f, left = inset?.float("left") ?: 0f,
                        right = inset?.float("right") ?: 0f, bottom = inset?.float("bottom") ?: 0f,
                    ),
                    height = if (json.isNull("height")) null else json.float("height", where),
                    radius = json.optDouble("radius", 0.0).toFloat(),
                    border = border(json.optJSONObject("border"), "$where.border"),
                    effect = effect(json.optJSONObject("effect"), "$where.effect"),
                    circle = json.optJSONObject("circle")?.let {
                        ThemeDecor.Circle(fixedColor(it.opt("color"), "$where.circle"), it.float("diameter"), it.float("centerX"), it.float("centerY"))
                    },
                )
            }
            else -> throw IllegalArgumentException("$where: unknown decor type \"$type\"")
        }
    }

    private fun shape(json: JSONObject, where: String): ThemeShape = when (val type = json.optString("type")) {
        "slanted" -> ThemeShape.Slanted(json.float("slant", where))
        "rounded" -> {
            val c = json.optJSONArray("corners")?.floats() ?: List(4) { 0f }
            require(c.size == 4) { "$where: \"corners\" needs 4 values" }
            ThemeShape.Rounded(c[0], c[1], c[2], c[3])
        }
        else -> throw IllegalArgumentException("$where: unknown shape type \"$type\"")
    }

    internal fun parseHex(value: String, where: String): Color {
        val hex = value.removePrefix("#")
        require(value.startsWith("#") && (hex.length == 6 || hex.length == 8) && hex.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }) {
            "$where: \"$value\" is not a color (#RRGGBB or #AARRGGBB)"
        }
        val argb = hex.toLong(16) or (if (hex.length == 6) 0xFF000000L else 0L)
        return Color(argb.toInt())
    }

    private fun JSONObject.requireString(key: String): String =
        optString(key).trim().ifEmpty { throw IllegalArgumentException("Theme is missing \"$key\"") }

    private fun JSONObject.requireObject(key: String, parent: String? = null): JSONObject =
        optJSONObject(key) ?: throw IllegalArgumentException(
            "Theme is missing ${if (parent != null) "$parent.$key" else "\"$key\""}",
        )

    private fun JSONObject.float(key: String, where: String = ""): Float {
        val v = optDouble(key)
        require(!v.isNaN()) { "${where.ifEmpty { "Theme" }} is missing \"$key\"" }
        return v.toFloat()
    }

    private fun JSONArray.floats(): List<Float> = (0 until length()).map { getDouble(it).toFloat() }
}
