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
import androidx.compose.ui.unit.dp
import com.quizzer.app.R
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
