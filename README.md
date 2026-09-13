# TasteIndia — Indian Cuisine Discovery Android Application

TasteIndia is a modern, responsive, and reliable native Android application built using Kotlin, Jetpack Compose, and Material 3 to discover, filter, search, and save authentic Indian recipes powered by **TheMealDB API**.

---

## Table of Contents
1. [Project Description](#project-description)
2. [Key Features](#key-features)
3. [Android Toolchain & Tech Stack](#android-toolchain--tech-stack)
4. [Clean Architecture & Package Structure](#clean-architecture--package-structure)
5. [Navigation Route Map](#navigation-route-map)
6. [TheMealDB API Endpoints Used](#themealdb-api-endpoints-used)
7. [Filtering & Indian-Boundary Strategy](#filtering--indian-boundary-strategy)
8. [Cache Policy & Request Deduplication](#cache-policy--request-deduplication)
9. [Persistence Strategy](#persistence-strategy)
10. [Error Handling Approach](#error-handling-approach)
11. [Accessibility & Design Considerations](#accessibility--design-considerations)
12. [Testing Instructions](#testing-instructions)
13. [How to Build & Run](#how-to-build--run)
14. [Assumptions](#assumptions)
15. [Tradeoffs](#tradeoffs)
16. [Known Issues](#known-issues)
17. [Approximate Time Spent](#approximate-time-spent)
18. [AI / Tool-Use Disclosure](#ai--tool-use-disclosure)
19. [Attribution](#attribution)
20. [Demo Recording Guide](#demo-recording-guide)

---

## 1. Project Description
TasteIndia allows users to browse an extensive catalog of Indian meals retrieved directly from **TheMealDB API**. Users can perform instant local searches, apply category and ingredient filters, sort recipes alphabetically, save favourite meals offline, and view comprehensive recipe details including normalized ingredients, step-by-step instructions, YouTube video links, and source URLs.

---

## 2. Key Features
- **Authoritative Indian Recipe List**: Displays authentic Indian recipes fetched via `filter.php?a=Indian`.
- **Meal-Name Search**: Real-time, case-insensitive search preserved across navigation.
- **Category & Ingredient Filters**: Dynamic selection dropdowns displaying categories and ingredients.
- **Strict Indian-Boundary Intersection**: Guarantees non-Indian items returned by global category/ingredient filters are strictly excluded.
- **Active Filter Chips & Clear All**: Visual indicators for active filters with single-click removal.
- **Alphabetical Sorting**: Interactive A–Z and Z–A sorting.
- **Recipe Details View**: Complete recipe view featuring hero images, area tags, normalized ingredients (ingredient + measurement pairs), step-by-step instructions, and external video/source links.
- **Persistent Favourites**: Save and remove favourite recipes using DataStore Preferences; works completely offline.
- **Navigation & State Restoration**: Restores search query, filter selections, sort order, and scroll position when returning from recipe details.
- **In-Memory Detail Cache & Request Deduplication**: Repository-level caching and in-flight request deduplication to prevent redundant network calls and N+1 requests.
- **Comprehensive Offline Test Suite**: 7 unit test classes with offline JSON response fixtures covering all core business logic.

---

## 3. Android Toolchain & Tech Stack
- **Compile SDK**: `34`
- **Target SDK**: `34`
- **Min SDK**: `24`
- **Language**: Kotlin 1.9.0
- **UI Framework**: Jetpack Compose with Material 3 (M3)
- **Networking**: Retrofit 2 + OkHttp 4
- **Serialization**: KotlinX Serialization
- **Image Loading**: Coil Compose (`AsyncImage`)
- **Async Execution**: Kotlin Coroutines & Flow (`StateFlow`, `combine`)
- **Local Persistence**: Jetpack DataStore Preferences

---

## 4. Clean Architecture & Package Structure
The app adheres to Clean Architecture principles separating responsibilities into distinct packages:

```
com.example.tasteindia
├── data
│   ├── local          # DataStore Preferences Manager for persistent favourites
│   ├── remote         # RetrofitClient, MealApi interface, DTOs, and SafeApiCall wrapper
│   │   └── dto        # Response DTOs (MealFilterResponseDto, MealDetailsResponseDto, etc.)
│   └── repository     # DefaultMealRepository, MealFilterEngine, and IngredientNormalizer
├── domain
│   └── model          # Domain models (Meal, MealDetails, Ingredient, FilterState, Result)
├── navigation         # NavGraph, NavRoutes, and BottomNavigation
└── ui
    ├── components     # Shared UI components
    ├── details        # Recipe Details Screen & ViewModel
    ├── favourites     # Favourites Screen & ViewModel
    ├── recipes        # Recipes List Screen, ViewModel, and FilterBar
    └── theme          # Material 3 Color palette, Typography, and Theme setup
```

### Architectural Data Flow:
```
UI (Composable) ──(Collects StateFlow)──> ViewModel ──(Calls suspend/Flow)──> Repository ──> Retrofit API / DataStore
```

---

## 5. Navigation Route Map
Navigation is managed via Jetpack Navigation Compose using type-safe route strings. Stable `mealId` strings are passed through routes rather than entire objects.

```
                  ┌────────────────────────┐
                  │   RecipesScreen ("recipes") │
                  └───────────┬────────────┘
                              │
                    Click Recipe (mealId)
                              │
                              ▼
               ┌────────────────────────────────┐
               │ RecipeDetailsScreen            │
               │ ("recipe_details/{mealId}")    │
               └──────────────┬─────────────────┘
                              │
                    Click Back Button
                              │
                              ▼
                  ┌────────────────────────┐
                  │   RecipesScreen ("recipes") │
                  │ (State & Scroll Preserved) │
                  └────────────────────────┘
```

---

## 6. TheMealDB API Endpoints Used
All API calls are executed against TheMealDB Free Tier API (`https://www.themealdb.com/api/json/v1/1/`):

1. `filter.php?a=Indian` — Retrieves list of all Indian recipes.
2. `lookup.php?i={mealId}` — Retrieves detailed instructions, ingredients, and links for a specific meal ID.
3. `filter.php?c={category}` — Retrieves list of meals matching a category.
4. `filter.php?i={ingredient}` — Retrieves list of meals containing an ingredient.
5. `list.php?c=list` — Retrieves available category list.
6. `list.php?i=list` — Retrieves available ingredient list.

---

## 7. Filtering & Indian-Boundary Strategy
The core business rule requires that **TasteIndia remain an Indian cuisine discovery application**.

The collection returned from `filter.php?a=Indian` is the **authoritative base set**.

When category or ingredient filters are selected, `MealFilterEngine` computes the set intersection of allowed meal IDs:

$$\text{Allowed Meals} = \text{INDIAN\_IDS} \cap \text{CATEGORY\_IDS} \cap \text{INGREDIENT\_IDS}$$

Search, favourites-only, and sorting (A–Z / Z-A) are then applied to the resulting domain collection. Non-Indian meals returned by global filter endpoints are strictly excluded.

---

## 8. Cache Policy & Request Deduplication
- **In-Memory Detail Cache**: `DefaultMealRepository` maintains a thread-safe `ConcurrentHashMap<String, MealDetails>`. When details for a meal ID are requested multiple times (e.g. reopening a recipe), the cached instance is returned instantly without a network call.
- **Request Deduplication**: Concurrent requests for the same meal ID share an in-flight `Deferred<Result<MealDetails>>` managed via Kotlin `Mutex`, preventing duplicate network calls.
- **Zero Uncontrolled N+1**: Recipe details are requested on-demand when opening `DetailScreen`, never per visible list row.

---

## 9. Persistence Strategy
- **DataStore Preferences**: Favourite recipe IDs are stored asynchronously as a `Set<String>` in `taste_india_prefs`.
- **Offline Availability**: Favourites operate independently of network availability. The app reads and updates favourite IDs offline.

---

## 10. Error Handling Approach
- **Safe API Call Wrapper**: Network requests are wrapped in `safeApiCall {}` returning a sealed `NetworkResult` (Handling HTTP errors, timeouts, network connectivity loss, and serialization failures).
- **Domain Result Types**: Sealed `Result.Success` and `Result.Error` communicate status to ViewModels without exposing raw exception tracebacks to users.
- **UI Error & Retry**: Clean Material 3 error messages with explicit "Retry" buttons enable immediate recovery.

---

## 11. Accessibility & Design Considerations
- **Content Descriptions**: Explicit semantic content descriptions for interactive buttons (e.g., `"Add Chicken Tikka to favourites"`, `"Remove Dal Makhani from favourites"`, `"Navigate back"`).
- **Dynamic Font Scaling**: Text styles use Material 3 scaleable typography (`sp`), preventing layout clipping under larger system font settings.
- **Color & State Contrast**: Selected filter chips and toggle states use Material 3 primary containers, ensuring high contrast in both Light and Dark themes.
- **Distinct Empty States**:
  - *No recipes available* (Network error / zero API payload)
  - *No recipes match your filters* (Filter bar restriction)
  - *No favourite recipes saved yet* (Empty favourites tab)

---

## 12. Testing Instructions
The test suite consists of 7 unit test classes operating 100% offline using deterministic JSON fixtures (`src/test/resources/`):

To run all unit tests:
```powershell
.\gradlew.bat test
```

### Test Coverage:
1. `MealFilterEngineTest`: Validates search, intersection logic, favourites filter, and A–Z/Z-A sorting.
2. `FilterIntersectionTest`: Guarantees strict Indian boundary set intersection ($\text{INDIAN\_IDS} \cap \text{CATEGORY\_IDS} \cap \text{INGREDIENT\_IDS}$).
3. `IngredientNormalizationTest`: Verifies DTO parsing, whitespace trimming, blank/null pair omission, and domain mapping.
4. `LatestStateTest`: Verifies rapid search/filter state updates always reflect the latest `FilterState`.
5. `FavouritesPersistenceTest`: Verifies offline add/remove/read operations for favourite IDs.
6. `NavigationStateRestorationTest`: Verifies `FilterState` preservation across navigation.
7. `MealRepositoryTest`: Verifies repository API calls and mock responses.

---

## 13. How to Build & Run

### Prerequisites:
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or JDK 21
- Android device or emulator running Android 7.0 (API level 24) or higher

### Build Instructions:
1. Clone the repository:
   ```bash
   git clone https://github.com/Akashmdshetty/zoomcar-tasteIndia.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle project files.
4. Build the debug APK via terminal:
   ```powershell
   .\gradlew.bat assembleDebug
   ```
5. Run the app on your emulator or physical device.

---

## 14. Assumptions
- TheMealDB Free Tier API endpoints remain accessible at `https://www.themealdb.com/api/json/v1/1/`.
- Indian cuisine is identified by `strArea = "Indian"` or `filter.php?a=Indian`.

---

## 15. Tradeoffs
- **Local Search over API Search**: Local search against the pre-loaded Indian meal collection was selected instead of `search.php?s=` to eliminate search latency and avoid returning non-Indian meals.
- **In-Memory Detail Cache vs Persistent Database**: In-memory caching for details was selected to keep the architecture clean and lightweight for an intern assignment, while persistent DataStore was used for user favourites.

---

## 16. Known Issues
- TheMealDB Free Tier returns up to 20 ingredient/measure fields (`strIngredient1`...`20`). Recipes requiring more than 20 ingredients are truncated by the API source itself.

---

## 17. Approximate Time Spent
- **Milestones 1–4 (Setup, Architecture, Repository, API Integration)**: ~3 hours
- **Milestones 5–10 (Recipes List, Details, Ingredients Normalization, Favourites)**: ~4 hours
- **Milestones 11–14 (Search, Category/Ingredient Filters, Intersection, Sorting)**: ~3 hours
- **Milestones 15–20 (State Preservation, Cache, Deduplication, Tests, Polish, Docs)**: ~4 hours
- **Total Time**: ~14 hours

---

## 18. AI / Tool-Use Disclosure
This application was developed with the assistance of Google Antigravity AI coding agent. AI assistance was utilized for:
- Initial project scaffold planning and Clean Architecture design recommendations.
- Refactoring `MealFilterEngine` pure set intersection logic and unit test fixtures.
- Formatting comprehensive documentation and verification checklists.

All AI-generated suggestions and code modifications were thoroughly reviewed, adapted, compiled, and tested offline by the developer.

---

## 19. Attribution
- **Data Provider**: [TheMealDB](https://www.themealdb.com/) for providing recipe data and media assets.
- **Third-Party Libraries**:
  - [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) — HTTP networking.
  - [Coil](https://coil-kt.github.io/coil/) — Image loading for Compose.
  - [KotlinX Serialization](https://github.com/Kotlin/kotlinx.serialization) — JSON parsing.

---

## 20. Demo Recording Guide (2–4 Minutes)
When recording the submission video:
1. **Launch App**: Show initial loading indicator and Indian recipes list loading (`filter.php?a=Indian`).
2. **Search**: Type `"Chicken"` in search bar -> show instant filtered results.
3. **Category & Ingredient Filters**: Select Category `"Chicken"` and Ingredient `"Garlic"` -> point out active filter chips.
4. **Result Count & Clear All**: Highlight the `"Showing X recipes"` counter, then click **Clear All**.
5. **Open Recipe Details**: Tap `"Butter Chicken"` -> point out hero image, tags, normalized ingredients list, instructions, and video/source buttons.
6. **Favourite Recipe**: Tap heart icon in top bar -> show toast notification.
7. **Favourites Screen**: Navigate to Favourites tab -> show saved recipe working offline.
8. **State Restoration**: Open details from Favourites, tap Back -> point out that scroll position and filters remain intact.
