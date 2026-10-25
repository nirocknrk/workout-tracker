# Workout Tracker

A personal workout tracker for a 3-day dumbbell program, built twice — as a **native Android app** and as a **portable single-file web app** — from one shared, JSON-driven program definition.

Built by [Niroshan Chandrawijayakumar](https://github.com/nirocknrk), Lead Software Engineer (mobile) based in Singapore.

## The two builds

| Folder | Stack | What it shows |
|---|---|---|
| [`android/`](android/) | Kotlin 2.0 · Jetpack Compose · Material 3 · kotlinx.serialization | Modern native Android architecture: offline-first, JSON-driven content, import/export bundles |
| [`web/`](web/) | Single self-contained `index.html` (no build step, no backend) | The same product as a zero-dependency web app — open it in any browser |

## Design idea: content as data

The entire program — schedule, exercises, set/rep targets, technique videos, coaching notes — lives in one `program.json`. Both apps render from that schema:

- Change the JSON and the app content changes — **no rebuild, no reinstall** (the Android app imports updated bundles at runtime and validates them before applying).
- Unit-aware logging: timed holds (planks) are tracked as time-under-tension and excluded from loaded volume; unilateral work counts both sides.
- Every exercise carries a full coaching guide: embedded technique video from a named source, setup steps, tempo, dos and don'ts, common mistakes, and a pro tip.

## Highlights

- **Offline-first** — the only network call in either build is opening a technique video.
- **Android:** minSdk 26, Compose UI with bottom-tab navigation, per-set logging (weight + reps/seconds), weekly stats, history, and ZIP bundle export/import with schema validation.
- **Web:** localStorage persistence, one-tap session start that preloads the day's exercises and targets, weekly summary with the same unit rules.
- 18 custom stroke-style exercise icons (SVG → Android VectorDrawables).

See each folder's README for architecture, the `program.json` schema, and build/hosting instructions.
