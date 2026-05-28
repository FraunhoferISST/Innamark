# AGENTS.md (webinterface)

This file applies to `webinterface/`. Also read the repository-level `../AGENTS.md` for global rules.

## Scope
- Kotlin/JS KVision frontend consuming multiplatform `watermarker` artifacts.
- Main UI code lives in `src/jsMain/kotlin/`.

## Dependency and build flow
- `webinterface/` depends on local `watermarker` publication.
- Precondition when library changes are involved:
  - `cd ../watermarker && ./gradlew publishToMavenLocal`

## Dev environment and commands
- Use JDK 21 and the local wrapper from this folder.
- Run from `webinterface/`:
  - `./gradlew ktlintCheck` (mandatory before finishing changes)
  - `./gradlew test`
  - `./gradlew -t run`
  - `./gradlew clean zip`
- Docker path validated in CI:
  - `docker build -t innamark-webinterface -f webinterface/Dockerfile .`
- Newcomer fast-start from repo root:
  - `docker compose up` using `../docker-compose.yml` (builds and serves the webinterface path).
- Before finishing changes, at minimum run `./gradlew ktlintCheck` and `./gradlew test`.

## UI behavior constraints
- Keep the two active text tabs functionally correct:
  - Embed tab lets users check if a watermark fits the cover text, choose `InnamarkTag`
    configurations (for example CRC32, SHA3, compression/size), and embed the watermark into
    the cover text.
  - Extract tab lets users extract watermark content from an existing watermarked cover text.
- Preserve capacity logic based on `PlainTextWatermarker.getMinimumInsertPositions(...)`.
- Keep the tab-specific UX with separate text input/textarea fields for embed and extract flows.
- Keep behavior aligned with library constraints (space-based capacity, alphabet/separator handling).

## Versioning caution
- Do not casually change pinned Kotlin/KVision versions in `gradle.properties`.
- KVision is version-sensitive to Kotlin; upgrades should be explicit and validated.

## Testing and docs
- Webinterface testing is optional today, not mandatory.
- Add or update JS tests for behavior changes when practical, especially for non-trivial UI logic.
- At minimum, validate changed flows in the dev server or docker-based fast-start path when feasible.
- If user-facing UI behavior changes, update docs under `docs/docs/03-usage/12-webinterface/`.
