# AI Agent Architecture & Guidelines

This document outlines the essential patterns, architectural guardrails, and conventions for AI agents working in this Kotlin Multiplatform project.

## Project Structure
- **/composeApp**: Main frontend for Android, iOS, and Desktop built with Compose Multiplatform. Core logic lives in `composeApp/src/commonMain/kotlin`.
- **/server**: Ktor-based backend server providing the REST API (`server/src/main/kotlin`).
- **/shared**: DTOs and API endpoints shared between client and server (`shared/src/commonMain/kotlin`).
- **/iosApp**: Xcode boilerplate entry point for the iOS platform.

## Architecture & Conventions

### Client (Compose Multiplatform)
- **MVI Pattern**: All presentation layers follow a strict Model-View-Intent flow. For any new screen:
  - Define an immutable `State` data class (e.g., `SearchState`).
  - Define a sealed `Action` class for UI intents (e.g., `SearchAction`).
  - Handle actions in the ViewModel strictly through a single `fun onAction(action: Action)` function. (Refer to `SearchViewModel.kt` for a complete example).
- **Reactive State Construction**: Prefer composing View State dynamically from multiple internal private flows via `combine(...)` and `.stateIn(...)` instead of imperatively mutating a single state object.
- **Clean Architecture Layers**: Code is split into `core/data` (Room DB, DAOs, Ktor Client), `core/domain` (Models, Repository Interfaces), and `core/presentation` (Screens, ViewModels).
- **Result & Error Handling**: Domain operations return custom type-safe `Result<T, DataError>` (from `core/domain/errorHandling`). React to outputs using the `.onSuccess { }` and `.onError { }` extension functions.

### Server (Ktor Backend)
- **Routing**: API routes are mounted under `/v2` and protected with bearer authentication. Route groups live in `routes/` and are registered in `plugins/Routing.kt`.
- **Database**: Data mapping relies on JetBrains Exposed ORM. Define tables using singletons derived from `Table` (e.g., `RecipesTable.kt`).
- **Dependency Injection**: Utilize Koin dynamically resolving service interfaces inside `configureRouting` (e.g., `val recipeService by inject<RecipeService>()`).

### Shared Contracts
- Define models that transit the network as `Dto` classes within `/shared/.../dto`.
- Write explicit mapping functions translating DTOs to Domain abstractions and vice versa (e.g., `RecipeMappers.kt` in `composeApp`).

## Key Dev Workflows
All main builds run through the Gradle wrapper in the project root:
- **Android**: `./gradlew :composeApp:assembleDebug`
- **Desktop JVM**: `./gradlew :composeApp:run`
- **Server**: `./gradlew :server:run`

