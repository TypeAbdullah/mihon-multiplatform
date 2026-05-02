# Mihon / MihonKMP

## Projects in This Repository

### 1. Original Mihon (Android-only) — `/` root
The upstream Mihon Android app. A native Android manga reader built with Kotlin + Jetpack Compose.
Not runnable in Replit (requires Android SDK + device/emulator).

### 2. MihonKMP — `/kmp/`
A new, from-scratch Kotlin Multiplatform boilerplate targeting **Android, iOS, Windows, and Linux**
using Compose Multiplatform. This is the actively developed KMP architecture.

---

## KMP Architecture (`/kmp/`)

### Tech Stack
| Concern | Library | Version |
|---|---|---|
| Shared UI | Compose Multiplatform | 1.7.0 |
| Navigation | Voyager (+ ScreenModel) | 1.1.0-beta03 |
| DI | Koin Multiplatform | 4.0.0 |
| Networking | Ktor | 3.0.1 |
| Local DB | SQLDelight | 2.0.2 |
| Images | Coil3 | 3.0.4 |
| File I/O | Okio | 3.9.1 |
| Concurrency | Kotlin Coroutines + Flow | 1.9.0 |
| Serialization | kotlinx.serialization JSON | — |

### Module Layout
```
kmp/
├── settings.gradle.kts          # Includes :shared, :androidApp, :desktopApp
├── build.gradle.kts             # Root plugin declarations
├── gradle.properties            # JVM args, config cache, parallel builds
├── gradle/
│   ├── libs.versions.toml       # Central version catalog
│   └── wrapper/
│       └── gradle-wrapper.properties  # Gradle 8.10
│
├── shared/                      # Shared KMP module (all business logic + UI)
│   ├── build.gradle.kts         # Configures androidTarget, iosX64/Arm64/Simulator, jvm("desktop")
│   └── src/
│       ├── commonMain/
│       │   ├── kotlin/app/mihon/shared/
│       │   │   ├── domain/
│       │   │   │   ├── model/Manga.kt           # Pure domain data class + MangaStatus enum
│       │   │   │   └── repository/MangaRepository.kt  # Interface (no impl details)
│       │   │   ├── data/
│       │   │   │   ├── db/
│       │   │   │   │   ├── DatabaseDriverFactory.kt   # expect class
│       │   │   │   │   ├── MangaDatabaseHelper.kt     # Wraps generated SQLDelight queries
│       │   │   │   │   └── Extensions.kt              # Flow helpers for SQLDelight
│       │   │   │   ├── network/
│       │   │   │   │   ├── MangaHttpClient.kt         # expect fun httpClientEngineFactory()
│       │   │   │   │   └── dto/MangaDto.kt            # API DTO + toDomain() mapper
│       │   │   │   └── repository/MangaRepositoryImpl.kt
│       │   │   ├── filesystem/
│       │   │   │   └── AppFileSystem.kt               # expect object
│       │   │   ├── presentation/
│       │   │   │   ├── library/
│       │   │   │   │   ├── LibraryScreen.kt           # Voyager Screen, adaptive LazyVerticalGrid
│       │   │   │   │   └── LibraryScreenModel.kt      # Voyager ScreenModel (ViewModel equiv)
│       │   │   │   └── theme/MihonTheme.kt            # Material3 light/dark theme
│       │   │   └── di/AppModule.kt                    # Single Koin module
│       │   └── sqldelight/app/mihon/shared/data/db/
│       │       └── Manga.sq                           # DDL + typed queries
│       │
│       ├── androidMain/         # Android actuals
│       │   ├── DatabaseDriverFactory.android.kt  → AndroidSqliteDriver
│       │   ├── HttpClientEngine.android.kt        → OkHttp
│       │   └── AppFileSystem.android.kt           → getExternalFilesDir
│       │
│       ├── iosMain/             # iOS actuals
│       │   ├── DatabaseDriverFactory.ios.kt       → NativeSqliteDriver
│       │   ├── HttpClientEngine.ios.kt            → Darwin (NSURLSession)
│       │   ├── AppFileSystem.ios.kt               → NSApplicationSupportDirectory
│       │   └── MainViewController.kt              # Entry point called from Swift
│       │
│       └── desktopMain/         # Desktop (Windows + Linux) actuals
│           ├── DatabaseDriverFactory.desktop.kt   → JdbcSqliteDriver
│           ├── HttpClientEngine.desktop.kt        → Apache5
│           └── AppFileSystem.desktop.kt           → %APPDATA% / ~/.local/share
│
├── androidApp/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── kotlin/app/mihon/android/
│       │   ├── MihonApplication.kt   # startKoin with androidContext
│       │   └── MainActivity.kt       # setContent → MihonTheme → Navigator(LibraryScreen)
│       └── res/values/
│           ├── strings.xml
│           └── themes.xml
│
└── desktopApp/
    ├── build.gradle.kts             # compose.desktop config, package targets (MSI, DEB)
    └── src/desktopMain/kotlin/app/mihon/desktop/
        └── main.kt                  # startKoin → application { Window { Navigator } }
```

### Clean Architecture Layers
```
UI (LibraryScreen)
   ↓ observes StateFlow
ScreenModel (LibraryScreenModel)
   ↓ calls
Repository Interface (MangaRepository)        ← domain layer boundary
   ↓ implemented by
MangaRepositoryImpl                           ← data layer
   ├── MangaHttpClient (Ktor)                 ← remote source
   └── MangaDatabaseHelper (SQLDelight)       ← local cache
```

### Platform Engine Selection
| Platform | HTTP Engine | DB Driver | Downloads Path |
|---|---|---|---|
| Android | OkHttp | AndroidSqliteDriver | `getExternalFilesDir("downloads")` |
| iOS | Darwin (NSURLSession) | NativeSqliteDriver | `Application Support/MihonKMP/downloads` |
| Windows | Apache5 | JdbcSqliteDriver | `%APPDATA%\MihonKMP\downloads` |
| Linux | Apache5 | JdbcSqliteDriver | `~/.local/share/MihonKMP/downloads` |

### Key Design Constraints Honoured
- **No Android imports in commonMain** — `Context`, `Toast`, `Bitmap` never appear outside `androidMain`
- **expect/actual** used for `DatabaseDriverFactory`, `httpClientEngineFactory()`, `AppFileSystem`
- **Adaptive grid** — `GridCells.Adaptive(160.dp)` automatically scales columns: 2 on phones → 8 on wide desktops
- **Mouse-scroll on Desktop** — `LazyVerticalGrid` delegates to Swing scroll physics on JVM, works out of the box
