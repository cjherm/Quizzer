# Quizzer – Plan

Status: sections marked ✅ are implemented; the rest is planned.

## Topic and question IDs ✅

- Topic files contain only human-readable data (`title`, `description`, `questions`). IDs are **not** part of the file.
- **Topic ID** is generated automatically from the initials of the title, lowercase:
  `"Android Automotive Knowledge"` → `aak`. A one-word title uses the word itself (max. 8 characters):
  `"ASPICE"` → `aspice`. Accents are removed (`Ü` → `u`).
- The topic ID is the file name in app storage (`files/topics/aak.json`), so it stays stable.
- **Question ID** = `<topicId>-<index>`, where index is the 0-based position in the file:
  `aak-0`, `aak-1`, …
- IDs are used to store per-question history and per-topic statistics.

Things to keep in mind:

- **Index-based question IDs:** inserting, deleting or reordering questions in a file shifts the IDs,
  so the history then belongs to a different question. Rule: *only append new questions at the end*;
  editing the wording of an existing question is fine.
- **Initials can collide** (e.g. "Android Automotive Knowledge" and "Advanced Algebra Knowledge" → both `aak`).
  On collision a number is appended (`aak2`), and the generated ID is stored with the topic so it stays
  stable once assigned. Re-importing a topic with the same title keeps its existing ID.
- Characters other than a–z/0–9 are dropped when building initials; a title without usable
  initials falls back to `topic`.

## Game rules ✅

- A game has **16 questions**. If a topic has fewer, play a short game with all of them.
- Questions are drawn by weight from the per-question history:

  | Last result | Weight |
  |---|---|
  | Wrong | 4 |
  | Partially correct | 3 |
  | Never asked | 2 |
  | Correct | 1 |

- Answers are shuffled per question.
- Result per question:
  - **Correct:** exactly the correct answers selected.
  - **Partially correct:** at least one correct answer selected, but not exactly the correct set.
  - **Wrong:** no correct answer selected.
- **Win:** at least 75 % correct (partially correct does not count), rounded up:
  16 questions → 12, 9 questions → 7.

## Button labels ✅

- Before checking: random label from 32 "submit" labels.
- After checking: random label from 32 "next" labels.
- Last question after checking: fixed label "Show my score!".
- Picked randomly per question; repeats within a game are allowed.
- Labels live in a string resource list, not in Kotlin code.

Submit labels: Go!, Fire!, Check!, Submit!, Lock it in!, Final answer!, Send it!, Engage!, Launch!,
Let's go!, Los!, Feuer!, Ab geht's!, Allez !, C'est parti !, Feu !, ¡Vamos!, ¡Fuego!, ¡Dale!,
Andiamo!, Fuoco!, Vamos lá!, Vooruit!, Kör!, Hajrá!, Naprzód!, Давай!, İleri!, Yalla!, 行くぞ!,
가자!, Ayo!

Next labels: Next!, Onward!, Moving on!, Bring it on!, Keep going!, Another one!, Hit me!,
Next up!, Continue!, Carry on!, Weiter!, Weiter geht's!, Nächste!, Suivant !, En avant !,
On continue !, ¡Siguiente!, ¡Adelante!, ¡Otra!, Avanti!, Prossima!, Próxima!, Verder!, Nästa!,
Dalej!, Další!, Tovább!, Дальше!, Devam!, 次へ!, 다음!, Lanjut!

## Result screen ✅


- Pie chart (drawn with Compose Canvas) with three slices: correct (green), partially correct
  (orange), wrong (red); score `12 / 16` in the middle.
- "You win!" / "Not this time", plus the score needed to win.

## Statistics ✅

- Stored in a small JSON file in app storage (no database).
- Per question (by question ID): last result, times asked, times correct.
- Per topic (by topic ID): games played, games won – shown in the topic list,
  e.g. "ASPICE · 5 wins / 8 games".
- Deleting a topic deletes its statistics. Re-importing a topic with the same title keeps its ID and history.
- A question's result is recorded when it is checked (so it counts even if the game is quit);
  a game counts as played only when it is finished.

## Themes ✅

- Four themes from Claude Design, built in: Sunset Coast (default), Neon Night, Postcard, Pocket Phone.
  Design handoff per theme (spec, theme JSON, 22 reference screens): `docs/design/themes/<id>/`.
- The palette button on the topic list opens the theme picker; the choice is remembered.
- A theme is a JSON file (schema v1) with colors, gradients, surfaces, shapes, text styles and decor.
  All screens read these tokens; the layout is the same for every theme.
- "Compatible" = the file has every token the app uses; validated on import with a message naming
  the missing/invalid token. Imported themes can be deleted; built-in and current theme cannot.
- Fonts shipped with the app (OFL): Anton, Inter, Shrikhand, Oswald (Cyrillic in Anton styles).
  Japanese/Korean use the system font. Icons: Phosphor (MIT). Licenses in `app/src/main/assets/licenses/`.
- Deviations from the handoff's "assumptions": win at 75 % (not 70 %), random button labels instead of
  "Check answer" / "Next question", and "N correct needed to win" shown on a lost game.
- Not done yet: answer-row crossfade/stagger animation, scrollbar in long questions, an in-app
  licenses screen.

## Build order

1. ✅ Game rules: IDs, 16 questions / short game, random labels, partial scoring, 75 % win rule.
2. ✅ Result screen with pie chart + theme system.
3. ✅ Statistics: weighted question selection, wins/games in the topic list.
