# AGENTS.md (cli)

This file applies to `cli/`. Also read the repository-level `../AGENTS.md` for global rules.

## Scope
- Thin Kotlin/JVM CLI wrapper over `watermarker-jvm`.
- Main entry point: `src/main/kotlin/Main.kt`.

## Dependency and build flow
- `cli/` depends on local Maven publication from `watermarker/`.
- Always run this first when library changes are involved:
  - `cd ../watermarker && ./gradlew publishToMavenLocal`
- If dependency resolution fails for `watermarker-jvm`, publish local artifacts and retry.

## Dev environment and commands
- Use JDK 21 and the local wrapper from this folder.
- Commands:
  - `./gradlew ktlintCheck` (mandatory before finishing changes)
  - `./gradlew test`
  - `./gradlew shadowJar` — produces `build/libs/cli-<version>-all.jar`
  - `./gradlew run --args="--help"`
- Before finishing CLI changes, at minimum run `./gradlew ktlintCheck` and `./gradlew test`.

## Implementation conventions
- Keep CLI behavior thin and delegate watermarking logic to `watermarker-jvm`.
- Avoid duplicating core algorithm logic in CLI.
- Follow existing option/argument style in `Main.kt` and keep UX consistent.

## Testing and quality
- CLI testing is optional today, not mandatory.
- Add or adjust tests for changed CLI behavior when practical, especially for larger behavior changes.
- At minimum, validate the affected CLI flow manually or with the existing Gradle test/run commands.
- Keep diffs focused and avoid unrelated formatting churn.
- If user-facing CLI behavior changes, update docs under `docs/docs/03-usage/11-cli/`.
