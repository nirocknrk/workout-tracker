# Workout Tracker — Android (JSON-driven)

A native Android workout tracker (Kotlin + Jetpack Compose, Material 3) whose **entire program content comes from `program.json`**. Update the JSON — schedule, exercises, videos, coaching notes — and the app picks it up on next launch or via an imported bundle. No rebuild, no reinstall.

- Package: `com.nova.workouttracker` · minSdk 26 · target/compileSdk 34 · Kotlin 2.0 · Compose BOM
- Fully offline-first: the only network use is tapping a technique-video link (opens YouTube in the browser/app).

## Project layout

```
workout-tracker-android/
├── program.json                  # master program data (edit this to change app content)
├── media/                        # 18 exercise icons as SVG (ic_<exercise_id>.svg)
├── README.md
├── settings.gradle.kts / build.gradle.kts / gradle.properties
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        ├── assets/
        │   ├── program.json      # bundled default (copied from ../../program.json)
        │   └── media/*.svg       # bundled default icons
        ├── java/com/nova/workouttracker/
        │   ├── MainActivity.kt            # entry point, nav graph, bottom tabs
        │   ├── data/
        │   │   ├── Models.kt             # Program + WorkoutLog data classes (kotlinx.serialization)
        │   │   ├── ProgramRepository.kt  # load / export / import program bundle
        │   │   └── LogRepository.kt      # workout log files + weekly stats
        │   ├── ui/
        │   │   ├── Nav.kt                # route definitions
        │   │   ├── Components.kt         # exercise icon, formatting helpers
        │   │   ├── theme/Theme.kt        # Material 3 dark/light theme
        │   │   └── screens/
        │   │       ├── HomeScreen.kt         # 3 day cards, exercise list, Start Day
        │   │       ├── SessionScreen.kt      # active logging: weight + reps/sec per set
        │   │       ├── ExerciseDetailScreen.kt # video link, setup, dos/don'ts, mistakes, tip
        │   │       ├── HistoryScreen.kt      # logged workouts, delete
        │   │       ├── StatsScreen.kt        # 7-day unit-aware summary
        │   │       └── ProgramScreen.kt      # export / import bundle UI
        │   └── util/Network.kt           # connectivity check for video links
        └── res/
            ├── drawable/ic_*.xml     # 18 VectorDrawables converted from media/ SVGs
            ├── drawable/ic_launcher.xml
            └── values/{strings,themes}.xml
```

## `program.json` schema

```jsonc
{
  "meta": {
    "programName": "Nova's 3-Day Dumbbell Program",  // shown in the app bar
    "version": "1.0.0",                              // bump on every content change
    "updatedAt": "2026-10-15"                        // informational
  },
  "days": [
    {
      "id": "day1",                                  // stable id, referenced by logs
      "title": "Day 1 — Push",                       // shown on the home card
      "focus": "Chest · Shoulders · Triceps",        // subtitle
      "items": [
        {
          "exerciseId": "flat_db_bench",             // must exist in "exercises"
          "sets": 4,                                 // preloaded set rows
          "target": "6-10",                          // rep/seconds range shown as target
          "unit": "reps",                            // "reps" | "seconds"
          "unilateral": null                         // null | "side" | "leg"
        }
      ]
    }
  ],
  "exercises": {
    "flat_db_bench": {
      "name": "Flat DB Bench Press",
      "icon": "ic_flat_db_bench",                    // matches media/ic_flat_db_bench.svg
      "primaryMuscles": ["Chest", "Front Delts", "Triceps"],
      "video": {
        "watchUrl": "https://www.youtube.com/watch?v=…",
        "embedUrl": "https://www.youtube.com/embed/…",
        "channel": "Barbell Logic",                  // attribution
        "title": "The Dumbbell Bench Press: Gym Shorts (How To)"
      },
      "referenceNote": "…",                          // one-paragraph reference
      "setup": ["…", "…"],                           // 3–5 ordered steps
      "tempo": "2-1-2",
      "dos": ["…"], "donts": ["…"],
      "mistakes": ["…"],                             // common mistakes
      "tip": "…"                                     // pro tip
    }
  }
}
```

Field rules:

- `unit`: `"reps"` renders a "Reps" input; `"seconds"` renders a "Sec" input and the set is counted as time-under-tension, excluded from loaded volume.
- `unilateral`: `"side"` / `"leg"` appends "per side" / "per leg" to the target label and doubles that exercise's volume in stats.
- `icon`: base name of the SVG in `media/` (without extension).
- Unknown extra fields are ignored on import (`ignoreUnknownKeys = true`), but missing required fields fail validation.

## Updating program content without rebuilding

1. Edit `program.json` (add/remove exercises, change days, swap videos…).
2. If you add an exercise, drop its icon as `media/ic_<new_id>.svg` (square, stroke-style, 32×32 viewBox like the others).
3. Bump `meta.version` and set `meta.updatedAt`.
4. On the phone: open the app → **Program tab → Import bundle** and pick a ZIP containing your updated `program.json` + `media/` folder (see bundle format below). The app validates the JSON first and only then replaces its copy — a bad file can never corrupt the app.

Shortcut for testing on an emulator: overwrite `app/src/main/assets/program.json` (+ `assets/media/`) and reinstall — first-run seeding copies assets into private storage.

Where the app keeps things at runtime (private app storage, no root needed to use the app):

- `filesDir/program.json` — the active program (what the UI reads).
- `filesDir/media/*.svg` — the active icons.
- `filesDir/logs/<yyyy-MM-dd_HHmmss>_<dayId>.json` — one JSON file per finished workout.

## Export / import bundle format

A bundle is a ZIP file with this exact layout:

```
workout-program-v1.0.0.zip
├── program.json
└── media/
    ├── ic_flat_db_bench.svg
    ├── … (one per exercise)
```

- **Export** (Program tab → Export bundle): the app zips its *active* `filesDir` copies via the system file saver — share it to Drive, email, another device.
- **Import** (Program tab → Import bundle): pick a ZIP with the system file picker. The app requires `program.json` at the ZIP root and validates it against the schema above; media SVGs are optional (missing icons simply don't render). On success the app reloads immediately.

## Building the APK

No Android SDK is bundled here — build on a machine with Android Studio:

1. Open `workout-tracker-android/` in Android Studio (it will offer to generate the Gradle wrapper if missing).
2. Let it sync (downloads AGP 8.5.2, Kotlin 2.0.20, Compose BOM 2024.06.00).
3. **Build → Build APK(s)**. Install `app/build/outputs/apk/debug/app-debug.apk` on a device (Android 8.0+).

Static validation (no SDK needed) is provided by `/tmp/validate_android.py` on the build machine: checks JSON schema, day→exercise references, icon/drawable/media coverage, drawable XML well-formedness, and Kotlin brace balance.

## Notes

- YouTube videos stream — tapping one needs internet; the app shows an offline notice otherwise. Everything else works fully offline.
- Logs live in app-private storage and are included in Android auto-backup; the GitHub repo carries program content only, never personal workout data.
- `primaryMuscles` was derived from standard exercise anatomy when the JSON was authored (the web source didn't carry muscle tags); everything else — names, sets/reps, videos, coaching text — is extracted verbatim from the validated web tracker data.
