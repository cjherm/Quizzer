package com.quizzer.app.theme

import androidx.compose.ui.graphics.Color

/**
 * A visual theme, loaded from a theme JSON file (schema v1, see docs/design/themes/<id>/handoff.md).
 *
 * The layout is the same for every theme; a theme only provides the values below.
 * All sizes are in dp, text sizes in sp.
 */
data class QuizTheme(
    val id: String,
    val name: String,
    val author: String,
    /** `true` if the system bar icons should be dark (for light backgrounds). */
    val darkSystemBarIcons: Boolean,
    val colors: Map<ColorId, Color>,
    val background: ThemePaint,
    val surfaces: Map<SurfaceId, ThemeSurface>,
    val shapes: Map<ShapeId, ThemeShape>,
    val text: Map<TextId, ThemeTextStyle>,
    val decor: Map<DecorId, ThemeDecor?>,
) {
    fun color(id: ColorId): Color = colors.getValue(id)
    fun surface(id: SurfaceId): ThemeSurface = surfaces.getValue(id)
    fun shape(id: ShapeId): ThemeShape = shapes.getValue(id)
    fun text(id: TextId): ThemeTextStyle = text.getValue(id)
    fun decor(id: DecorId): ThemeDecor? = decor[id]
}

// ---- Token names (the keys in the JSON file) ----

enum class ColorId(val key: String) {
    TextPrimary("textPrimary"), TextSecondary("textSecondary"), TextMuted("textMuted"),
    Accent("accent"), Highlight("highlight"), OnFill("onFill"), DisplayShadow("displayShadow"),
    Scrim("scrim"), ChartCorrect("chartCorrect"), ChartPartial("chartPartial"),
    ChartWrong("chartWrong"), ChartGap("chartGap"),
}

enum class SurfaceId(val key: String) {
    TopicRow("topicRow"), TopicRowPressed("topicRowPressed"), QuestionCard("questionCard"),
    AnswerIdle("answerIdle"), AnswerSelected("answerSelected"), AnswerCorrect("answerCorrect"),
    AnswerMissed("answerMissed"), AnswerWrong("answerWrong"), AnswerDimmed("answerDimmed"),
    Checkbox("checkbox"), CheckboxChecked("checkboxChecked"),
    FeedbackCorrect("feedbackCorrect"), FeedbackPartial("feedbackPartial"), FeedbackWrong("feedbackWrong"),
    ButtonPrimary("buttonPrimary"), ButtonPrimaryPressed("buttonPrimaryPressed"),
    ButtonDisabled("buttonDisabled"), ButtonSecondary("buttonSecondary"),
    ProgressTrack("progressTrack"), ProgressFill("progressFill"),
    Dialog("dialog"), Snackbar("snackbar"), LegendPanel("legendPanel"),
    ChipWin("chipWin"), ChipLose("chipLose"), ChipCurrent("chipCurrent"),
}

enum class ShapeId(val key: String) {
    TopicRow("topicRow"), Answer("answer"), Feedback("feedback"), Button("button"), Chip("chip"),
    Progress("progress"), Card("card"), Dialog("dialog"), Checkbox("checkbox"), Preview("preview"),
}

enum class TextId(val key: String) {
    Logo("logo"), SectionLabel("sectionLabel"), TopicIndex("topicIndex"), TopicTitle("topicTitle"),
    TopicDescription("topicDescription"), TopicMeta("topicMeta"), TopicStats("topicStats"),
    ImportAction("importAction"), HeaderSmall("headerSmall"), Back("back"), Counter("counter"),
    CounterTotal("counterTotal"), Question("question"), Hint("hint"), AnswerLetter("answerLetter"),
    Answer("answer"), Feedback("feedback"), Explanation("explanation"), Button("button"),
    ButtonSecondary("buttonSecondary"), Chip("chip"), PageTitle("pageTitle"), Headline("headline"),
    Score("score"), ScoreTotal("scoreTotal"), Percent("percent"), LegendLabel("legendLabel"),
    LegendValue("legendValue"), DialogTitle("dialogTitle"), DialogBody("dialogBody"),
    DialogAction("dialogAction"), Snackbar("snackbar"), Body("body"),
}

enum class DecorId(val key: String) {
    ListHeader("listHeader"), ResultWin("resultWin"), ResultLose("resultLose"), Screen("screen"),
}

enum class FontId(val key: String) {
    Anton("anton"), Inter("inter"), Shrikhand("shrikhand"), Oswald("oswald"),
}

// ---- Value types ----

/**
 * A color that may depend on where it is used:
 * a fixed color, or the content / accent-content color of the surface it sits on.
 */
sealed interface ColorSpec {
    data class Fixed(val color: Color) : ColorSpec
    data object Content : ColorSpec
    data object ContentAccent : ColorSpec
}

sealed interface ThemePaint {
    data class Solid(val color: ColorSpec) : ThemePaint

    /** CSS-style angle: 0 = bottom→top, 90 = left→right, 180 = top→bottom. */
    data class Linear(val angle: Float, val stops: List<Pair<Float, ColorSpec>>) : ThemePaint
}

data class ThemeBorder(
    val width: Float,
    val color: ColorSpec,
    val dashed: Boolean = false,
    val topOnly: Boolean = false,
)

sealed interface ThemeEffect {
    /** Blurred shadow without offset, following the shape. */
    data class Glow(val color: Color, val radius: Float) : ThemeEffect

    /** The shape filled and offset, no blur. */
    data class HardShadow(val dx: Float, val dy: Float, val color: Color) : ThemeEffect
}

/**
 * [content] is the color of text and icons on the surface. Inside [content] itself,
 * [ColorSpec.Content] means the content color of the *parent* surface (used by checkboxes);
 * inside [fill] and [border] it means this surface's own content color.
 */
data class ThemeSurface(
    val fill: ThemePaint,
    val content: ColorSpec,
    val contentAccent: ColorSpec?,
    val border: ThemeBorder?,
    val effect: ThemeEffect?,
)

sealed interface ThemeShape {
    /** Parallelogram: top-left and bottom-right corners inset horizontally by [slant]. */
    data class Slanted(val slant: Float) : ThemeShape
    data class Rounded(val topStart: Float, val topEnd: Float, val bottomEnd: Float, val bottomStart: Float) : ThemeShape
}

data class TextShadowSpec(val color: ColorSpec, val dx: Float, val dy: Float, val blur: Float)
data class TextStrokeSpec(val width: Float, val color: ColorSpec)

data class ThemeTextStyle(
    val font: FontId,
    val weight: Int,
    val size: Float,
    val lineHeight: Float,
    /** In em. */
    val letterSpacing: Float,
    val uppercase: Boolean,
    /** Degrees; negative leans right. */
    val skewX: Float,
    val color: ColorSpec,
    val opacity: Float,
    val shadow: TextShadowSpec?,
    val stroke: TextStrokeSpec?,
)

// ---- Decor (backgrounds drawn by the app; themes pick a style and set its parameters) ----

sealed interface ThemeDecor {
    data class Sunset(
        val sky: ThemePaint,
        val sun: ThemePaint,
        val sunDiameter: Float,
        /** Fraction of the width. */
        val sunCenterX: Float,
        /** dp from the top. */
        val sunCenterY: Float,
        val sunOpacity: Float,
        val stripes: Stripes?,
        val fade: Fade?,
    ) : ThemeDecor

    data class Glow(
        val base: ThemePaint?,
        val color: Color,
        /** Fractions of the decor area (may exceed 1). */
        val centerX: Float,
        val centerY: Float,
        val radius: Float,
        val rain: Rain?,
    ) : ThemeDecor

    data class Bands(
        val colors: List<Color>,
        val weights: List<Float>,
        val inset: Insets,
        val height: Float?,
        val radius: Float,
        val border: ThemeBorder?,
        val effect: ThemeEffect?,
        val circle: Circle?,
    ) : ThemeDecor

    data class Stripes(val count: Int, val height: Float, val gap: Float, val color: Color, val bottom: Float)
    data class Fade(val height: Float, val from: Float, val color: Color)
    data class Rain(val color: Color, val angle: Float, val spacing: Float)
    data class Insets(val top: Float, val left: Float, val right: Float, val bottom: Float)

    /** Centers are fractions of the band card. */
    data class Circle(val color: Color, val diameter: Float, val centerX: Float, val centerY: Float)
}
