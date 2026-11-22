package com.quizzer.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.quizzer.app.R
import com.quizzer.app.appicon.AppIcon
import com.quizzer.app.theme.ColorId
import com.quizzer.app.theme.LocalContentColors
import com.quizzer.app.theme.LocalQuizTheme
import com.quizzer.app.theme.ShapeId
import com.quizzer.app.theme.SurfaceId
import com.quizzer.app.theme.TextId
import com.quizzer.app.theme.ThemeEntry
import com.quizzer.app.theme.ThemedBox
import com.quizzer.app.theme.ThemedIcon
import com.quizzer.app.theme.ThemedText
import com.quizzer.app.theme.drawThemePreview
import com.quizzer.app.theme.toShape

@Composable
fun ThemePickerScreen(
    themes: List<ThemeEntry>,
    currentId: String?,
    appIcon: AppIcon,
    onSelectAppIcon: (AppIcon) -> Unit,
    onOpenLicenses: () -> Unit,
    onSelect: (ThemeEntry) -> Unit,
    onDelete: (ThemeEntry) -> Unit,
    onImport: () -> Unit,
    onBack: () -> Unit,
) {
    var themeToDelete by remember { mutableStateOf<ThemeEntry?>(null) }

    ThemedScreen {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarPadding(),
        ) {
            ThemedTopBar("Back", R.drawable.ic_arrow_left, 16.dp, onBack)
            ThemedText("Themes", TextId.PageTitle, Modifier.padding(start = 22.dp, top = 4.dp, end = 22.dp, bottom = 16.dp))
            LazyColumn(
                Modifier.navigationBarPadding(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(themes, key = { it.theme.id }) { entry ->
                    ThemeRow(
                        entry = entry,
                        isCurrent = entry.theme.id == currentId,
                        onClick = { onSelect(entry) },
                        onDelete = { themeToDelete = entry },
                    )
                }
                item { ImportRow("Import theme", onImport) }
                item { AppIconSection(appIcon, onSelectAppIcon) }
                item { ImportRow("Licenses", onOpenLicenses, symbol = "§") }
            }
        }

        ThemedConfirmDialog(
            visible = themeToDelete != null,
            title = "Delete theme?",
            body = "\"${themeToDelete?.theme?.name.orEmpty()}\" will be removed from the app. You can import it again later.",
            confirmLabel = "Delete",
            onConfirm = {
                themeToDelete?.let(onDelete)
                themeToDelete = null
            },
            onDismiss = { themeToDelete = null },
        )
    }
}

@Composable
private fun ThemeRow(entry: ThemeEntry, isCurrent: Boolean, onClick: () -> Unit, onDelete: () -> Unit) {
    val active = LocalQuizTheme.current
    val previewShape = active.shape(ShapeId.Preview).toShape()
    PressableRow(onClick) {
        Row(
            Modifier
                .weight(1f)
                .padding(start = 14.dp, top = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Preview from the listed theme's own tokens, framed in the active theme's preview shape.
            Canvas(
                Modifier
                    .size(52.dp, 90.dp)
                    .clip(previewShape)
                    .border(1.dp, active.color(ColorId.TextMuted), previewShape),
            ) { drawThemePreview(entry.theme) }

            Column(Modifier.weight(1f)) {
                ThemedText(entry.theme.name, TextId.TopicTitle, maxLines = 2)
                ThemedText(
                    if (entry.builtIn) "Built-in" else "Imported · ${entry.sourceFile.orEmpty()}",
                    TextId.TopicDescription,
                    maxLines = 2,
                )
                if (isCurrent) {
                    Spacer(Modifier.size(4.dp))
                    ThemedBox(SurfaceId.ChipCurrent, ShapeId.Chip) {
                        ThemedText("Current", TextId.Chip, Modifier.padding(horizontal = 10.dp, vertical = 2.dp))
                    }
                }
            }
        }
        Box(
            Modifier
                .padding(end = 10.dp)
                .size(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            when {
                isCurrent -> ThemedIcon(R.drawable.ic_check, 22.dp, tint = LocalContentColors.current.accent)
                !entry.builtIn -> IconAction(R.drawable.ic_trash, 20.dp, onDelete, alpha = 0.75f)
            }
        }
    }
}

/** "App icon" section: the four launcher icons as tappable previews, the current one marked. */
@Composable
private fun AppIconSection(current: AppIcon, onSelect: (AppIcon) -> Unit) {
    val theme = LocalQuizTheme.current
    val accent = theme.color(ColorId.Accent)
    Column(Modifier.padding(top = 8.dp)) {
        SectionRow("App icon")
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            AppIcon.entries.forEach { icon ->
                val selected = icon == current
                Column(
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelect(icon) }
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    AdaptiveIconPreview(
                        icon,
                        Modifier.border(if (selected) 3.dp else 0.dp, if (selected) accent else Color.Transparent, IconShape),
                    )
                    ThemedText(
                        icon.label, TextId.TopicMeta, maxLines = 1,
                        color = if (selected) accent else null,
                    )
                }
            }
        }
        ThemedText(
            "The new icon appears after you leave Quizzer. Re-add home screen shortcuts if needed.",
            TextId.TopicDescription,
            Modifier.padding(start = 6.dp, top = 10.dp, end = 6.dp),
        )
    }
}

private val IconShape = RoundedCornerShape(30)

/**
 * Draws an adaptive icon like a launcher does: both 108 dp layers, cropped to the visible
 * central part (72 dp of 108 dp) and masked with a rounded shape.
 */
@Composable
private fun AdaptiveIconPreview(icon: AppIcon, modifier: Modifier = Modifier, size: androidx.compose.ui.unit.Dp = 60.dp) {
    Box(
        modifier
            .size(size)
            .clip(IconShape),
        contentAlignment = Alignment.Center,
    ) {
        val layerSize = size * 1.5f
        Image(painterResource(icon.background), null, Modifier.requiredSize(layerSize))
        Image(painterResource(icon.foreground), null, Modifier.requiredSize(layerSize))
    }
}
