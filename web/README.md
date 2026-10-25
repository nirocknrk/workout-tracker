# Workout Tracker

A portable, single-file workout tracker for a 3-day dumbbell home program. No build step, no backend, no account — just open `index.html` in a browser.

## What's inside

- **Program tab** — Day 1 (Push), Day 2 (Pull + Legs), Day 3 (Shoulders + Arms), each with one-tap **Start** buttons that preload that session's exercises, set counts and rep targets into the log.
- **Logging** — per-set weight + reps inputs, add/remove sets, one-handed mobile layout.
- **Form guides** — every exercise opens a guide with an embedded YouTube technique video (from a named coaching source), a "Watch on YouTube" fallback link, setup steps, tempo, dos and don'ts, common mistakes, and a training tip.
- **History tab** — every logged workout with per-exercise breakdown and delete controls.
- **Weekly summary** — unit-aware totals: loaded volume excludes timed holds (planks), which are tracked separately as time under tension; unilateral work (rows, split squats, twists) counts both sides.

## Data

Workouts are stored in the browser's `localStorage` under the key `wtg-logs-v1`. Nothing leaves the device. Clearing browser data will erase logs — export them first via your browser's dev tools if needed.

## Hosting

Because it's one self-contained file (all CSS/JS inline; YouTube embeds are the only external requests), you can host it anywhere static files work:

- **GitHub Pages**: push `index.html` to a repo, enable Pages in repo settings (deploy from branch), and open the published URL.
- Any static host (Netlify, Cloudflare Pages, Vercel) works the same way — just drop the file in.

## Program

Designed for adjustable dumbbells + an adjustable bench, 3 sessions/week:

- **Day 1 — Push**: Flat DB Bench Press 4×6–10, Incline DB Press 3×8–12, Standing DB Overhead Press 3×8–12, Lateral Raises 3×12–15, Overhead Triceps Extension 3×10–15, Hanging Knee/Leg Raises 3×12–15
- **Day 2 — Pull + Legs**: One-Arm DB Row 4×8–12/side, DB Romanian Deadlift 3×8–12, Bulgarian Split Squat 3×8–12/leg, Bent-Over Rear Delt Fly 3×12–15, DB Bicep Curls 3×10–12, Hammer Curls 2×10–12, Plank 3×45–60s
- **Day 3 — Shoulders + Arms**: Arnold Press 3×8–12, Close-Grip DB Press 3×10–12, Lateral Raises 4×12–15, Front Raises 2×12, Hammer Curls 3×10–12, Overhead Triceps Extension 3×10–12, Prone Y-Raises 2×12–15, Russian Twists 3×15/side
