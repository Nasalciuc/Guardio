# AI Coding Agent Instructions for this Repository

Focus: Native Android (Kotlin, Jetpack Compose) security scanning app (frontend only so far) with planned FastAPI + PocketBase backend (not yet in repo). Many UI components are presently MOCK/STUB implementations awaiting real networking & persistence.

## 1. Current State vs Planned Architecture
- Present code = Android UI skeleton + mock data (`SecurityMockData`, placeholder scan/news/report flows). No backend sources exist yet.
- Planned backend: FastAPI services (`/scan`, `/scan/email`, `/news`, `/report`) aggregating VirusTotal, Google Safe Browsing, URLScan.io, HaveIBeenPwned, RSS feeds; persistence via PocketBase collections: `scans`, `email_checks`, `reports` (see `.kiro/specs/...`).
- Treat current UI components as integration targets: replace mock providers with Repository layer calling Retrofit services once added.

## 2. Module & Navigation Structure
- Single module `app` using Compose. Entry: `MainActivity` -> `MainApp()` -> `Scaffold` with bottom bar (`BottomNavigationComponent`).
- Navigation is manual via a `sealed class Screen` in `navigation/AppNavigation.kt`; not using Jetpack Navigation library. Maintain this pattern unless explicitly replaced. To add a screen: extend `Screen`, add branch in `AppNavigation`, map bottom nav index if needed.

## 3. UI / State Patterns
- Stateless UI components expect data + callbacks (e.g. `SearchResultsComponent`, `FileUploadComponent`). Internal transient UI interactions use `remember { mutableStateOf(...) }` only; no ViewModels yet.
- Color/status logic centralized in models helper (`SecurityMockData.getStatusColor`, etc.) and component-specific functions (`getStatusColor` in `SearchResultsComponent`); consolidate to a single utility when introducing real domain layer.
- Mock lists (files, urls, articles) = source of truth; replace with repository outputs preserving data shapes to minimize refactors.

## 4. Data Models (Frontend)
Key data classes: `SecurityFile`, `SecurityUrl`, `SecurityArticle` plus enums `FileStatus`, `UrlStatus`, `ArticleSeverity`, `FileType` in `data/SecurityModels.kt`.
Planned but missing (add before API wiring): `ScanResult`, `BreachResult`, `Report` (see design doc). Add in same file or new `SecurityDomainModels.kt` and keep naming consistent with backend Pydantic plans.

## 5. Implementation Priorities (from tasks backlog)
1. Add domain models + Retrofit interfaces.
2. Introduce Repository pattern (e.g. `SecurityRepository`) mediating: network -> local cache (Room planned) -> UI.
3. Replace mock calls inside components with hoisted state (lift data to a screen-level container composable or future ViewModel).
4. Add ViewModels per screen once logic grows (e.g. `SecurityViewModel`). Use Kotlin coroutines + Flow.

## 6. Conventions & Decisions
- Dependencies managed via `gradle/libs.versions.toml` (version catalogs). Add libraries by editing `libs.versions.toml` then reference with `libs.` alias in module `build.gradle.kts`.
- Compose Material3 baseline; extended icons added explicitly (`material-icons-extended`). Keep UI additions consistent with Material3 components.
- Navigation intentionally simple (no back stack). If deep links / back handling are needed, introduce Jetpack Navigation only after confirming requirement.
- Status semantics:
  SAFE (green), UNSAFE (red), SCANNING (amber), ERROR (gray). Maintain consistent mapping when integrating backend verdicts (design doc: verdicts safe/suspicious/malicious -> map malicious->UNSAFE, suspicious->SCANNING or new enum member `SUSPICIOUS`).

## 7. Planned Backend Contract (Guidance for Stubs)
Define Retrofit interfaces early (even before backend exists) to unblock UI:
- POST /scan -> returns aggregated verdict + raw API metadata.
- POST /scan/email -> breach status + list.
- GET /news -> list of Article objects (align with `SecurityArticle`).
- POST /report -> report id/confirmation.
Return wrappers should include `error, message, code` fields per design error format.

## 8. Where to Insert New Code
- Retrofit API interfaces: `data/remote/` (create package).
- Repository: `data/repository/`.
- ViewModels: `ui/viewmodel/`.
- Domain models (new): extend `data/` keeping existing file or separate domain file; avoid breaking current imports.
- Caching (future Room): create `data/local/` with DAOs & entities mirroring domain models (prefix `Entity`).

## 9. Testing Approach (Incremental Now)
- Existing test dirs are empty. When adding logic:
  - Put unit tests for repositories & mappers under `app/src/test/...`.
  - Use fake implementations instead of instrumentation until Android-specific APIs needed.
  - UI tests for composables: use `androidx.compose.ui.test` for screen-level interactions (e.g., verifying scan result color badges).

## 10. Security & Privacy Hooks
- Do NOT hardcode API keys; expect injection via gradle properties or local `local.properties`. Ensure any placeholder constants are clearly marked `// TODO secure`.
- Plan email hashing (SHA-256) before sending to backend if requirement changes to client-side hashing; currently described as backend responsibility.

## 11. Build & Run
- Standard Gradle wrapper; compile/target SDK 36, Kotlin 2.0.21, Java 11.
- Common commands:
  ./gradlew assembleDebug
  ./gradlew test
  ./gradlew connectedAndroidTest (when device/emulator available)

## 12. Incremental Migration Steps Example
When implementing real scan flow:
1. Add `ScanApi` with `suspend fun scan(request: ScanRequest): ScanResponse`.
2. Add `SecurityRepository.scan(content)` mapping response verdict -> `FileStatus` / new model.
3. Introduce `SecurityViewModel` holding `StateFlow<List<SearchResult>>`.
4. Update `SearchResultsComponent` to accept state from caller; remove internal mock generator.
5. Delete now-unused mock entries selectively (keep until parity reached).

## 13. Pitfalls / Watchouts
- Manual navigation currently loses transient state when switching screens; ViewModel introduction will stabilize.
- Mock progress animations (`UploadProgressComponent`) simulate scanning—ensure replacement uses real coroutine updates not busy loops.
- Color hex strings in `SecurityMockData` vs `Color` objects elsewhere—normalize during refactor to avoid duplication.

## 14. Minimal Style for Contributions
- Keep composables small & single‑purpose; move transformation logic out of UI.
- Prefer sealed classes for UI states (e.g., `ScanUiState`) once asynchronous flows are added.
- Avoid premature introduction of DI (Hilt) until first repository + ViewModel are in place.

## 15. Next Suggested Automations for Agents
- Generate domain models + Retrofit interfaces (stubs) with TODOs.
- Scaffold repository + basic ViewModel using mock data adapter.
- Write first unit test for status mapping logic.

(Provide feedback if any section needs clarification or if backend code will be added so instructions can evolve.)

---

## 16. Operational Constraints for AI Agents (IMPORTANT)
To preserve signal and repo quality:
1. Run only a small number of well‑reasoned commands (build, test, lint) that are strictly required to satisfy the user request. Avoid exploratory mass commands.
2. Do NOT create large batches of trivial or speculative files. Every new file must have a clear, immediate purpose tied to an explicit requirement.
3. Prefer minimal, surgical diffs over sweeping refactors. Do not reformat untouched code or churn imports unless essential.
4. Group related edits into as few patches as practical (while staying readable) instead of many micro commits.
5. When unsure, pause and request clarification rather than generating placeholder “slop” code.
6. Absolutely avoid generating dozens of near‑duplicate stubs—implement only what’s referenced by current UI or documented contracts.

Violations (excessive edits/commands) should be treated as task failure; agents must self‑audit before applying patches.
