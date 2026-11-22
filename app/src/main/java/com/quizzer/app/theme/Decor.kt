package com.quizzer.app.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/** Fills the element with the theme's screen background. */
fun Modifier.themeBackground(theme: QuizTheme, colors: ContentColors): Modifier =
    drawBehind { drawRect(theme.background.toBrush(size, colors)) }

/** Draws [decor] (if any) behind the element. */
fun Modifier.decor(decor: ThemeDecor?, colors: ContentColors): Modifier =
    if (decor == null) this else drawBehind { drawDecor(decor, size, colors) }

fun DrawScope.drawDecor(decor: ThemeDecor, area: Size, colors: ContentColors) {
    clipRect(0f, 0f, area.width, area.height) {
        when (decor) {
            is ThemeDecor.Sunset -> drawSunset(decor, area, colors)
            is ThemeDecor.Glow -> drawGlow(decor, area, colors)
            is ThemeDecor.Bands -> drawBands(decor, area)
        }
    }
}

private fun DrawScope.drawSunset(d: ThemeDecor.Sunset, area: Size, colors: ContentColors) {
    drawRect(d.sky.toBrush(area, colors))

    val diameter = d.sunDiameter.dp.toPx()
    val topLeft = Offset(area.width * d.sunCenterX - diameter / 2, d.sunCenterY.dp.toPx() - diameter / 2)
    translate(topLeft.x, topLeft.y) {
        drawOval(d.sun.toBrush(Size(diameter, diameter), colors), size = Size(diameter, diameter), alpha = d.sunOpacity)
    }

    d.stripes?.let { s ->
        val h = s.height.dp.toPx()
        val step = h + s.gap.dp.toPx()
        val bottom = area.height - s.bottom.dp.toPx()
        repeat(s.count) { i -> drawRect(s.color, Offset(0f, bottom - h - i * step), Size(area.width, h)) }
    }

    d.fade?.let { f ->
        val h = f.height.dp.toPx()
        // Transparent at the top of the fade, solid color from fraction `from` (0 = only at the very end).
        val solidFrom = if (f.from <= 0f) 1f else f.from
        drawRect(
            Brush.verticalGradient(
                0f to f.color.copy(alpha = 0f), solidFrom to f.color, 1f to f.color,
                startY = area.height - h, endY = area.height,
            ),
            topLeft = Offset(0f, area.height - h),
            size = Size(area.width, h),
        )
    }
}

private fun DrawScope.drawGlow(d: ThemeDecor.Glow, area: Size, colors: ContentColors) {
    d.base?.let { drawRect(it.toBrush(area, colors)) }
    val center = Offset(area.width * d.centerX, area.height * d.centerY)
    drawRect(Brush.radialGradient(listOf(d.color, d.color.copy(alpha = 0f)), center, d.radius.dp.toPx()))

    d.rain?.let { r ->
        // 1 dp lines, perpendicular to the CSS gradient direction `angle`.
        val rad = Math.toRadians(r.angle.toDouble())
        val dir = Offset(sin(rad).toFloat(), -cos(rad).toFloat())
        val along = Offset(-dir.y, dir.x)
        val diag = hypot(area.width, area.height)
        val spacing = r.spacing.dp.toPx()
        val mid = Offset(area.width / 2, area.height / 2)
        val n = (diag / spacing).toInt() + 1
        for (k in -n..n) {
            val p = mid + dir * (k * spacing)
            drawLine(r.color, p - along * diag, p + along * diag, strokeWidth = 1.dp.toPx())
        }
    }
}

private fun DrawScope.drawBands(d: ThemeDecor.Bands, area: Size) {
    val left = d.inset.left.dp.toPx()
    val top = d.inset.top.dp.toPx()
    val right = area.width - d.inset.right.dp.toPx()
    val bottom = d.height?.let { top + it.dp.toPx() } ?: (area.height - d.inset.bottom.dp.toPx())
    val card = Rect(left, top, right, bottom)
    val path = roundRectPath(left, top, right, bottom, d.radius.dp.toPx())

    (d.effect as? ThemeEffect.HardShadow)?.let { s ->
        translate(s.dx.dp.toPx(), s.dy.dp.toPx()) { drawPath(path, s.color) }
    }
    clipPath(path) {
        var y = card.top
        d.colors.forEachIndexed { i, color ->
            val h = card.height * d.weights[i]
            drawRect(color, Offset(card.left, y), Size(card.width, h + 1f))
            y += h
        }
        d.circle?.let { c ->
            drawCircle(
                c.color, radius = c.diameter.dp.toPx() / 2,
                center = Offset(card.left + card.width * c.centerX, card.top + card.height * c.centerY),
            )
        }
    }
    d.border?.let { drawBorder(path, card.size, it, ContentColors(Color.Black, Color.Black)) }
}

/**
 * Small preview of [theme] for the theme picker, drawn from *its* tokens (52 × 90 dp design).
 */
fun DrawScope.drawThemePreview(theme: QuizTheme) {
    val colors = ContentColors(theme.color(ColorId.TextPrimary), theme.color(ColorId.Accent))
    val u = size.width / 52f // design units → px
    drawRect(theme.background.toBrush(size, colors))

    // Simplified list header.
    val header = Size(size.width, 30 * u)
    when (val d = theme.decor(DecorId.ListHeader)) {
        is ThemeDecor.Sunset -> drawRect(d.sky.toBrush(header, colors), size = header)
        is ThemeDecor.Glow -> {
            d.base?.let { drawRect(it.toBrush(header, colors), size = header) }
            drawRect(
                Brush.radialGradient(listOf(d.color, d.color.copy(alpha = 0f)), Offset(header.width * d.centerX, header.height * d.centerY), 40 * u),
                size = header,
            )
        }
        is ThemeDecor.Bands -> {
            var y = 0f
            d.colors.forEachIndexed { i, c ->
                val h = header.height * d.weights[i]
                drawRect(c, Offset(0f, y), Size(header.width, h + 1f)); y += h
            }
        }
        null -> Unit
    }

    // Logo bar in the logo color (stroke color for outlined logos).
    val logo = theme.text(TextId.Logo)
    val logoColor = logo.stroke?.color?.resolve(colors) ?: logo.color.resolve(colors)
    drawRoundRect(logoColor, Offset(6 * u, 18 * u), Size(26 * u, 6 * u), androidx.compose.ui.geometry.CornerRadius(1.5f * u))

    // Answer bars: idle, selected, idle.
    listOf(SurfaceId.AnswerIdle, SurfaceId.AnswerSelected, SurfaceId.AnswerIdle).forEachIndexed { i, sid ->
        val s = theme.surface(sid)
        val barSize = Size(size.width - 12 * u, 8 * u)
        translate(6 * u, (38 + i * 12) * u) {
            val path = roundRectPath(0f, 0f, barSize.width, barSize.height, 3 * u)
            val c = s.contentColors(colors)
            drawPath(path, s.fill.toBrush(barSize, c))
            s.border?.let { b ->
                drawPath(path, b.color.resolve(c), style = Stroke(1 * u))
            }
        }
    }

    // Primary button bar.
    val button = theme.surface(SurfaceId.ButtonPrimary)
    val barSize = Size(size.width - 12 * u, 10 * u)
    translate(6 * u, size.height - 16 * u) {
        drawPath(roundRectPath(0f, 0f, barSize.width, barSize.height, 3 * u), button.fill.toBrush(barSize, button.contentColors(colors)))
    }
}

/** Two quarter arcs (accent → highlight) rotating on the progress track. */
@Composable
fun ThemedSpinner(modifier: Modifier = Modifier, size: Dp = 32.dp) {
    val theme = LocalQuizTheme.current
    val colors = LocalContentColors.current
    val rotation by rememberInfiniteTransition(label = "spinner").animateFloat(
        0f, 360f, infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Restart), label = "rotation",
    )
    val track = theme.surface(SurfaceId.ProgressTrack).fill.firstColor(colors)
    val accent = theme.color(ColorId.Accent)
    val highlight = theme.color(ColorId.Highlight)
    Canvas(modifier.size(size)) {
        val stroke = 3.dp.toPx()
        val inset = stroke / 2
        val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
        drawCircle(track, radius = arcSize.width / 2, style = Stroke(stroke))
        drawArc(accent, rotation - 90f, 90f, false, Offset(inset, inset), arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
        drawArc(highlight, rotation, 90f, false, Offset(inset, inset), arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
    }
}

/**
 * Pie chart: slices clockwise from 12 o'clock in the order given, zero slices skipped,
 * 2 dp separators in [gap]. [progress] (0..1) animates the sweep.
 */
fun DrawScope.drawPie(values: List<Pair<Int, Color>>, gap: Color, progress: Float) {
    val total = values.sumOf { it.first }.takeIf { it > 0 } ?: return
    val gapWidth = 2.dp.toPx()
    val radius = size.minDimension / 2
    val center = Offset(size.width / 2, size.height / 2)
    val maxSweep = 360f * progress
    var start = -90f
    val boundaries = mutableListOf<Float>()
    values.filter { it.first > 0 }.forEach { (value, color) ->
        val sweep = 360f * value / total
        val visible = (maxSweep - (start + 90f)).coerceIn(0f, sweep)
        if (visible > 0f) drawArc(color, start, visible, useCenter = true)
        boundaries += start
        start += sweep
    }
    if (boundaries.size > 1) {
        boundaries.forEach { angle ->
            if (angle + 90f <= maxSweep) {
                val rad = Math.toRadians(angle.toDouble())
                drawLine(gap, center, center + Offset(cos(rad).toFloat(), sin(rad).toFloat()) * radius, gapWidth)
            }
        }
    }
    drawCircle(gap, radius - gapWidth / 2, center, style = Stroke(gapWidth))
}
