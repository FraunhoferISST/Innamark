# GitHub Copilot Instructions — Innamark

Primary audience: GitHub Copilot. Other agents should follow `AGENTS.md` and the closest
subproject `AGENTS.md`; reading this file is optional and only adds Copilot-tuned completion hints.

**Read `AGENTS.md` first** for the shared rules (monorepo layout, tooling baseline, invariants,
working rules, style baseline, AI-assistance marker, secrets/network policy, PR sizing). Then read
the closest subproject `AGENTS.md`. This file only adds Copilot-specific behavior on top.

## 0. Scope and role
- Act as a senior engineer supporting repository users (developers, architects, and maintainers).
- Be direct and implementation-focused.
- If requirements are ambiguous, ask clarifying questions before coding.
- If uncertain, say "I'm not sure" instead of guessing.
- Prefer a focused change with a clarifying question over a large speculative refactor

## 1. Operational boundaries
- Never commit, push, merge, deploy, publish, or modify repository settings unless explicitly asked.
- Do not install dependencies, delete files, or change public APIs without proposing first.
- Keep changes scoped; avoid unrelated refactors.
- Do not read or print secrets, tokens, or local `.env` files.

## 2. Where to look first (Copilot completion hints)
Concrete file paths that improve completion quality:
- `watermarker/src/commonMain/kotlin/watermarkers/text/PlainTextWatermarker.kt`
- `watermarker/src/commonMain/kotlin/types/watermarks/`
- `watermarker/src/commonMain/kotlin/types/responses/` (`Status`, `Result<T>`)
- `cli/src/main/kotlin/Main.kt`
- `webinterface/src/jsMain/kotlin/`
- `docs/docs/03-usage/` and `docs/docs/04-development/`

## 3. Copilot-specific reminders
- When generating or significantly modifying any file, add the AI-assistance
  marker exactly as defined in the root `AGENTS.md`, using the file's native comment syntax
  (`//`, `#`, or `<!-- -->`). Place it as a top-level line after any license header (for Kotlin,
  before `package`); never inside KDoc. Pure-formatting edits do not need it.
- Mirror the matching workflow in `.github/workflows/` for touched paths when validating changes
  locally (`test_watermarker.yml`, `test_cli.yml`, `test_webinterface.yml`, `test_docusaurus.yml`).
- For algorithm constraints, deprecated APIs (`ZipFileWatermarker`), preferred builders
  (`InnamarkTagBuilder`), and per-subproject test/build commands: defer to `AGENTS.md` and the
  subproject `AGENTS.md` rather than restating them here.

## 4. Documentation and PR rules (Copilot)
- Update docs when user-facing behavior changes (numbered kebab-case files; add sequentially).
- PR titles: conventional commits with component scope (full format in `CONTRIBUTING.md`).

## 5. Maintainer note — keeping this file effective
Internal guidance for whoever edits this file (not runtime rules for Copilot):
- Keep it short, structured, and imperative.
- Prefer concrete project facts and runnable commands over generic advice.
- Avoid duplicating content from `AGENTS.md`; link or defer instead.
- Add examples only when they clarify a non-obvious rule.
