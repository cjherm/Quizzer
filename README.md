# Quizzer
How can I easily learn new things while commuting?

Quizzer is a simple Android quiz app (Kotlin + Jetpack Compose). Questions are not hardcoded:
each topic is a JSON file that can be imported into the app.

## Topic file format

One file per topic. Each question has 2–4 answers; one, several, or all of them can be correct.

```json
{
  "title": "ASPICE",
  "description": "Optional short description shown in the topic list.",
  "questions": [
    {
      "question": "Which of these are capability levels in ASPICE?",
      "answers": [
        { "text": "Level 0 – Incomplete", "correct": true },
        { "text": "Level 1 – Performed",  "correct": true },
        { "text": "Level 7 – Perfect",    "correct": false }
      ],
      "explanation": "Optional. Shown after the answer has been checked."
    }
  ]
}
```

| Field | Required | Notes |
|---|---|---|
| `title` | yes | Importing a file with the same title replaces the existing topic. |
| `description` | no | |
| `questions[].question` | yes | |
| `questions[].answers` | yes | 2–4 entries, at least one with `"correct": true`. |
| `answers[].correct` | no | Defaults to `false`. |
| `questions[].explanation` | no | |

A question counts as correct only if exactly the correct answers are selected.
Questions and answers are shuffled every time a quiz starts.

## Topics in the app

- Two demo topics (`app/src/main/assets/topics/`) are installed on first launch.
- **Import topic** opens the system file picker; the file is validated and copied into the app.
- Topics can be deleted from the topic list.
- `sample-topics/` contains an extra file for testing the import, e.g.:
  `adb push sample-topics/kotlin_basics_demo.json /sdcard/Download/`

## Build & run

```
.\gradlew installDebug     # build and install on the connected device
.\gradlew testDebugUnitTest  # run unit tests
```
