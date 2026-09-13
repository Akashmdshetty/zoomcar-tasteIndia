# TasteIndia — Development Notes & Architecture Decisions

This document details the architectural choices, SDK versions, library selections, and design decisions made for the **TasteIndia** Indian Cuisine Discovery Android Application.

---

## 1. SDK Versions Selected

- **`minSdk`**: `24` (Android 7.0 Nougat)
  - **Rationale**: Provides support for ~95%+ of active Android devices globally while giving access to modern Java 8+ / Kotlin language features and APIs without heavy desugaring overhead.
- **`compileSdk`**: `35` (Android 15)
  - **Rationale**: Aligns with modern Android 15 platform APIs and compiler requirements.
- **`targetSdk`**: `35` (Android 15)
  - **Rationale**: Ensures compliance with Google Play Store target API level guidelines and enables edge-to-edge support by default.

---

## 2. Architectural Approach

TasteIndia follows **Clean Architecture** principles combined with **Unidirectional Data Flow (UDF)**:

```
Compose UI (View)
       ↓ (User Actions)
   ViewModel (State Management)
       ↓ (Data Request)
   Repository (Single Source of Truth)
       ↓                 ↓
   Remote API        Local Persistence
 (Retrofit DTOs)     (DataStore)
```

### Layer Separation
- **`data`**:
  - `remote/dto`: API response data transfer objects. Annotated with `@Serializable` and isolated from domain models.
  - `local`: Local persistence abstractions (PreferencesDataStore).
  - `repository`: Repository implementations acting as the single source of truth, converting DTOs to clean domain models.
- **`domain`**:
  - `model`: Clean, business-centric Data Classes (`Recipe`). Zero dependencies on Android or third-party frameworks.
- **`ui`**:
  - `recipes`, `details`, `filters`, `favourites`: Screen composables and ViewModels separated by feature domain.
  - `theme`: Material 3 color schemes, typography, and theme definitions.
- **`navigation`**:
  - `NavGraph`: Navigation Compose route declarations and screen transitions.

---

## 3. Technology & Dependency Selection Justifications

### A. Networking: Retrofit + OkHttp
- **Selection**: `com.squareup.retrofit2:retrofit:2.11.0` + `com.squareup.okhttp3:logging-interceptor:4.12.0`
- **Why**: Retrofit is the industry standard Android HTTP client. It uses simple interface annotations (`@GET`, `@Query`), integrates seamlessly with Kotlin Coroutines (`suspend fun`), supports OkHttp interceptors for network logging/caching, and allows deterministic test doubles via MockWebServer or custom interfaces.

### B. Serialization: Kotlinx Serialization
- **Selection**: `org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3` + `retrofit2-kotlinx-serialization-converter:1.0.0`
- **Why**: Kotlinx Serialization is Kotlin-native, multiplatform compatible, and uses compile-time code generation (reflection-free). Unlike Moshi (which is in maintenance mode), Kotlinx Serialization is actively developed by JetBrains and officially recommended for modern Android development.

### C. Local Persistence: DataStore Preferences
- **Selection**: `androidx.datastore:datastore-preferences:1.1.1`
- **Why**: For storing user favourite meal IDs (a set of ID strings), DataStore Preferences provides a asynchronous, non-blocking key-value storage solution built on Kotlin Coroutines and `Flow`. It avoids the heavy multi-table database boilerplate, SQL migrations, and setup complexity of Room while offering full thread safety over legacy `SharedPreferences`.

### D. UI Framework: Jetpack Compose + Material 3
- **Selection**: Jetpack Compose BOM `2024.10.01` + Material 3
- **Why**: Declarative UI paradigm eliminating XML layout overhead. Material 3 (`androidx.compose.material3`) delivers modern design tokens, dynamic color support, clean typography, and full accessibility support out of the box.

### E. Navigation: Navigation Compose
- **Selection**: `androidx.navigation:navigation-compose:2.8.4`
- **Why**: Official Jetpack library for declarative navigation within Compose, providing seamless backstack management and parameter passing between screens.

### F. Asynchronous Programming: Kotlin Coroutines & StateFlow
- **Selection**: `kotlinx-coroutines-android` + `lifecycle-runtime-compose` (`collectAsStateWithLifecycle`)
- **Why**: Provides lifecycle-aware state collection in Compose without memory leaks or unnecessary background re-compositions when the screen is paused or stopped.

---

## 4. Testability & Dependency Injection Strategy

- Repositories and Data Sources are defined behind interfaces (`RecipeRepository`, `PreferencesManager`).
- Components receive dependencies via constructor injection.
- Allows deterministic unit testing by substituting fake data sources without requiring complex Dagger/Hilt code generation during early project setup.
