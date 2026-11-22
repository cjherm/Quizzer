# Quizzer – Neon Night theme handoff

Theme id `neon-night` · imported theme (deletable) · status bar icons: **light**.
Target: Samsung Galaxy S22, 360 × 780 dp (1080 × 2340 px). All sizes dp, text sizes sp.

This package contains:
| File | Content |
|---|---|
| `handoff.md` | This file: every value needed to implement the theme, plus the shared layout |
| `theme-neon-night.json` | The same values, machine-readable (schema v1), to ship as an asset |
| `screens/<state>.png` | 22 states, 1080 × 2340 px |

The layout is the same for every theme (section B). This theme changes only the values in section A.

---
# A. Theme values

## A1. Colors
| Token | Hex (ARGB) | Opacity | Compose | Used for |
|---|---|---|---|---|
| `textPrimary` | `#F4F0FF` | 100 % | `Color(0xFFF4F0FF)` | Main text, icons on background |
| `textSecondary` | `#B9CBD6` | 100 % | `Color(0xFFB9CBD6)` | Descriptions, explanations, dialog body, legend labels |
| `textMuted` | `#8FAFC0` | 100 % | `Color(0xFF8FAFC0)` | Counter total, captions, preview border |
| `accent` | `#00EAFF` | 100 % | `Color(0xFF00EAFF)` | Section label, counter, percent, stats, spinner arc |
| `highlight` | `#FF7FD4` | 100 % | `Color(0xFFFF7FD4)` | Hint text, second spinner arc |
| `onFill` | `#08070F` | 100 % | `Color(0xFF08070F)` | Text/icons on bright fills |
| `displayShadow` | `#FF2FB3` | 100 % | `Color(0xFFFF2FB3)` | Shadow of display text |
| `scrim` | `#BF05040A` | 75 % | `Color(0xBF05040A)` | Dialog backdrop |
| `chartCorrect` | `#39FFB0` | 100 % | `Color(0xFF39FFB0)` | Pie: correct |
| `chartPartial` | `#FFC65C` | 100 % | `Color(0xFFFFC65C)` | Pie: partially correct |
| `chartWrong` | `#FF4D6A` | 100 % | `Color(0xFFFF4D6A)` | Pie: wrong |
| `chartGap` | `#0C0A18` | 100 % | `Color(0xFF0C0A18)` | Pie slice separators (2 dp) |

## A2. Background
Screen background: linear 180°: `#08070F` @ 0 %, `#110A1F` @ 100 %

Screen overlay (all screens, above background, below content): **glow**, see A6.

Angle convention: 0° = bottom → top, 90° = left → right, 180° = top → bottom (CSS convention). In Compose use `Brush.linearGradient(colorStops, start, end)` along that direction through the element's center.

## A3. Surfaces
"content" = color of text and icons placed on the surface.

| Surface | Fill | Content | Accent content | Border | Effect | Used by |
|---|---|---|---|---|---|---|
| `topicRow` | `#00000000` | `textPrimary` = `#F4F0FF` | `highlight` = `#FF7FD4` | 1.5 dp solid `#8C00EAFF` | glow `#4000EAFF`, radius 10 dp | Topic rows, theme rows |
| `topicRowPressed` | `#26FF2FB3` | `#FFE6F6` | `#FFE6F6` | 1.5 dp solid `#FF2FB3` | glow `#8CFF2FB3`, radius 14 dp | Pressed topic/theme row |
| `questionCard` | `#00000000` | `textPrimary` = `#F4F0FF` | – | – | – | Box around question text |
| `answerIdle` | `#00000000` | `#D8FBFF` | – | 1.5 dp solid `#6600EAFF` | – | Answer, not selected |
| `answerSelected` | `#1FFF2FB3` | `#FFD6F0` | – | 1.5 dp solid `#FF2FB3` | glow `#8CFF2FB3`, radius 14 dp | Answer, selected |
| `answerCorrect` | `#1A39FFB0` | `#C9FFE9` | – | 1.5 dp solid `#39FFB0` | glow `#8039FFB0`, radius 14 dp | After check: selected and correct |
| `answerMissed` | `#00000000` | `#39FFB0` | – | 1.5 dp solid `#8039FFB0` | – | After check: correct, not selected |
| `answerWrong` | `#1AFF4D4D` | `#FFD0D0` | – | 1.5 dp solid `#FF4D4D` | glow `#80FF4D4D`, radius 14 dp | After check: selected and wrong |
| `answerDimmed` | `#00000000` | `#9AB0BC` | – | 1.5 dp solid `#2600EAFF` | – | After check: not selected, not correct |
| `checkbox` | `#00000000` | `content` | – | 1.5 dp solid `content` | – | Empty checkbox |
| `checkboxChecked` | `#FF2FB3` | `#08070F` | – | 1.5 dp solid `#FF2FB3` | – | Checked checkbox |
| `feedbackCorrect` | `#1A39FFB0` | `#C9FFE9` | – | 1.5 dp solid `#39FFB0` | glow `#8039FFB0`, radius 14 dp | Feedback banner: correct |
| `feedbackPartial` | `#1AFFC65C` | `#FFE7B0` | – | 1.5 dp solid `#FFC65C` | glow `#80FFC65C`, radius 14 dp | Feedback banner: partially correct |
| `feedbackWrong` | `#1AFF4D4D` | `#FFD0D0` | – | 1.5 dp solid `#FF4D4D` | glow `#80FF4D4D`, radius 14 dp | Feedback banner: wrong |
| `buttonPrimary` | `#1AFF2FB3` | `#FFE6F6` | – | 1.5 dp solid `#FF2FB3` | glow `#8CFF2FB3`, radius 16 dp | Submit, Try again, dialog Delete |
| `buttonPrimaryPressed` | `#40FF2FB3` | `#FFE6F6` | – | 1.5 dp solid `#FF2FB3` | glow `#B3FF2FB3`, radius 20 dp | Primary button pressed |
| `buttonDisabled` | `#00000000` | `#66F4F0FF` | – | 1.5 dp solid `#33F4F0FF` | – | Primary button disabled |
| `buttonSecondary` | `#00000000` | `#D8FBFF` | – | 1.5 dp solid `#9900EAFF` | glow `#4D00EAFF`, radius 10 dp | Back to topics |
| `progressTrack` | `#1F00EAFF` | `textPrimary` = `#F4F0FF` | – | – | – | Progress track, spinner track |
| `progressFill` | `#00EAFF` | `onFill` = `#08070F` | – | – | glow `#B300EAFF`, radius 10 dp | Progress fill |
| `dialog` | `#0C0A18` | `textPrimary` = `#F4F0FF` | – | 1.5 dp solid `#FF2FB3` | glow `#73FF2FB3`, radius 24 dp | Dialog card |
| `snackbar` | `#0C0A18` | `#D8FBFF` | – | 1.5 dp solid `#00EAFF` | glow `#7300EAFF`, radius 14 dp | Snackbar |
| `legendPanel` | `#CC0C0A18` | `textPrimary` = `#F4F0FF` | – | 1.5 dp solid `#4000EAFF` | – | Result chart panel |
| `chipWin` | `#1A39FFB0` | `#C9FFE9` | – | 1.5 dp solid `#39FFB0` | glow `#8039FFB0`, radius 12 dp | Chip "Win" |
| `chipLose` | `#1AFF4D4D` | `#FFD0D0` | – | 1.5 dp solid `#FF4D4D` | glow `#80FF4D4D`, radius 12 dp | Chip "Lose" |
| `chipCurrent` | `#1AFF2FB3` | `#FFD6F0` | – | 1.5 dp solid `#FF2FB3` | glow `#80FF2FB3`, radius 10 dp | Chip "Current" |

## A4. Shapes
| Shape | Value | Applies to |
|---|---|---|
| `topicRow` | rounded 16 dp | Topic and theme rows |
| `answer` | rounded 26 dp | Answer rows |
| `feedback` | rounded 14 dp | Feedback banner |
| `button` | rounded 28 dp | Primary and secondary buttons |
| `chip` | rounded 14 dp | Chips, dialog Delete button |
| `progress` | rounded 2 dp | Progress track and fill |
| `card` | rounded 16 dp | Question card, snackbar, chart panel |
| `dialog` | rounded 20 dp | Dialog card |
| `checkbox` | rounded 9 dp | Checkbox, legend swatches |
| `preview` | rounded 8 dp | Theme preview in picker |

Slanted = parallelogram: top-left and bottom-right corners inset horizontally by the slant (`GenericShape`: (s,0) → (w,0) → (w−s,h) → (0,h)).

## A5. Typography
Color "content" = content color of the surface the text sits on. Skew: negative leans right; Compose `TextGeometricTransform(skewX = -tan(deg))` (−9° → 0.158).

| Style | Font | Weight | Size sp | Line height sp | Letter spacing | Upper | Skew | Color | Opacity | Shadow | Stroke |
|---|---|---|---|---|---|---|---|---|---|---|---|
| `logo` | Anton | 400 | 64 | 64 | 0.04 em | yes | – | `#00000000` | 1 | `#FF2FB3` 0/0 dp, blur 18 | 1.6 dp `#FF7FD4` |
| `sectionLabel` | Anton | 400 | 15 | 20 | 0.18 em | yes | – | `accent` = `#00EAFF` | 1 | `#B300EAFF` 0/0 dp, blur 8 | – |
| `topicIndex` | Anton | 400 | 14 | 20 | 0 | no | – | `content` | 0.7 | – | – |
| `topicTitle` | Anton | 400 | 22 | 26 | 0.02 em | yes | – | `content` | 1 | `#9900EAFF` 0/0 dp, blur 8 | – |
| `topicDescription` | Inter | 400 | 12 | 16 | 0 | no | – | `content` | 0.8 | – | – |
| `topicMeta` | Inter | 600 | 12 | 16 | 0 | no | – | `content` | 1 | – | – |
| `topicStats` | Inter | 600 | 12 | 16 | 0 | no | – | `contentAccent` | 1 | – | – |
| `importAction` | Anton | 400 | 22 | 28 | 0.06 em | yes | – | `highlight` = `#FF7FD4` | 1 | `#FF2FB3` 0/0 dp, blur 8 | – |
| `headerSmall` | Inter | 600 | 12 | 16 | 0.06 em | yes | – | `textSecondary` = `#B9CBD6` | 1 | – | – |
| `back` | Anton | 400 | 15 | 20 | 0.1 em | yes | – | `accent` = `#00EAFF` | 1 | `#CC00EAFF` 0/0 dp, blur 8 | – |
| `counter` | Anton | 400 | 52 | 52 | 0 | no | – | `highlight` = `#FF7FD4` | 1 | `#FF2FB3` 0/0 dp, blur 12 | – |
| `counterTotal` | Anton | 400 | 20 | 26 | 0 | no | – | `textMuted` = `#8FAFC0` | 1 | – | – |
| `question` | Inter | 700 | 21 | 27 | 0 | no | – | `content` | 1 | – | – |
| `hint` | Inter | 600 | 12 | 16 | 0.04 em | yes | – | `highlight` = `#FF7FD4` | 1 | – | – |
| `answerLetter` | Anton | 400 | 18 | 24 | 0 | no | – | `content` | 1 | – | – |
| `answer` | Inter | 600 | 15 | 20 | 0 | no | – | `content` | 1 | – | – |
| `feedback` | Anton | 400 | 20 | 24 | 0.03 em | yes | – | `content` | 1 | `content` 0/0 dp, blur 8 | – |
| `explanation` | Inter | 400 | 13 | 20 | 0 | no | – | `textSecondary` = `#B9CBD6` | 1 | – | – |
| `button` | Anton | 400 | 20 | 26 | 0.08 em | yes | – | `content` | 1 | `#FF2FB3` 0/0 dp, blur 8 | – |
| `buttonSecondary` | Anton | 400 | 18 | 24 | 0.08 em | yes | – | `content` | 1 | – | – |
| `chip` | Anton | 400 | 16 | 20 | 0.14 em | yes | – | `content` | 1 | – | – |
| `pageTitle` | Anton | 400 | 40 | 44 | 0.04 em | yes | – | `#D8FBFF` | 1 | `#00EAFF` 0/0 dp, blur 14 | – |
| `headline` | Anton | 400 | 40 | 46 | 0.04 em | yes | – | `#D8FBFF` | 1 | `#00EAFF` 0/0 dp, blur 14 | – |
| `score` | Anton | 400 | 120 | 124 | 0 | no | – | `#FFD6F0` | 1 | `#FF2FB3` 0/0 dp, blur 24 | – |
| `scoreTotal` | Anton | 400 | 40 | 48 | 0 | no | – | `highlight` = `#FF7FD4` | 1 | `#FF2FB3` 0/0 dp, blur 10 | – |
| `percent` | Anton | 400 | 20 | 26 | 0.1 em | yes | – | `accent` = `#00EAFF` | 1 | `#B300EAFF` 0/0 dp, blur 8 | – |
| `legendLabel` | Inter | 600 | 13 | 18 | 0 | no | – | `textSecondary` = `#B9CBD6` | 1 | – | – |
| `legendValue` | Anton | 400 | 20 | 24 | 0 | no | – | `textPrimary` = `#F4F0FF` | 1 | – | – |
| `dialogTitle` | Anton | 400 | 26 | 32 | 0.04 em | yes | – | `#FFD6F0` | 1 | `#FF2FB3` 0/0 dp, blur 10 | – |
| `dialogBody` | Inter | 400 | 14 | 21 | 0 | no | – | `textSecondary` = `#B9CBD6` | 1 | – | – |
| `dialogAction` | Anton | 400 | 16 | 24 | 0.08 em | yes | – | `accent` = `#00EAFF` | 1 | – | – |
| `snackbar` | Inter | 700 | 13 | 18 | 0 | no | – | `content` | 1 | – | – |
| `body` | Inter | 400 | 15 | 22 | 0 | no | – | `textSecondary` = `#B9CBD6` | 1 | – | – |

Where each style is used: see section B (every element names its style).

## A6. Decor
### `listHeader` (Topic list header, 360 × 236): type **glow**
| Param | Value |
|---|---|
| base | – |
| color | `#66FF2FB3` |
| centerX | 0.25 |
| centerY | 0.55 |
| radius | 200 dp |
| rain | – |

### `resultWin` (Result background (win), 360 × 780): type **glow**
| Param | Value |
|---|---|
| base | – |
| color | `#4D39FFB0` |
| centerX | 0.3 |
| centerY | 0.2 |
| radius | 320 dp |
| rain | 1 dp lines, `#0F00EAFF`, 100°, every 15 dp |

### `resultLose` (Result background (lose), 360 × 780): type **glow**
| Param | Value |
|---|---|
| base | – |
| color | `#40FF4D4D` |
| centerX | 0.3 |
| centerY | 0.2 |
| radius | 300 dp |
| rain | 1 dp lines, `#0F00EAFF`, 100°, every 15 dp |

### `screen` (Overlay on all screens, 360 × 780): type **glow**
| Param | Value |
|---|---|
| base | – |
| color | `#38FF2FB3` |
| centerX | 0.5 |
| centerY | 1.05 |
| radius | 420 dp |
| rain | 1 dp lines, `#0900EAFF`, 100°, every 15 dp |

Decor style definitions (how to draw sunset / glow / bands): section B5.

## A7. Fonts for this theme
All SIL Open Font License 1.1, free to bundle; download the static TTFs from Google Fonts. Put them in `res/font/` and add the OFL text to the app's licenses.

| Family | Weights used | Source |
|---|---|---|
| Anton | 400 | https://fonts.google.com/specimen/Anton |
| Inter | 400, 600, 700 | https://fonts.google.com/specimen/Inter |
| Oswald (fallback) | 700 | https://fonts.google.com/specimen/Oswald: replaces Anton for Cyrillic text |
| Noto Sans JP (fallback) | 700, 900 | https://fonts.google.com/noto/specimen/Noto+Sans+JP: Japanese for all styles |

## A8. Contrast (WCAG 2.1 AA)
Text colors against their fill composited over every background stop; the worst stop counts. 4.5 : 1 for text, 3 : 1 for large text (≥ 24 sp or ≥ 18.66 sp bold).

| Pair | Ratio | Needs | Result |
|---|---|---|---|
| textPrimary on background | 17.25 : 1 | 4.5 : 1 | pass |
| textSecondary on background | 11.57 : 1 | 4.5 : 1 | pass |
| textMuted on background | 8.34 : 1 | 4.5 : 1 | pass |
| accent on background | 13.08 : 1 | 4.5 : 1 | pass |
| highlight on background | 8.50 : 1 | 4.5 : 1 | pass |
| topicRow: content on fill | 17.25 : 1 | 4.5 : 1 | pass |
| topicRowPressed: content on fill | 14.08 : 1 | 4.5 : 1 | pass |
| questionCard: content on fill | 17.25 : 1 | 4.5 : 1 | pass |
| answerIdle: content on fill | 17.63 : 1 | 4.5 : 1 | pass |
| answerSelected: content on fill | 13.16 : 1 | 4.5 : 1 | pass |
| answerCorrect: content on fill | 14.46 : 1 | 4.5 : 1 | pass |
| answerMissed: content on fill | 14.82 : 1 | 4.5 : 1 | pass |
| answerWrong: content on fill | 12.66 : 1 | 4.5 : 1 | pass |
| answerDimmed: content on fill | 8.56 : 1 | 4.5 : 1 | pass |
| feedbackCorrect: content on fill | 14.46 : 1 | 4.5 : 1 | pass |
| feedbackPartial: content on fill | 13.32 : 1 | 4.5 : 1 | pass |
| feedbackWrong: content on fill | 12.66 : 1 | 4.5 : 1 | pass |
| buttonPrimary: content on fill | 14.96 : 1 | 4.5 : 1 | pass |
| buttonPrimaryPressed: content on fill | 12.00 : 1 | 4.5 : 1 | pass |
| buttonSecondary: content on fill | 17.63 : 1 | 4.5 : 1 | pass |
| dialog: content on fill | 17.49 : 1 | 4.5 : 1 | pass |
| snackbar: content on fill | 17.87 : 1 | 4.5 : 1 | pass |
| legendPanel: content on fill | 17.45 : 1 | 4.5 : 1 | pass |
| chipWin: content on fill | 14.46 : 1 | 4.5 : 1 | pass |
| chipLose: content on fill | 12.66 : 1 | 4.5 : 1 | pass |
| chipCurrent: content on fill | 13.48 : 1 | 4.5 : 1 | pass |
| topicRow: description | 11.10 : 1 | 4.5 : 1 | pass |
| topicRow: stats (contentAccent) | 8.50 : 1 | 4.5 : 1 | pass |
| topicRowPressed: stats | 14.08 : 1 | 4.5 : 1 | pass |
| dialog: body (textSecondary) | 11.73 : 1 | 4.5 : 1 | pass |
| legend label on legendPanel | 11.70 : 1 | 4.5 : 1 | pass |
| buttonDisabled (exempt: inactive) | 3.46 : 1 | exempt | n/a |
| resultWin: headline over decor (large) | 7.92 : 1 | 3 : 1 | pass |
| resultWin: score over decor (large) | 6.66 : 1 | 3 : 1 | pass |
| resultWin: percent over decor | 5.87 : 1 | 4.5 : 1 | pass |
| resultLose: headline over decor (large) | 12.75 : 1 | 3 : 1 | pass |
| resultLose: score over decor (large) | 10.73 : 1 | 3 : 1 | pass |
| resultLose: percent over decor | 9.46 : 1 | 4.5 : 1 | pass |

Exceptions: disabled button text (inactive control, exempt), the logo (logotype, exempt), decor (no information). Idle answer border (40 % cyan) is below 3 : 1 non-text contrast; the row is also identified by its text and letter.

## A9. What this theme lost in the shared layout
**Neon Night (3a)**: list rows lost their card-style spacing (now 10 dp like all themes). The neon underline under the logo was dropped (only the glow decor remains). The result screen gained the chart panel and the chip. Otherwise nothing important was lost.

---
# B. Shared layout (identical for all themes)

## B1. Screens

Every theme uses exactly this structure and these positions. Themes change colors, gradients, fonts, text styles, shapes, effects and decor only. Insets: status bar 24 dp top and gesture bar 20 dp bottom are drawn by the system (edge-to-edge); use `WindowInsets`, don't hard-code.

### Topic list (L1–L6)
| Element | Spec |
|---|---|
| Header | Full width, height 236 (includes status bar area). Background = `decor.listHeader`. |
| Logo "Quizzer" | Text style `logo`. Anchored left 24, bottom 16 of header. Single line. |
| Theme button | Icon button 44 × 44, top 28, right 12, icon Palette 24, color `textPrimary`. Opens the theme picker. |
| Section row | Padding 16 / 22 / 6 / 22 (top, right, bottom, left). Label "Topics" (`sectionLabel`), gap 10, then a 2 dp line filling the rest: horizontal gradient from the label color to transparent. |
| List | Padding 6 / 16 / 0 / 16, items gap 10, scrolls under the header. |
| Topic row | Surface `topicRow` (pressed: `topicRowPressed`), shape `topicRow`. Padding 12 / 12 / 12 / 18. Row: index (width 20, top-aligned, padding-top 3, `topicIndex`), gap 12, text column (gap 3): title `topicTitle` max 2 lines + ellipsis; description `topicDescription` 1 line + ellipsis; meta row (gap 8, single line): "<n> questions" `topicMeta` · 3 dp dot (content color, 60 %) · stats `topicStats`. Delete icon button 40 × 40, icon Trash 20, content color 75 %. |
| Import row | Padding 12 / 18, "+" (width 20) gap 12 "Import topic", both `importAction`. Last item of the list. |
| Loading (L1) | Spinner 32 dp, stroke 3 dp, centered in the list area. Track = `surfaces.progressTrack.fill`, arc = `accent` → `highlight` (two quarter arcs). |
| Empty (L2) | Padding 18 / 22 / 6 / 22, `body`, two lines. |
| Snackbar (L6) | Surface `snackbar`, shape `card`. Left/right 12, bottom 16 above the gesture bar. Padding 14 / 22. Text `snackbar`. |
| Dialog (L5, P2) | Scrim `colors.scrim` full screen. Card: surface `dialog`, shape `dialog`, horizontal margin 16, vertically centered, padding 22 / 20 / 16 / 20, gap 10. Title `dialogTitle`, body `dialogBody`, actions row right-aligned, gap 8, margin-top 8: "Cancel" (`dialogAction`, padding 10 / 16) and "Delete" (surface `buttonPrimary`, shape `chip`, padding 10 / 20, `dialogAction` in the surface content color). |

### Quiz (Q1–Q10)
| Element | Spec |
|---|---|
| Top bar | Padding 8 / 20 / 10 / 12. Left: "Quit" button (icon X 14, gap 6, `back`, padding 10). Right: topic title `headerSmall`, right-aligned, 1 line + ellipsis. |
| Counter row | Padding 0 / 20. Current number (2 digits, `counter`), gap 12, "/ total" (`counterTotal`, padding-bottom 2), gap 12, progress bar (fills rest, height 8, margin-bottom 9, track `progressTrack`, fill `progressFill`, shape `progress`). Progress = (index − 1 + (checked ? 1 : 0)) / total. |
| Scroll area | Fills the space between counter row and bottom bar. Padding 18 / 16 / 16 / 16, gap 8. |
| Question card | Surface `questionCard`, shape `card`, padding 14 / 16. Text `question`. (Transparent in Sunset / Neon.) |
| Hint | "Select all correct answers.", `hint`, padding 2 / 6 / 4 / 6. |
| Answer row | Min height 52, padding 14 / 20, gap 14, shape `answer`. Letter A–D (width 16, `answerLetter`), text (`answer`, fills), marker 20 (see states). |
| Answer states | `answerIdle` + empty checkbox · `answerSelected` + checked checkbox · after check: `answerCorrect` (selected and correct, icon Check) · `answerMissed` (correct but not selected, icon Check) · `answerWrong` (selected and wrong, icon X) · `answerDimmed` (not selected, not correct, no marker). |
| Checkbox | 18 × 18, shape `checkbox`, surfaces `checkbox` / `checkboxChecked`, check icon 12. |
| Feedback | Margin-top 10, gap 8. Banner: surface `feedbackCorrect` / `feedbackPartial` / `feedbackWrong`, shape `feedback`, left-aligned, max width 300, padding 8 / 20 / 8 / 18, text `feedback`. Explanation below (optional): `explanation`, padding 0 / 6. |
| Scrolling (Q9, Q10) | Content scrolls; button stays docked. Scrollbar 3 dp wide, 3 from right, `textPrimary` 35 %. At top: 32 dp fade at the bottom edge of the scroll area (transparent → last background stop). |
| Bottom bar | Padding 10 / 16 / 14 / 16. Primary button height 56, shape `button`, horizontal padding 20, text `button`, single line, never wraps. Disabled = `buttonDisabled` while nothing is selected. Labels: "Check answer" → "Next question" / "Show result" (last). |

### Submit button sheet (B1)
Button is the same component as the quiz bottom bar: height 56, full width (328 at 360 dp). Labels tested: "Go!", "Check answer", "Final answer!", "Weiter geht's!", "行くぞ!", "Давай!", plus pressed and disabled. All fit on one line at 360 dp in every theme; the longest (Weiter geht's!) leaves ≥ 80 dp spare. If a future label is wider than the button, scale the text down to min 16 sp, never wrap.

### Result (R1–R3)
| Element | Spec |
|---|---|
| Background | `decor.resultWin` or `decor.resultLose`, full screen behind everything. |
| Topic title | `headerSmall`, padding 14 / 22 / 0 / 22 (below the status bar). |
| Chip + headline | Padding 18 / 22 / 0 / 22, gap 10. Chip "Win" / "Lose": surface `chipWin` / `chipLose`, shape `chip`, padding 4 / 16 / 4 / 14, text `chip`. Headline `headline`. |
| Score row | Padding 4 / 22 / 0 / 22, baseline-aligned, gap 10: correct count `score`, "/ total" `scoreTotal`. |
| Percent | "<p> % correct", `percent`, padding 0 / 22. |
| Chart panel | Pushed to the bottom (flexible spacer above). Surface `legendPanel`, shape `card`, margin 0 / 16, padding 16, gap 18. Pie 128 × 128, slices clockwise from 12 o'clock in the order correct, partially correct, wrong; 2 dp separators in `chartGap`; zero slices are skipped (R3). Legend (fills, gap 12): swatch 12 × 12 (shape `checkbox`, chart color), gap 10, label `legendLabel` (fills), value `legendValue`. |
| Buttons | Padding 14 / 16, gap 10. "Try again": `buttonPrimary`, 56 high. "Back to topics": `buttonSecondary`, 52 high, text `buttonSecondary`. |

### Theme picker (P1–P2)
| Element | Spec |
|---|---|
| Top bar | As quiz top bar; left button "Back" with icon ArrowLeft 16. |
| Title | "Themes", `pageTitle`, padding 4 / 22 / 16 / 22. |
| List | Padding 0 / 16, gap 10. Last item: import row "+ Import theme" (same as topic list). |
| Theme row | Surface `topicRow`, shape `topicRow`, padding 10 / 10 / 10 / 14, gap 14. Preview 52 × 90 · text column (name `topicTitle`; source line `topicDescription`: "Built-in" or "Imported · <file name>"; current: chip "Current", surface `chipCurrent`, shape `chip`, padding 2 / 10, margin-top 4) · trailing 40 × 40: current → icon Check 22 in `contentAccent`; deletable → Trash 20; built-in non-current → nothing. |
| Preview | Drawn from the *listed* theme's tokens (not the active theme): 52 × 90, shape `preview` of the active theme, 1 dp border `textMuted`. Fill = its `background`; top 30 = simplified `decor.listHeader` (sunset → sky, glow → radial glow r 40, bands → bands); logo bar 26 × 6 at (6, 18) in its logo color; three 8 dp bars at y 38 / 50 / 62 (inset 6, radius 3) in `answerIdle`, `answerSelected`, `answerIdle` fill + border; button bar 10 dp at bottom 6 in `buttonPrimary`. |

## B2. Value types (as used in theme-neon-night.json)

| Type | Format |
|---|---|
| Color | `"#RRGGBB"` or `"#AARRGGBB"` (Android order, alpha first) |
| ColorRef | A Color, or a key of `colors` (e.g. `"accent"`), or inside a surface: `"content"` (the surface's content color) / `"contentAccent"` |
| Paint | A ColorRef, or a gradient `{ "angle": deg, "stops": [ { "color": ColorRef, "offset": 0..1 } ] }`. Angle like CSS: 0 = bottom→top, 90 = left→right, 180 = top→bottom, 100 = left→right tilted 10° down. Compose: `Brush.linearGradient` with start/end on the line through the center at that angle, length = |w·sin a| + |h·cos a|. |
| Border | `{ "width": dp, "color": ColorRef, "style"?: "solid" or "dashed" (dash 6 / gap 4), "sides"?: "all" (default) or "top" }` |
| Effect | `{ "type": "glow", "color": Color, "radius": dp }` blurred shadow with 0 offset, follows the shape; or `{ "type": "hardShadow", "dx": dp, "dy": dp, "color": Color }` the same shape filled and offset, no blur. Drawn behind the surface. |
| Surface | `{ "fill": Paint, "content": ColorRef, "contentAccent"?: ColorRef, "border"?: Border, "effect"?: Effect }`. `content` is the color for text and icons on it. |
| Shape | `{ "type": "slanted", "slant": dp }` parallelogram: top-left and bottom-right corners inset horizontally by `slant` · or `{ "type": "rounded", "corners": [topStart, topEnd, bottomEnd, bottomStart] }` in dp (0 = square). |
| TextStyle | `{ "font": FontId, "weight": 100–900, "size": sp, "lineHeight": sp, "letterSpacing": em, "uppercase": bool, "skewX": deg (negative = leans right), "color": ColorRef, "opacity": 0–1, "shadow": null or { "color", "dx", "dy", "blur" }, "stroke": null or { "width": dp, "color" } }` |
| FontId | `"anton"`, `"inter"`, `"shrikhand"`, `"oswald"` (shipped set, see Fonts) |
| Decor | See *Decor styles*. `null` = none. |

## B5. Decor styles

The app implements these three; themes only pick one and set its parameters. All drawable with Compose `Canvas` / `drawBehind`.

**`sunset`**: sky gradient, a sun disc, horizontal stripes, a fade to the background.
| Param | Type | Meaning |
|---|---|---|
| `sky` | Paint | Fills the whole decor area |
| `sun` | Paint | Disc fill (usually vertical gradient) |
| `sunDiameter` | dp | |
| `sunCenterX` | 0–1 | Fraction of width |
| `sunCenterY` | dp | From top of decor area |
| `sunOpacity` | 0–1 | |
| `stripes` | null or { count, height dp, gap dp, color, bottom dp } | Full-width bars stacked upward from `bottom` (distance from decor bottom), drawn above the sun |
| `fade` | null or { height dp, from 0–1, color } | Vertical gradient at the bottom: transparent at the top of `height`, solid `color` from fraction `from` (0 = only at the end) |

**`glow`**: soft radial light, optional rain streaks.
| Param | Type | Meaning |
|---|---|---|
| `base` | Paint or null | Optional fill under the glow |
| `color` | Color | Glow center color, fades to transparent |
| `centerX`, `centerY` | 0–1 | Fractions of the decor area (may exceed 1) |
| `radius` | dp | |
| `rain` | null or { color, angle deg, spacing dp } | 1 dp parallel lines across the area |

**`bands`**: a card of horizontal color bands, optional circle.
| Param | Type | Meaning |
|---|---|---|
| `colors` | Color[] | Bands from top to bottom, hard edges |
| `weights` | number[] | Relative band heights, sum 1 |
| `inset` | { top, left, right, bottom } dp | Card position inside the decor area |
| `height` | dp or null | Fixed card height (else top/bottom insets) |
| `radius` | dp | Card corner radius |
| `border` | Border or null | |
| `effect` | Effect or null | |
| `circle` | null or { color, diameter, centerX, centerY } | Disc clipped to the card; centers are fractions of the card |

## B6. Icons

Phosphor Icons (phosphoricons.com), MIT license, "regular" weight unless noted. Use the official Android vector drawables or convert the SVGs. Color = the content color of the element they sit on.

| Use | Icon | Size dp |
|---|---|---|
| Theme button (topic list) | Palette | 24 |
| Delete (topic / theme rows) | Trash | 20 |
| Quit (quiz) | X (bold) | 14 |
| Back (picker) | ArrowLeft (bold) | 16 |
| Checkbox tick | Check (bold) | 12 |
| Answer marker: correct / missed | Check (bold) | 20 |
| Answer marker: wrong | X (bold) | 20 |
| Current theme | Check (bold) | 22 |
| Import rows | "+" character in `importAction` style | – |

## B7. Animations

Same for all themes (themes do not change motion).

| What | Motion | Duration | Easing |
|---|---|---|---|
| Screen change (list ↔ quiz ↔ result ↔ picker) | Crossfade + 24 dp slide in the navigation direction | 250 ms | FastOutSlowIn |
| Topic / theme row press | Surface swaps to the pressed style | 120 ms | Linear |
| Answer select / deselect | Fill, border and checkbox crossfade | 150 ms | FastOutSlowIn |
| Check answer | Rows change to result states, staggered 40 ms per row | 200 ms each | FastOutSlowIn |
| Feedback banner | Slides in 16 dp from the start edge + fades in | 220 ms | Decelerate (LinearOutSlowIn) |
| Progress bar | Width to new value | 350 ms | FastOutSlowIn |
| Next question | Content fades out 120 ms, new content fades in and slides up 12 dp | 120 + 200 ms | FastOutSlowIn |
| Primary button press | Scale to 0.97 and pressed surface | 100 ms down / 150 ms up | FastOutSlowIn |
| Button enable | Disabled → enabled surface crossfade | 150 ms | Linear |
| Snackbar | Slides up 24 dp + fade; auto-hide after 4 s; exit reverse | 250 ms | FastOutSlowIn |
| Dialog | Scrim fades in; card scales 0.94 → 1 + fades | 200 ms | FastOutSlowIn |
| Result score | Count up from 0 to the correct count | 800 ms | Decelerate |
| Pie chart | Sweep clockwise from 12 o'clock, slices in order | 900 ms total | FastOutSlowIn |
| Result chip + headline | Fade + slide up 8 dp, after the count-up starts (+150 ms) | 250 ms | FastOutSlowIn |
| Loading spinner | Rotation | 800 ms per turn | Linear, infinite |
| Theme switch | Whole screen crossfade to the new theme | 300 ms | Linear |

Respect the system "Remove animations" setting: skip count-up and sweep, show the final state.

## B8. Compose notes

- **Slanted shape**: `GenericShape` with points (slant, 0), (w, 0), (w − slant, h), (0, h). Borders and glows follow the shape (`Modifier.border(width, color, shape)`).
- **Glow effect**: `drawBehind` with a `Paint` using `BlurMaskFilter(radius)` on the shape outline (`drawIntoCanvas { it.nativeCanvas … }`). Fallback below API 28: skip the glow, keep the border.
- **Hard shadow**: draw the same shape offset by (dx, dy) in the shadow color before the surface. Reserve the offset inside the layout gaps (all gaps ≥ 8 dp; max offset 6 dp, dialog only).
- **Skewed text**: `TextStyle(textGeometricTransform = TextGeometricTransform(skewX = tan(−deg)))`. skewX −9° ≈ 0.158 (leans right).
- **Outlined text** (Neon Night logo): `TextStyle(drawStyle = Stroke(width))`, color = stroke color, plus the glow `shadow`.
- **Text shadow**: `TextStyle(shadow = Shadow(color, Offset(dx, dy), blurRadius))`.
- **Gradient paint in text** is not used, so all text colors are solid.
- **Dashed border** (Postcard `answerMissed`): `drawBehind` with `PathEffect.dashPathEffect(floatArrayOf(6.dp, 4.dp))` on the shape outline.
- Decor is drawn in a `Canvas` behind the content. Stripes and bands are plain rects.

---
## C. Assumptions (please confirm)

- **Partially correct**: at least one correct answer selected, but the selection is not exactly the correct set. **Wrong**: no correct answer selected. **Correct**: exactly the correct set (unchanged from `QuizSession`).
- **Win / lose**: win if `correct * 100 / total >= 70` (the existing "Well done!" threshold). Headline: 100 % → "Perfect!", win → "Well done!", lose → "Keep practising!".
- **Topic stats** "x wins / y games" are new data (per topic: games played, games won). Singular: "1 win", "1 game". A new topic shows "0 wins / 0 games".
- **New copy**: partial feedback "Partly right. The correct answers are marked with ✓."; picker strings "Themes", "Import theme", "Current", "Built-in", "Imported · <file name>", "Delete theme?". All other strings are unchanged from the app.
- **Themes**: Sunset Coast is built in and cannot be deleted. The current theme cannot be deleted (no delete button). Importing a file with an existing `id` replaces that theme (same rule as topics).
- Themes may be light (Postcard). `statusBarIcons` tells the app which system bar icon color to request.

## Screens

| Id | State |
|---|---|
| L1 | Loading |
| L2 | Empty |
| L3 | 3 topics, one 2-line title, with stats |
| L4 | Row pressed |
| L5 | Delete topic dialog |
| L6 | Snackbar after import (4 topics) |
| Q1 | 4 answers, nothing selected, button disabled |
| Q2 | Answers selected |
| Q3 | Correct, with explanation |
| Q4 | Correct, 2 answers, no explanation |
| Q5 | Partially correct, with explanation, last question |
| Q6 | Partially correct, no explanation (incl. one wrong pick) |
| Q7 | Wrong, with explanation |
| Q8 | Wrong, no explanation |
| Q9 | Long question, scrolled to top |
| Q10 | Long question, scrolled to bottom |
| B1 | Submit button: label lengths, scripts, pressed, disabled |
| R1 | Win 13 / 16 (13 · 2 · 1) |
| R2 | Lose 7 / 16 (7 · 4 · 5) |
| R3 | Short game 8 / 9 (8 · 1 · 0) |
| P1 | Theme picker, current marked |
| P2 | Delete theme dialog |


Files: `screens/<Id>.png`.
