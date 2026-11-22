package com.quizzer.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.quizzer.app.R
import com.quizzer.app.theme.ShapeId
import com.quizzer.app.theme.SurfaceId
import com.quizzer.app.theme.TextId
import com.quizzer.app.theme.ThemedBox
import com.quizzer.app.theme.ThemedText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** One open-source component and the license text shipped in assets/licenses. */
private data class LicenseEntry(
    val name: String,
    val kind: String,
    val license: String,
    val copyright: String,
    val asset: String,
)

private val LICENSES = listOf(
    LicenseEntry(
        "Quizzer", "This app", "GNU General Public License v3.0",
        "Source code and license: see the project repository", "quizzer-GPL-3.0.txt",
    ),
    LicenseEntry("Anton", "Font", "SIL Open Font License 1.1", "Copyright 2020 The Anton Project Authors", "font-anton-OFL.txt"),
    LicenseEntry("Inter", "Font", "SIL Open Font License 1.1", "Copyright 2020 The Inter Project Authors", "font-inter-OFL.txt"),
    LicenseEntry("Oswald", "Font", "SIL Open Font License 1.1", "Copyright 2016 The Oswald Project Authors", "font-oswald-OFL.txt"),
    LicenseEntry("Shrikhand", "Font", "SIL Open Font License 1.1", "Copyright (c) 2015 Jonny Pinhorn", "font-shrikhand-OFL.txt"),
    LicenseEntry("Phosphor Icons", "Icons", "MIT License", "Copyright (c) 2023 Phosphor Icons", "icons-phosphor-MIT.txt"),
    LicenseEntry(
        "AndroidX, Jetpack Compose, Kotlin, kotlinx.coroutines", "Libraries", "Apache License 2.0",
        "Copyright The Android Open Source Project, JetBrains s.r.o. and contributors", "libraries-Apache-2.0.txt",
    ),
)

@Composable
fun LicensesScreen(onBack: () -> Unit) {
    var expanded by rememberSaveable { mutableStateOf<String?>(null) }

    ThemedScreen {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarPadding(),
        ) {
            ThemedTopBar("Back", R.drawable.ic_arrow_left, 16.dp, onBack)
            ThemedText("Licenses", TextId.PageTitle, Modifier.padding(start = 22.dp, top = 4.dp, end = 22.dp, bottom = 8.dp))
            LazyColumn(
                Modifier.navigationBarPadding(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    ThemedText(
                        "Quizzer is free software. It uses the following open-source components. Tap an entry to read its license.",
                        TextId.Body,
                        Modifier.padding(start = 6.dp, end = 6.dp, bottom = 6.dp),
                    )
                }
                items(LICENSES, key = { it.asset }) { entry ->
                    LicenseRow(
                        entry = entry,
                        isExpanded = expanded == entry.asset,
                        onClick = { expanded = if (expanded == entry.asset) null else entry.asset },
                    )
                }
            }
        }
    }
}

@Composable
private fun LicenseRow(entry: LicenseEntry, isExpanded: Boolean, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PressableRow(onClick) {
            Row(
                Modifier
                    .weight(1f)
                    .padding(start = 18.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    ThemedText(entry.name, TextId.TopicTitle, maxLines = 2)
                    ThemedText(entry.copyright, TextId.TopicDescription, maxLines = 2)
                    ThemedText("${entry.kind} · ${entry.license}", TextId.TopicStats, maxLines = 1)
                }
                ThemedText(if (isExpanded) "–" else "+", TextId.ImportAction, Modifier.padding(start = 12.dp))
            }
        }
        AnimatedVisibility(isExpanded) {
            ThemedBox(SurfaceId.LegendPanel, ShapeId.Card, Modifier.fillMaxWidth()) {
                ThemedText(licenseText(entry.asset), TextId.Explanation, Modifier.padding(16.dp))
            }
        }
    }
}

/** Reads the license text from assets/licenses (off the main thread). */
@Composable
private fun licenseText(asset: String): String {
    val context = LocalContext.current
    val text by produceState("Loading…", asset) {
        value = withContext(Dispatchers.IO) {
            runCatching { context.assets.open("licenses/$asset").bufferedReader().use { it.readText() } }
                .getOrElse { "License text not found ($asset)." }
        }
    }
    return text
}
