# Quizzer – Sunset Coast theme handoff

Theme id `sunset-coast` · built in (cannot be deleted) · status bar icons: **light**.
Target: Samsung Galaxy S22, 360 × 780 dp (1080 × 2340 px). All sizes dp, text sizes sp.

This package contains:
| File | Content |
|---|---|
| `handoff.md` | This file: every value needed to implement the theme, plus the shared layout |
| `theme-sunset-coast.json` | The same values, machine-readable (schema v1), to ship as an asset |
| `screens/<state>.png` | 22 states, 1080 × 2340 px |

The layout is the same for every theme (section B). This theme changes only the values in section A.

---
# A. Theme values

## A1. Colors
| Token | Hex (ARGB) | Opacity | Compose | Used for |
|---|---|---|---|---|
| `textPrimary` | `#FFF3F8` | 100 % | `Color(0xFFFFF3F8)` | Main text, icons on background |
| `textSecondary` | `#E8C9DC` | 100 % | `Color(0xFFE8C9DC)` | Descriptions, explanations, dialog body, legend labels |
| `textMuted` | `#B48AAE` | 100 % | `Color(0xFFB48AAE)` | Counter total, captions, preview border |
| `accent` | `#FFC65C` | 100 % | `Color(0xFFFFC65C)` | Section label, counter, percent, stats, spinner arc |
| `highlight` | `#FF8AB8` | 100 % | `Color(0xFFFF8AB8)` | Hint text, second spinner arc |
| `onFill` | `#1A0B24` | 100 % | `Color(0xFF1A0B24)` | Text/icons on bright fills |
| `displayShadow` | `#FF3D8B` | 100 % | `Color(0xFFFF3D8B)` | Shadow of display text |
| `scrim` | `#B80C0614` | 72 % | `Color(0xB80C0614)` | Dialog backdrop |
| `chartCorrect` | `#2EE0C4` | 100 % | `Color(0xFF2EE0C4)` | Pie: correct |
| `chartPartial` | `#FFC65C` | 100 % | `Color(0xFFFFC65C)` | Pie: partially correct |
| `chartWrong` | `#FF4D6A` | 100 % | `Color(0xFFFF4D6A)` | Pie: wrong |
| `chartGap` | `#1A0C2C` | 100 % | `Color(0xFF1A0C2C)` | Pie slice separators (2 dp) |

## A2. Background
Screen background: linear 180°: `#140A24` @ 0 %, `#1D0D33` @ 60 %, `#2A1145` @ 100 %

Angle convention: 0° = bottom → top, 90° = left → right, 180° = top → bottom (CSS convention). In Compose use `Brush.linearGradient(colorStops, start, end)` along that direction through the element's center.

## A3. Surfaces
"content" = color of text and icons placed on the surface.

| Surface | Fill | Content | Accent content | Border | Effect | Used by |
|---|---|---|---|---|---|---|
| `topicRow` | `#00000000` | `textPrimary` = `#FFF3F8` | `accent` = `#FFC65C` | – | – | Topic rows, theme rows |
| `topicRowPressed` | linear 100°: `#FF3D8B` @ 0 %, `#FF8A3D` @ 100 % | `onFill` = `#1A0B24` | `onFill` = `#1A0B24` | – | – | Pressed topic/theme row |
| `questionCard` | `#00000000` | `textPrimary` = `#FFF3F8` | – | – | – | Box around question text |
| `answerIdle` | `#0FFFF0FA` | `textPrimary` = `#FFF3F8` | – | – | – | Answer, not selected |
| `answerSelected` | linear 100°: `#FF3D8B` @ 0 %, `#FF8A3D` @ 100 % | `onFill` = `#1A0B24` | – | – | – | Answer, selected |
| `answerCorrect` | linear 100°: `#18C9B4` @ 0 %, `#5FF2D6` @ 100 % | `onFill` = `#1A0B24` | – | – | – | After check: selected and correct |
| `answerMissed` | `#2E18C9B4` | `#5FF2D6` | – | – | – | After check: correct, not selected |
| `answerWrong` | linear 100°: `#F2445F` @ 0 %, `#FF6E70` @ 100 % | `onFill` = `#1A0B24` | – | – | – | After check: selected and wrong |
| `answerDimmed` | `#0FFFF0FA` | `#99FFF3F8` | – | – | – | After check: not selected, not correct |
| `checkbox` | `#00000000` | `content` | – | 2 dp solid `content` | – | Empty checkbox |
| `checkboxChecked` | `onFill` = `#1A0B24` | `textPrimary` = `#FFF3F8` | – | 2 dp solid `onFill` = `#1A0B24` | – | Checked checkbox |
| `feedbackCorrect` | linear 100°: `#18C9B4` @ 0 %, `#5FF2D6` @ 100 % | `onFill` = `#1A0B24` | – | – | – | Feedback banner: correct |
| `feedbackPartial` | linear 100°: `#FFC65C` @ 0 %, `#FF8A3D` @ 100 % | `onFill` = `#1A0B24` | – | – | – | Feedback banner: partially correct |
| `feedbackWrong` | linear 100°: `#F2445F` @ 0 %, `#FF6E70` @ 100 % | `onFill` = `#1A0B24` | – | – | – | Feedback banner: wrong |
| `buttonPrimary` | linear 100°: `#FF3D8B` @ 0 %, `#FF8A3D` @ 60 %, `#FFC65C` @ 100 % | `onFill` = `#1A0B24` | – | – | – | Submit, Try again, dialog Delete |
| `buttonPrimaryPressed` | linear 100°: `#EA3F80` @ 0 %, `#EE8238` @ 60 %, `#EEB650` @ 100 % | `onFill` = `#1A0B24` | – | – | – | Primary button pressed |
| `buttonDisabled` | `#1AFFF0FA` | `#73FFF3F8` | – | – | – | Primary button disabled |
| `buttonSecondary` | `#1AFFF0FA` | `textPrimary` = `#FFF3F8` | – | – | – | Back to topics |
| `progressTrack` | `#1AFFF0FA` | `textPrimary` = `#FFF3F8` | – | – | – | Progress track, spinner track |
| `progressFill` | linear 90°: `#FF3D8B` @ 0 %, `#FF8A3D` @ 50 %, `#FFC65C` @ 100 % | `onFill` = `#1A0B24` | – | – | – | Progress fill |
| `dialog` | `#1D0D33` | `textPrimary` = `#FFF3F8` | – | 3 dp solid `#FF3D8B` (top only) | – | Dialog card |
| `snackbar` | linear 100°: `#FFC65C` @ 0 %, `#FF8A3D` @ 100 % | `onFill` = `#1A0B24` | – | – | – | Snackbar |
| `legendPanel` | `#B8140A24` | `textPrimary` = `#FFF3F8` | – | – | – | Result chart panel |
| `chipWin` | linear 100°: `#18C9B4` @ 0 %, `#5FF2D6` @ 100 % | `onFill` = `#1A0B24` | – | – | – | Chip "Win" |
| `chipLose` | linear 100°: `#F2445F` @ 0 %, `#FF6E70` @ 100 % | `onFill` = `#1A0B24` | – | – | – | Chip "Lose" |
| `chipCurrent` | linear 100°: `#FF3D8B` @ 0 %, `#FF8A3D` @ 100 % | `onFill` = `#1A0B24` | – | – | – | Chip "Current" |

## A4. Shapes
| Shape | Value | Applies to |
|---|---|---|
| `topicRow` | slanted, slant 10 dp | Topic and theme rows |
| `answer` | slanted, slant 12 dp | Answer rows |
| `feedback` | slanted, slant 10 dp | Feedback banner |
| `button` | slanted, slant 14 dp | Primary and secondary buttons |
| `chip` | slanted, slant 8 dp | Chips, dialog Delete button |
| `progress` | slanted, slant 4 dp | Progress track and fill |
| `card` | square | Question card, snackbar, chart panel |
| `dialog` | square | Dialog card |
| `checkbox` | rounded 3 dp | Checkbox, legend swatches |
| `preview` | rounded 6 dp | Theme preview in picker |

Slanted = parallelogram: top-left and bottom-right corners inset horizontally by the slant (`GenericShape`: (s,0) → (w,0) → (w−s,h) → (0,h)).

## A5. Typography
Color "content" = content color of the surface the text sits on. Skew: negative leans right; Compose `TextGeometricTransform(skewX = -tan(deg))` (−9° → 0.158).

| Style | Font | Weight | Size sp | Line height sp | Letter spacing | Upper | Skew | Color | Opacity | Shadow | Stroke |
|---|---|---|---|---|---|---|---|---|---|---|---|
| `logo` | Anton | 400 | 68 | 68 | 0.01 em | yes | -9° | `textPrimary` = `#FFF3F8` | 1 | `displayShadow` = `#FF3D8B` 0/4 dp, blur 0 | – |
| `sectionLabel` | Anton | 400 | 15 | 20 | 0.18 em | yes | – | `accent` = `#FFC65C` | 1 | – | – |
| `topicIndex` | Anton | 400 | 14 | 20 | 0 | no | – | `content` | 0.7 | – | – |
| `topicTitle` | Anton | 400 | 24 | 26 | 0.01 em | yes | – | `content` | 1 | – | – |
| `topicDescription` | Inter | 400 | 12 | 16 | 0 | no | – | `content` | 0.8 | – | – |
| `topicMeta` | Inter | 600 | 12 | 16 | 0 | no | – | `content` | 1 | – | – |
| `topicStats` | Inter | 600 | 12 | 16 | 0 | no | – | `contentAccent` | 1 | – | – |
| `importAction` | Anton | 400 | 22 | 28 | 0.02 em | yes | – | `accent` = `#FFC65C` | 1 | – | – |
| `headerSmall` | Inter | 600 | 12 | 16 | 0.06 em | yes | – | `textSecondary` = `#E8C9DC` | 1 | – | – |
| `back` | Anton | 400 | 15 | 20 | 0.08 em | yes | – | `textSecondary` = `#E8C9DC` | 1 | – | – |
| `counter` | Anton | 400 | 52 | 52 | 0 | no | -9° | `accent` = `#FFC65C` | 1 | `displayShadow` = `#FF3D8B` 0/3 dp, blur 0 | – |
| `counterTotal` | Anton | 400 | 20 | 26 | 0 | no | – | `textMuted` = `#B48AAE` | 1 | – | – |
| `question` | Inter | 700 | 21 | 27 | 0 | no | – | `content` | 1 | – | – |
| `hint` | Inter | 600 | 12 | 16 | 0.04 em | yes | – | `highlight` = `#FF8AB8` | 1 | – | – |
| `answerLetter` | Anton | 400 | 20 | 24 | 0 | no | – | `content` | 1 | – | – |
| `answer` | Inter | 600 | 15 | 20 | 0 | no | – | `content` | 1 | – | – |
| `feedback` | Anton | 400 | 20 | 24 | 0.02 em | yes | – | `content` | 1 | – | – |
| `explanation` | Inter | 400 | 13 | 20 | 0 | no | – | `textSecondary` = `#E8C9DC` | 1 | – | – |
| `button` | Anton | 400 | 22 | 28 | 0.04 em | yes | – | `content` | 1 | – | – |
| `buttonSecondary` | Anton | 400 | 20 | 26 | 0.04 em | yes | – | `content` | 1 | – | – |
| `chip` | Anton | 400 | 16 | 20 | 0.14 em | yes | – | `content` | 1 | – | – |
| `pageTitle` | Anton | 400 | 40 | 44 | 0 | yes | -9° | `textPrimary` = `#FFF3F8` | 1 | `displayShadow` = `#FF3D8B` 0/3 dp, blur 0 | – |
| `headline` | Anton | 400 | 46 | 48 | 0 | yes | -9° | `textPrimary` = `#FFF3F8` | 1 | `displayShadow` = `#FF3D8B` 0/3 dp, blur 0 | – |
| `score` | Anton | 400 | 120 | 124 | 0 | no | – | `textPrimary` = `#FFF3F8` | 1 | `displayShadow` = `#FF3D8B` 0/6 dp, blur 0 | – |
| `scoreTotal` | Anton | 400 | 40 | 48 | 0 | no | – | `textSecondary` = `#E8C9DC` | 1 | – | – |
| `percent` | Anton | 400 | 22 | 28 | 0.04 em | yes | – | `accent` = `#FFC65C` | 1 | – | – |
| `legendLabel` | Inter | 600 | 13 | 18 | 0 | no | – | `textSecondary` = `#E8C9DC` | 1 | – | – |
| `legendValue` | Anton | 400 | 20 | 24 | 0 | no | – | `textPrimary` = `#FFF3F8` | 1 | – | – |
| `dialogTitle` | Anton | 400 | 28 | 34 | 0.02 em | yes | -9° | `content` | 1 | – | – |
| `dialogBody` | Inter | 400 | 14 | 21 | 0 | no | – | `textSecondary` = `#E8C9DC` | 1 | – | – |
| `dialogAction` | Anton | 400 | 17 | 24 | 0.06 em | yes | – | `textSecondary` = `#E8C9DC` | 1 | – | – |
| `snackbar` | Inter | 700 | 13 | 18 | 0 | no | – | `content` | 1 | – | – |
| `body` | Inter | 400 | 15 | 22 | 0 | no | – | `textSecondary` = `#E8C9DC` | 1 | – | – |

Where each style is used: see section B (every element names its style).

## A6. Decor
### `listHeader` (Topic list header, 360 × 236): type **sunset**
| Param | Value |
|---|---|
| sky | linear 180°: `#2A1145` @ 0 %, `#7A1F6B` @ 32 %, `#FF3D8B` @ 58 %, `#FF8A3D` @ 82 %, `#FFC65C` @ 100 % |
| sun | linear 180°: `#FFE08A` @ 0 %, `#FFB45C` @ 45 %, `#FF3D8B` @ 100 % |
| sunDiameter | 160 dp |
| sunCenterX | 0.5 |
| sunCenterY | 120 dp |
| sunOpacity | 1 |
| stripes | 6 bars, 4 dp high, 9 dp gap, `#FF6A6A`, bottom 36 dp |
| fade | height 116 dp, solid from 0 %, `#140A24` |

### `resultWin` (Result background (win), 360 × 780): type **sunset**
| Param | Value |
|---|---|
| sky | linear 180°: `#140A24` @ 0 %, `#4A1460` @ 30 %, `#C42A7A` @ 50 %, `#FF8A3D` @ 62 %, `#FFC65C` @ 70 % |
| sun | linear 180°: `#FFE08A` @ 0 %, `#FF3D8B` @ 100 % |
| sunDiameter | 200 dp |
| sunCenterX | 0.8 |
| sunCenterY | 400 dp |
| sunOpacity | 0.6 |
| stripes | 7 bars, 4 dp high, 10 dp gap, `#8CFF3D8B`, bottom 340 dp |
| fade | height 480 dp, solid from 45 %, `#140A24` |

### `resultLose` (Result background (lose), 360 × 780): type **sunset**
| Param | Value |
|---|---|
| sky | linear 180°: `#140A24` @ 0 %, `#2A1145` @ 30 %, `#5A1A63` @ 50 %, `#8C2A6E` @ 62 % |
| sun | linear 180°: `#FFB45C` @ 0 %, `#FF3D8B` @ 100 % |
| sunDiameter | 200 dp |
| sunCenterX | 0.8 |
| sunCenterY | 470 dp |
| sunOpacity | 0.35 |
| stripes | 7 bars, 4 dp high, 10 dp gap, `#59FF3D8B`, bottom 340 dp |
| fade | height 480 dp, solid from 45 %, `#140A24` |

### `screen` (Overlay on all screens, 360 × 780)
None.

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
| textPrimary on background | 15.36 : 1 | 4.5 : 1 | pass |
| textSecondary on background | 10.93 : 1 | 4.5 : 1 | pass |
| textMuted on background | 5.68 : 1 | 4.5 : 1 | pass |
| accent on background | 10.66 : 1 | 4.5 : 1 | pass |
| highlight on background | 7.58 : 1 | 4.5 : 1 | pass |
| topicRow: content on fill | 15.36 : 1 | 4.5 : 1 | pass |
| topicRowPressed: content on fill | 5.63 : 1 | 4.5 : 1 | pass |
| questionCard: content on fill | 15.36 : 1 | 4.5 : 1 | pass |
| answerIdle: content on fill | 13.29 : 1 | 4.5 : 1 | pass |
| answerSelected: content on fill | 5.63 : 1 | 4.5 : 1 | pass |
| answerCorrect: content on fill | 9.00 : 1 | 4.5 : 1 | pass |
| answerMissed: content on fill | 8.97 : 1 | 4.5 : 1 | pass |
| answerWrong: content on fill | 5.16 : 1 | 4.5 : 1 | pass |
| answerDimmed: content on fill | 5.72 : 1 | 4.5 : 1 | pass |
| feedbackCorrect: content on fill | 9.00 : 1 | 4.5 : 1 | pass |
| feedbackPartial: content on fill | 8.01 : 1 | 4.5 : 1 | pass |
| feedbackWrong: content on fill | 5.16 : 1 | 4.5 : 1 | pass |
| buttonPrimary: content on fill | 5.63 : 1 | 4.5 : 1 | pass |
| buttonPrimaryPressed: content on fill | 4.94 : 1 | 4.5 : 1 | pass |
| buttonSecondary: content on fill | 11.74 : 1 | 4.5 : 1 | pass |
| dialog: content on fill | 16.78 : 1 | 4.5 : 1 | pass |
| snackbar: content on fill | 8.01 : 1 | 4.5 : 1 | pass |
| legendPanel: content on fill | 17.12 : 1 | 4.5 : 1 | pass |
| chipWin: content on fill | 9.00 : 1 | 4.5 : 1 | pass |
| chipLose: content on fill | 5.16 : 1 | 4.5 : 1 | pass |
| chipCurrent: content on fill | 5.63 : 1 | 4.5 : 1 | pass |
| topicRow: description | 10.14 : 1 | 4.5 : 1 | pass |
| topicRow: stats (contentAccent) | 10.66 : 1 | 4.5 : 1 | pass |
| topicRowPressed: stats | 5.63 : 1 | 4.5 : 1 | pass |
| dialog: body (textSecondary) | 11.94 : 1 | 4.5 : 1 | pass |
| legend label on legendPanel | 12.18 : 1 | 4.5 : 1 | pass |
| buttonDisabled (exempt: inactive) | 3.67 : 1 | exempt | n/a |
| resultWin: headline over decor (large) | 7.22 : 1 | 3 : 1 | pass |
| resultWin: score over decor (large) | 7.22 : 1 | 3 : 1 | pass |
| resultWin: percent over decor | 5.01 : 1 | 4.5 : 1 | pass |
| resultLose: headline over decor (large) | 12.78 : 1 | 3 : 1 | pass |
| resultLose: score over decor (large) | 12.78 : 1 | 3 : 1 | pass |
| resultLose: percent over decor | 8.87 : 1 | 4.5 : 1 | pass |

Exceptions: disabled button text (inactive control, exempt), the logo (logotype, exempt), decor (no information).

## A9. What this theme lost in the shared layout
Reference layout (direction 2a). Nothing was lost.

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

## B2. Value types (as used in theme-sunset-coast.json)

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
