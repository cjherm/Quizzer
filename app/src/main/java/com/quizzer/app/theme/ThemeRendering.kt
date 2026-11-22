package com.quizzer.app.theme

import android.graphics.BlurMaskFilter
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.quizzer.app.R
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

// ---- Composition locals ----

val LocalQuizTheme = staticCompositionLocalOf<QuizTheme> { error("No QuizTheme provided") }

/** Content colors of the surface the current composable sits on. */
@Immutable
data class ContentColors(val content: Color, val accent: Color)

val LocalContentColors = compositionLocalOf { ContentColors(Color.White, Color.White) }

/** Provides [theme] and the default content colors (text on the background). */
@Composable
fun ProvideQuizTheme(theme: QuizTheme, content: @Composable () -> Unit) {
    val text = theme.color(ColorId.TextPrimary)
    CompositionLocalProvider(
        LocalQuizTheme provides theme,
        LocalContentColors provides ContentColors(text, theme.color(ColorId.Accent)),
        content = content,
    )
}

// ---- Colors and paints ----

fun ColorSpec.resolve(colors: ContentColors): Color = when (this) {
    is ColorSpec.Fixed -> color
    ColorSpec.Content -> colors.content
    ColorSpec.ContentAccent -> colors.accent
}

/** The surface's own content colors, given the colors of the surface it sits on. */
fun ThemeSurface.contentColors(parent: ContentColors): ContentColors {
    val own = content.resolve(parent)
    return ContentColors(own, contentAccent?.resolve(ContentColors(own, parent.accent)) ?: own)
}

fun ThemePaint.toBrush(size: Size, colors: ContentColors): Brush = when (this) {
    is ThemePaint.Solid -> SolidColor(color.resolve(colors))
    is ThemePaint.Linear -> {
        // CSS convention: 0° = towards the top, 90° = towards the right.
        val rad = Math.toRadians(angle.toDouble())
        val dir = Offset(sin(rad).toFloat(), -cos(rad).toFloat())
        val length = (abs(size.width * sin(rad)) + abs(size.height * cos(rad))).toFloat()
        val center = Offset(size.width / 2, size.height / 2)
        Brush.linearGradient(
            colorStops = stops.map { (offset, c) -> offset to c.resolve(colors) }.toTypedArray(),
            start = center - dir * (length / 2),
            end = center + dir * (length / 2),
        )
    }
}

/** A representative solid color (first stop of a gradient). */
fun ThemePaint.firstColor(colors: ContentColors): Color = when (this) {
    is ThemePaint.Solid -> color.resolve(colors)
    is ThemePaint.Linear -> stops.first().second.resolve(colors)
}

// ---- Shapes ----

class SlantedShape(private val slant: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val s = with(density) { slant.toPx() }.coerceAtMost(size.width / 2)
        return Outline.Generic(Path().apply {
            moveTo(s, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width - s, size.height)
            lineTo(0f, size.height)
            close()
        })
    }
}

fun ThemeShape.toShape(): Shape = when (this) {
    is ThemeShape.Slanted -> SlantedShape(slant.dp)
    is ThemeShape.Rounded -> androidx.compose.foundation.shape.RoundedCornerShape(
        topStart = topStart.dp, topEnd = topEnd.dp, bottomEnd = bottomEnd.dp, bottomStart = bottomStart.dp,
    )
}

fun Outline.toPath(): Path = when (this) {
    is Outline.Generic -> path
    is Outline.Rectangle -> Path().apply { addRect(rect) }
    is Outline.Rounded -> Path().apply { addRoundRect(roundRect) }
}

// ---- Surfaces ----

/** Draws effect, fill and border of [surface] in [shape] behind the content. */
fun Modifier.themedSurface(surface: ThemeSurface, shape: Shape, colors: ContentColors): Modifier =
    drawBehind {
        val path = shape.createOutline(size, layoutDirection, this).toPath()
        drawSurface(path, size, surface.fill, surface.border, surface.effect, colors)
    }

fun DrawScope.drawSurface(
    path: Path,
    size: Size,
    fill: ThemePaint,
    border: ThemeBorder?,
    effect: ThemeEffect?,
    colors: ContentColors,
) {
    when (effect) {
        is ThemeEffect.Glow -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Like CSS box-shadow: not drawn underneath the surface itself.
            clipPath(path, ClipOp.Difference) {
                drawIntoCanvas { canvas ->
                    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        color = effect.color.toArgb()
                        maskFilter = BlurMaskFilter(effect.radius.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
                    }
                    canvas.nativeCanvas.drawPath(path.asAndroidPath(), paint)
                }
            }
        }
        is ThemeEffect.HardShadow -> clipPath(path, ClipOp.Difference) {
            translate(effect.dx.dp.toPx(), effect.dy.dp.toPx()) { drawPath(path, effect.color) }
        }
        null -> Unit
    }
    drawPath(path, fill.toBrush(size, colors))
    if (border != null) drawBorder(path, size, border, colors)
}

/** Border inside the shape (like CSS): the stroke is doubled and clipped to the shape. */
fun DrawScope.drawBorder(path: Path, size: Size, border: ThemeBorder, colors: ContentColors) {
    val width = border.width.dp.toPx()
    val color = border.color.resolve(colors)
    clipPath(path) {
        if (border.topOnly) {
            drawRect(color, size = Size(size.width, width))
        } else {
            drawPath(
                path, color,
                style = Stroke(
                    width = width * 2,
                    pathEffect = if (border.dashed) PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())) else null,
                ),
            )
        }
    }
}

/** A box drawn as [surfaceId] in [shapeId] that provides its content colors to [content]. */
@Composable
fun ThemedBox(
    surfaceId: SurfaceId,
    shapeId: ShapeId,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    val theme = LocalQuizTheme.current
    val surface = theme.surface(surfaceId)
    val colors = surface.contentColors(LocalContentColors.current)
    Box(
        modifier.themedSurface(surface, theme.shape(shapeId).toShape(), colors),
        contentAlignment = contentAlignment,
    ) {
        CompositionLocalProvider(LocalContentColors provides colors) { content() }
    }
}

// ---- Text ----

@OptIn(ExperimentalTextApi::class)
object ThemeFonts {
    private fun variable(res: Int, weights: IntRange) = FontFamily(
        weights.step(100).map { w ->
            Font(res, FontWeight(w), variationSettings = FontVariation.Settings(FontVariation.weight(w)))
        },
    )

    private val anton = FontFamily(Font(R.font.anton_regular))
    private val shrikhand = FontFamily(Font(R.font.shrikhand_regular))
    private val inter = variable(R.font.inter_variable, 100..900)
    private val oswald = variable(R.font.oswald_variable, 200..700)

    fun family(font: FontId): FontFamily = when (font) {
        FontId.Anton -> anton
        FontId.Inter -> inter
        FontId.Shrikhand -> shrikhand
        FontId.Oswald -> oswald
    }

    /** Anton has no Cyrillic glyphs; the handoff uses Oswald Bold for those. */
    fun forText(font: FontId, text: String): Pair<FontId, Int?> =
        if (font == FontId.Anton && text.any { Character.UnicodeBlock.of(it) == Character.UnicodeBlock.CYRILLIC }) {
            FontId.Oswald to 700
        } else {
            font to null
        }
}

private fun FontId.isSingleWeight() = this == FontId.Anton || this == FontId.Shrikhand

fun ThemeTextStyle.toTextStyle(colors: ContentColors, density: Density, text: String, align: TextAlign?): TextStyle {
    val (font, weightOverride) = ThemeFonts.forText(font, text)
    val weight = weightOverride ?: if (font.isSingleWeight()) 400 else this.weight
    return TextStyle(
        color = color.resolve(colors).let { it.copy(alpha = it.alpha * opacity) },
        fontFamily = ThemeFonts.family(font),
        fontWeight = FontWeight(weight),
        fontSynthesis = FontSynthesis.None,
        fontSize = size.sp,
        lineHeight = if (lineHeight > 0) lineHeight.sp else androidx.compose.ui.unit.TextUnit.Unspecified,
        letterSpacing = letterSpacing.em,
        // Negative skewX leans right (like italics).
        textGeometricTransform = if (skewX != 0f) {
            TextGeometricTransform(skewX = tan(Math.toRadians(skewX.toDouble())).toFloat())
        } else null,
        shadow = shadow?.let {
            with(density) { Shadow(it.color.resolve(colors), Offset(it.dx.dp.toPx(), it.dy.dp.toPx()), it.blur.dp.toPx()) }
        },
        textAlign = align ?: TextAlign.Unspecified,
    )
}

/**
 * Text in a theme text style. Colors "content"/"contentAccent" come from the surface it sits on.
 * [autoSizeMin] enables shrinking (single line) down to that size if the text doesn't fit.
 */
@Composable
fun ThemedText(
    text: String,
    style: TextId,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    align: TextAlign? = null,
    autoSizeMin: Float? = null,
    /** Overrides the style's color (e.g. content color of the surface it sits on). */
    color: Color? = null,
) {
    val spec = LocalQuizTheme.current.text(style).let { if (color != null) it.copy(color = ColorSpec.Fixed(color)) else it }
    val colors = LocalContentColors.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    val shown = if (spec.uppercase) text.uppercase() else text
    val base = spec.toTextStyle(colors, density, shown, align)
    val overflow = if (maxLines == Int.MAX_VALUE) TextOverflow.Clip else TextOverflow.Ellipsis
    val autoSize = autoSizeMin?.let { TextAutoSize.StepBased(it.sp, spec.size.sp, 1.sp) }
    val softWrap = autoSizeMin == null

    if (spec.stroke == null) {
        BasicText(shown, modifier, base, maxLines = maxLines, overflow = overflow, softWrap = softWrap, autoSize = autoSize)
    } else {
        // Outlined text: optional fill underneath, then the stroke (with the shadow as glow).
        Box(modifier) {
            if (base.color.alpha > 0f) {
                BasicText(shown, style = base.copy(shadow = null), maxLines = maxLines, overflow = overflow, softWrap = softWrap)
            }
            BasicText(
                shown,
                style = base.copy(
                    color = spec.stroke.color.resolve(colors),
                    drawStyle = Stroke(width = with(density) { spec.stroke.width.dp.toPx() }),
                ),
                maxLines = maxLines, overflow = overflow, softWrap = softWrap,
            )
        }
    }
}

// ---- Icons ----

@Composable
fun ThemedIcon(res: Int, size: Dp, modifier: Modifier = Modifier, tint: Color = LocalContentColors.current.content) {
    Image(
        painter = painterResource(res),
        contentDescription = null,
        colorFilter = ColorFilter.tint(tint),
        modifier = modifier.size(size),
    )
}

fun roundRectPath(left: Float, top: Float, right: Float, bottom: Float, radius: Float) = Path().apply {
    addRoundRect(RoundRect(left, top, right, bottom, CornerRadius(radius)))
}
