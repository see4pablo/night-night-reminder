# Night Recaller

<p align="center">
  <img src="docs/icon.png" alt="Night Recaller icon" width="128" height="128" />
</p>

Nighttime Android app: speak what’s left to do and get a checklist.

> Anything left for tomorrow?

Example: *“brush teeth, pack bag, and charge phone”* → three tasks.

## Features

- Typography-first **home** (night mode) with a red mic
- Voice → checklist items (splits on commas, `and` / `y`, or magic word **`next`** / **`también`**)
- **Today** / **Past**: per-day lists, persisted locally
- Soft **20:00** ping with pending tasks (*“You have pending tasks · night night”*)
- Short wooden UI sounds (start / stop / items added)
- Stops alone if no speech starts within ~3 seconds

## Stack

- Kotlin + Jetpack Compose
- `SpeechRecognizer` (device / Google speech service)
- Local persistence (`SharedPreferences` + JSON)
- `AlarmManager` + social-style notifications (not an alarm)
- Nix (`shell.nix`) → JDK 17 + Android SDK

## Requirements

- Phone with Google speech recognition
- Microphone + notification permissions
- Nix (for the provided shell)

## Setup

```bash
cd night-night-reminder
nix-shell
./gradlew :app:assembleDebug
./gradlew :app:installDebug   # device with USB debugging
```

`nix-shell` writes `local.properties` with `sdk.dir` (gitignored).

On **Xiaomi / HyperOS** you often need:

1. USB debugging + **Install via USB**
2. **USB debugging (Security settings)**
3. **Media** volume up (UI sounds use that stream)

## Quick use

1. Tap the mic → dictate tasks (use *next* / *también* between items if needed)
2. Open **Today** to check / remove items
3. **Past** keeps earlier nights (includes an Oct 9 demo seed)
4. Home debug: **Try ping** / **Reschedule** (20:00 alert)

## Tests

```bash
nix-shell --run './gradlew :app:testDebugUnitTest'
```

Covers:

- `ChecklistParser` (EN/ES split, capitalization, dedupe, prefixes)
- `SeedData` (demo birthday date)
- `BedtimeScheduler.nextTriggerDateTime` (before/after 20:00)

## MVP

- [x] Voice → text → checklist
- [x] Magic word / basic multilingual connectors
- [x] Night Recaller home → Today / Past
- [x] Persist per day
- [x] 20:00 notification + debug
- [x] UI sounds + silence timeout
- [ ] Stronger multilingual parsing
- [ ] Configurable bedtime hour

## Package

`cl.see4pablo.nightnightreminder` · version `0.1.0`
