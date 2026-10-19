# Night Recaller

App Android nocturna: dictás lo que falta hacer y se convierte en una checklist.

> ¿Algo pendiente para mañana?

Ejemplo: *“cepillar dientes también cargar teléfono también empacar mochila”* → tres tareas.

## Qué hace

- **Home** tipográfica (modo noche) con micrófono rojo
- Dictado por voz → ítems (separa por comas, `y` / `and`, o palabra mágica **`también`**)
- **Hoy** / **Anteriores**: listas por día, persistidas
- Aviso suave a las **20:00** con pendientes (*“Tienes tareas pendientes · night night”*)
- Sonidos cortos de madera (empezar / parar / agregar ítems)
- Si no hay voz en ~3 s (sin empezar a hablar), corta solo

## Stack

- Kotlin + Jetpack Compose
- `SpeechRecognizer` (servicio de voz del dispositivo / Google)
- Persistencia local (`SharedPreferences` + JSON)
- `AlarmManager` + notificaciones estilo “social” (no alarma)
- Nix (`shell.nix`) → JDK 17 + Android SDK

## Requisitos

- Teléfono con reconocimiento de voz de Google
- Permisos: micrófono + notificaciones
- Nix (para el entorno del repo)

## Setup

```bash
cd night-night-reminder
nix-shell
./gradlew :app:assembleDebug
./gradlew :app:installDebug   # dispositivo con depuración USB
```

`nix-shell` genera `local.properties` con `sdk.dir` (gitignored).

En **Xiaomi / HyperOS** suele hacer falta:

1. Depuración USB + **Instalar vía USB**
2. **Depuración USB (ajustes de seguridad)**
3. Volumen de **medios** arriba (los sonidos UI usan ese stream)

## Uso rápido

1. Tocá el mic → dictá tareas (separá con *también* si hace falta)
2. Abrí **Hoy** para marcar / borrar
3. **Anteriores** guarda noches pasadas (incluye seed de demo del 9 oct)
4. Debug en home: **Probar aviso** / **Reprogramar** (alerta 20:00)

## Tests

```bash
nix-shell --run './gradlew :app:testDebugUnitTest'
```

Cubre:

- `ChecklistParser` (split EN/ES, mayúsculas, dedupe, prefijos)
- `SeedData` (fecha de cumpleaños demo)
- `BedtimeScheduler.nextTriggerDateTime` (antes/después de las 20:00)

## MVP

- [x] Voz → texto → checklist
- [x] Palabra mágica / conectores multilingües básicos
- [x] Home Night Recaller → Hoy / Anteriores
- [x] Persistencia por día
- [x] Notificación 20:00 + debug
- [x] Sonidos UI + timeout de silencio
- [ ] Parsing multilingüe más robusto
- [ ] Horario de bedtime configurable

## Paquete

`cl.see4pablo.nightnightreminder` · versión `0.1.0`
