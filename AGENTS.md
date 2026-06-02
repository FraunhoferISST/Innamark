# AGENTS.md

Repository-wide guidance for all coding agents in this monorepo.

## Read order and precedence
- Start here for global rules.
- Then read the closest subproject file (nearest file wins):
  - `watermarker/AGENTS.md`
  - `cli/AGENTS.md`
  - `webinterface/AGENTS.md`
  - `docs/AGENTS.md`
- For GitHub Copilot-specific behavior, also read `.github/copilot-instructions.md`.

## Monorepo layout
- This repository has **independent builds** per subproject; there is no root Gradle build.
- Subprojects:
  - `watermarker/`: Kotlin Multiplatform core library (JVM + JS)
  - `cli/`: Kotlin/JVM CLI app consuming `watermarker-jvm`
  - `webinterface/`: Kotlin/JS KVision UI consuming `watermarker`
  - `docs/`: Docusaurus 3 documentation site
- `cli/` and `webinterface/` consume the **locally published** `watermarker` artifact. When touching
  library code, publish first from `watermarker/`:

  ```bash
  cd watermarker && ./gradlew publishToMavenLocal
  ```

## Tooling baseline
- Kotlin subprojects (`watermarker/`, `cli/`, `webinterface/`): JDK 21, Gradle wrapper from the
  subproject folder.
- `docs/`: Node 24 (LTS) with yarn.
- Always run commands from the relevant subproject directory.

## Global invariants
- Innamark uses whitespace replacement (base-4 alphabet: `\u2008`, `\u2009`, `\u202F`, `\u205F`).
- Separator is `\u2004` (`DefaultTranscoding.SEPARATOR_CHAR`).
- Capacity is based on **regular-space count**, not total text length.
- Watermark repetition across the cover is intentional for robustness; do not remove it.
- Prefer `InnamarkTagBuilder` for new watermark creation paths.
- `ZipFileWatermarker` is deprecated; do not extend it.

## Working rules
- Run commands from the relevant subproject directory.
- Keep diffs focused; avoid drive-by reformatting.
- Keep Fraunhofer license headers intact in source files (see `CONTRIBUTING.md` → License Header
  for the exact block).
- Add tests for behavioral changes and keep docs in `docs/` in sync for user-facing changes.
- Do not commit, push, merge, release, or deploy unless explicitly requested by the user.
- Do not install dependencies, fetch from the network, delete files, or change public APIs without
  proposing the change first.
- Do not read or print secrets, tokens, or local `.env` files; if such content is encountered,
  stop and ask before continuing.
- Soft recommendation: keep individual change sets under ~1000 LOC (see `CONTRIBUTING.md` for the
  full PR sizing guidance).

## Testing policy
- `watermarker/` is the only subproject with a mature automated test suite and should remain fully
  tested.
- For `watermarker/`, behavioral changes should come with tests unless there is a strong reason not to.
- `cli/` and `webinterface/` testing is optional today, not mandatory; add tests when practical,
  but do not block small focused changes solely on missing new tests there.
- File-based behaviors should reuse existing fixtures from the `samples/` folder where possible
  instead of inventing new ad-hoc samples.

## Style and quality baseline
- Kotlin style: 4 spaces, hard wrap at 100 chars, KDoc for public APIs.
- Follow Kotlin KDoc syntax: `https://kotlinlang.org/docs/kotlin-doc.html`.
- Every class/interface/object should have valid KDoc.
- Methods/functions with substantial logic should have valid KDoc when it improves generated docs
  and maintainability.
- Inline comments should stay light; prefer short one-sentence comments only for long or difficult
  code blocks.
- Do not end single-line comments with a trailing period.
- Tests do not need extra explanatory comments unless a case is genuinely non-obvious.
- Watermarker library: value-based error handling (`Status`/`Result<T>`), avoid exceptions in core.
- Every Kotlin file should keep the Fraunhofer license header and a copyright year range from the
  original creation year to the latest modification year. On any change to a file, bump the end of
  the range to the current year.
- If an AI assistant makes a non-trivial change to any file (i.e. anything beyond pure
  formatting, renames, or comment-only edits), add the AI-assistance marker using the file's
  native single-line comment syntax. The marker text is always:

  ```text
  This file was developed with AI assistance.
  ```

  Place it as a single top-level line near the top of the file: for Kotlin, directly after the
  license header block and before the `package` statement; for other files, directly after any
  license header, or at the very top when there is none. This keeps the marker file-level,
  greppable, and out of generated KDoc/docs. Pure-formatting edits (e.g. reindentation) do not
  require the marker.
- PR titles should follow conventional commits with component scope (e.g. `fix(cli): ...`); see
  `CONTRIBUTING.md` for the full format.

## Supply chain and SBOM (ORT)
- Dependencies, the CycloneDX SBOM, the license gate, and the OSV scan run via
  `oss-review-toolkit/ort-ci-github-action`; license policy comes from upstream `ort-config`
  (no in-repo ORT config).
- License policy is simple: copyleft fails the build (`fail-on: violations`).
- The SBOM (`bom.cyclonedx.json`) is never committed; it is a CI artifact and a GitHub Release asset.
- Workflows: `.github/workflows/sbom_watermarker.yml` (license gate + SBOM) and
  `sbom_scan_watermarker.yml` (weekly OSV scan); replicate for `cli/`/`webinterface/`, not `docs/`.

## Related instructions
- Copilot-focused instructions: `.github/copilot-instructions.md`
- Contribution details: `CONTRIBUTING.md`

## Maintainer note — keeping this file effective
Internal guidance for whoever edits this file (not runtime rules for agents):
- Keep it short, structured, and imperative.
- Prefer concrete project facts and runnable commands over generic advice.
- Avoid duplicating content from `CONTRIBUTING.md` or subproject `AGENTS.md`; link or defer instead.
- Add examples only when they clarify a non-obvious rule.
- When changing rules here, check whether `.github/copilot-instructions.md` and the subproject
  `AGENTS.md` files still align.
