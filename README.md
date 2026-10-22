# Sleepy Goals

<p align="center">
  <img src="docs/icon.png" alt="Sleepy Goals icon" width="128" height="128" />
</p>

Nighttime Android journal: speak what’s left to do and watch your diary open with tonight’s goals.

> Anything left for tomorrow?

Example: *“brush teeth, pack bag, and charge phone”* → three lines in the journal.

## Features

- **Closed black diary** while you speak into the mic
- **Opens onto paper** when tonight’s items are transcribed
- Voice → checklist (commas, `and` / `y`, magic word **`next`** / **`también`**)
- **Past** nights, persisted locally
- Soft **20:00** ping (*“You have pending tasks · night night”*)
- Wooden UI sounds; silence timeout (~3s if speech never starts)

## Stack

- Kotlin + Jetpack Compose
- `SpeechRecognizer`
- `SharedPreferences` + JSON
- `AlarmManager` + social-style notifications
- Nix (`shell.nix`) → JDK 17 + Android SDK

## Setup

```bash
cd sleepy-goals
nix-shell
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

On **Xiaomi / HyperOS**: enable Install via USB + USB debugging (Security settings); keep **media** volume up for sounds.

## Tests

```bash
nix-shell --run './gradlew :app:testDebugUnitTest'
```

## Package

`cl.see4pablo.nightnightreminder` · app label **Sleepy Goals** · version `0.1.0`
