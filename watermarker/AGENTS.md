# AGENTS.md (watermarker)

This file applies to `watermarker/`. Also read the repository-level `../AGENTS.md` for global rules.

## Scope
- Core Kotlin Multiplatform watermarking library (`commonMain`, `jvmMain`, `jsMain`).
- This is the canonical implementation used by both `cli/` and `webinterface/`.

## Dev environment and commands
- Use JDK 21 and the local wrapper from this folder.
- Main commands:
  - `./gradlew ktlintCheck`
  - `./gradlew allTests`
  - `./gradlew build` — canonical pre-finish check (runs `ktlintCheck` + tests).
  - `./gradlew publishToMavenLocal`
- If downstream projects are touched (`cli/`, `webinterface/`), publish locally first.

## Architecture map
- Core text algorithm:
  - `src/commonMain/kotlin/watermarkers/text/PlainTextWatermarker.kt`
  - `src/commonMain/kotlin/watermarkers/text/TextWatermarker.kt`
- Watermark/tag types:
  - `src/commonMain/kotlin/types/watermarks/`
- Value-based responses:
  - `src/commonMain/kotlin/types/responses/`
- JVM file integrations:
  - `src/jvmMain/kotlin/watermarkers/file/`
  - `src/jvmMain/kotlin/utils/FileHandling.kt`
- Platform-specific utils:
  - JVM: `src/jvmMain/kotlin/utils/`
  - JS: `src/jsMain/kotlin/utils/`

## Core algorithm constraints (do not break)
- `PlainTextWatermarker` must reject covers that already contain alphabet chars or separator char
  (alphabet: `\u2008`, `\u2009`, `\u202F`, `\u205F`; separator: `\u2004`).
- Capacity is driven by regular spaces only.
- Repeated embedding with separator delimiters is intentional robustness behavior.
- Keep JVM/JS/common behavior aligned so generated watermarks are interoperable.

## Implementation conventions
- Use `InnamarkTagBuilder` by default for new watermark creation flows.
- Use value-based error handling in library code (`Status`, `Result<T>`, `appendStatus`, `prependStatus`).
- For new custom events, subclass `Event.Warning` or `Event.Error`, set `source`, and implement `getMessage()`.
- Do not throw exceptions in core library paths unless there is an existing established exception boundary.

## File type support (JVM only)
- For new watermarkable file types:
  1. Implement `WatermarkableFile`.
  2. Implement matching `FileWatermarker`.
  3. Register extension in `SupportedFileType` in `src/jvmMain/kotlin/utils/FileHandling.kt`.
- Do not extend deprecated `ZipFileWatermarker`.

## Testing expectations
- `watermarker/` should always aim to be fully tested; this is the best-covered subproject in the repo.
- Before finishing watermarker changes, run from this folder:
  - `./gradlew build` (canonical; runs `ktlintCheck` + all tests)
  - or, for faster inner-loop iteration: `./gradlew ktlintCheck` plus `./gradlew jvmTest` /
    `./gradlew jsTest` / `./gradlew allTests`
- Add/update tests in the correct target and level:
  - `commonTest`, `jvmTest`, `jsTest`
  - unit/integration where appropriate
- Current JVM test layout mirrors production packages and then splits into `unitTest/` and
  `integrationTest/` directories, for example:
  - `src/jvmTest/kotlin/types/files/unitTest/`
  - `src/jvmTest/kotlin/types/files/integrationTest/`
  - `src/jvmTest/kotlin/watermarkers/file/unitTest/`
  - `src/jvmTest/kotlin/watermarkers/file/integrationTest/`
- Follow existing class naming patterns:
  - shared/general tests often use `*Test`
  - JVM-specific tests often use `*TestJvm`
  - integration tests use `*IntegrationTest` or `*IntegrationTestJvm`
- Follow existing test function naming style: lowercase names with underscores that describe
  operation, condition, and expected result, for example `addWatermark_valid_success`,
  `fromFile_invalidPath_error`, or `decode_encode_inversion`.
- Keep the current `Arrange` / `Act` / `Assert` structure used in existing tests.
- Use `src/jvmTest/resources/` for stable JVM test fixtures and reuse representative files from
  `samples/` for file-based scenarios when they already match the intended behavior.
- The `samples/` directory contains important text and zip examples for file-oriented watermarking
  cases; prefer reusing those scenarios over inventing new binary fixtures.
- Do not regenerate or modify existing binary fixtures unintentionally.
