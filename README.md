# Night Night Reminder

Android MVP: speak a short list before bed, get a checklist.

Example: *“brush teeth, pack bag, and charge phone”* → three checkable tasks.

## Stack

- Kotlin + Jetpack Compose
- Android `SpeechRecognizer` (on-device / Google speech service)
- Simple local parser (splits on `and` / `y` / commas)
- Nix `shell.nix` for JDK + Android SDK

## Requirements

- Phone or emulator with Google speech recognition
- Microphone permission
- Nix (for the provided shell)

## Setup

```bash
cd night-night-reminder
nix-shell
./gradlew :app:assembleDebug
./gradlew :app:installDebug   # device/emulator connected
```

`nix-shell` writes `local.properties` with `sdk.dir` (gitignored).

First `nix-shell` / Gradle run may download the SDK and dependencies — that can take a while.

## MVP scope

- [x] Voice → text
- [x] Split into checklist items
- [x] Check / uncheck / delete
- [x] Clear done / clear all
- [ ] Persist list across app restarts
- [ ] Notifications / bedtime schedule
- [ ] Better multilingual parsing

## Package

`cl.see4pablo.nightnightreminder`
