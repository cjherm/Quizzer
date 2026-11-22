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

## Result screen

✅ Interim version: win/lose, score, score needed to win, short-game note, and the counts of
correct / partially correct / wrong as plain rows. Still to do:

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

## Themes (idea – waiting for Claude Design)

- The user can switch the app's theme, and add/remove themes, similar to topics.
- A theme is a **data file** (e.g. JSON) with design tokens: colors, gradients, fonts, text styles,
  shape style (e.g. slant), spacing. The app has **one set of screens** that read these tokens.
- "Compatible" = the file has all required tokens; validated on import like topic files.
- Themes can change the look, but not the layout structure (e.g. a different screen arrangement
  needs code). Possible small switches: header style, list row style.
- Open: fonts – bundled set to choose from, or a theme package (zip) that includes font files.

## Build order

1. ✅ Game rules: IDs, 16 questions / short game, random labels, partial scoring, 75 % win rule.
2. Result screen with pie chart + chosen design / theme system (waiting for Claude Design).
3. ✅ Statistics: weighted question selection, wins/games in the topic list.
