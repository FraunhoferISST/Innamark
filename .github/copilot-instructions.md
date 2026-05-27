# GitHub Copilot Instructions — Innamark

Trust these instructions. Only fall back to repository search when something here is missing
or proves to be incorrect.

## 0. Assistant role & boundaries

- Act as a highly skilled developer assisting a software architect. Communicate directly and
  concisely; focus on implementation details.
- Be direct. Do not be sycophantic. If the proposed approach is wrong, say so and explain why.
- If a requirement is ambiguous, stop and **ask a clarifying question before coding**.
- If you are uncertain, say "I'm not sure" instead of guessing confidently.
- **Never act on behalf of the user without explicit approval**: do not commit, push, open or
  merge pull requests, create branches that the user did not request, publish artifacts, change
  repository settings, or run release/deploy workflows unless the user explicitly instructs you
  to. When in doubt, ask first.
- Do not install new dependencies, change public APIs, or delete files without proposing the
  change first.

## 1. What this repository is

**Innamark** is a text-watermarking / steganography toolchain developed at Fraunhofer ISST. It
hides invisible watermarks in text by replacing regular spaces with visually identical Unicode
whitespace characters. The transcoding alphabet is **base-4**
(`\u2008`, `\u2009`, `\u202F`, `\u205F`); a fifth Unicode whitespace, the **separator char**
`\u2004` (three-per-em space, `DefaultTranscoding.SEPARATOR_CHAR`), delimits consecutive
watermarks in the cover. For robustness the watermark is embedded **repeatedly** until every
regular space in the cover has been consumed — if part of the text is destroyed (cut,
re-flowed, partially copied), the algorithm can still recover the watermark from another
occurrence. Optional `InnamarkTag` metadata adds size, CRC32, SHA3-256 and DEFLATE compression.
Scientific background: IEEE Access 2025, https://doi.org/10.1109/ACCESS.2025.3583591.

It is a **monorepo with 4 independent Gradle/Node subprojects** (no root build):

| Folder          | Type                                       | Purpose                                                                  |
|-----------------|--------------------------------------------|--------------------------------------------------------------------------|
| `watermarker/`  | Kotlin Multiplatform library (JVM + JS)    | Core algorithm. Published as `watermarker-jvm` / `watermarker-js` under `de.fraunhofer.isst.innamark`. |
| `cli/`          | Kotlin/JVM application (Shadow fat-jar)    | Thin CLI over **`watermarker-jvm`**.                                     |
| `webinterface/` | Kotlin/JS KVision frontend                 | Browser UI; consumes the **`watermarker-js`** artifact of the library.   |
| `docs/`         | Docusaurus 3 site (Node 18, yarn)          | Central documentation. Standalone, no Gradle.                            |

Size: small/medium (~few thousand LOC of Kotlin + docs). Read `AGENTS.md` for deeper algorithm
context before changing core code.

## 2. Required toolchain

The CI uses, and you should use, exactly:

- **JDK 21 (Temurin)** — set via `jvmToolchain(21)` in `watermarker/build.gradle.kts`.
- **Gradle wrapper** shipped per subproject. **Always** invoke `./gradlew` from inside the
  relevant subproject, never from the repo root (there is no root `build.gradle`).
- **Node 18** + **yarn** for `docs/` (Docusaurus 3).
- **Docker + Docker Compose** only needed for the `docker compose up` integration path.
- Linux/macOS shell assumed; `gradlew.bat` exists for Windows.

The Gradle wrappers download Kotlin, KVision, ktlint, Dokka and Kover on first run (versions
are pinned in each subproject's `build.gradle.kts` / `gradle.properties` — **read those files
rather than hard-coding versions in code or docs**). First build can take several minutes —
do **not** treat the initial long download as a hang.

## 3. Build, test, lint — exact commands

All four subprojects are independent. CI runs them with `working-directory:` set to the
subproject. Match that locally.

### 3.1 `watermarker/` (always build/publish this FIRST if you change it)

```bash
cd watermarker
./gradlew ktlintCheck         # lint (CI gate)
./gradlew allTests            # runs jvmTest + jsTest (CI gate)
./gradlew build               # full build incl. tests
./gradlew publishToMavenLocal # REQUIRED before building cli/ or webinterface/
```

Test sources are split by target and level: `commonTest`, `jvmTest`, `jsTest` (subdivided into
`unitTest`/`integrationTest`). Use `./gradlew jvmTest` or `./gradlew jsTest` to narrow.

### 3.2 `cli/` (depends on `watermarker-jvm` from Maven Local)

```bash
# Precondition: cd ../watermarker && ./gradlew publishToMavenLocal
cd cli
./gradlew ktlintCheck
./gradlew test
./gradlew shadowJar           # produces build/libs/cli-<ver>-all.jar
./gradlew run --args="--help" # dev run
```

If `cli` build fails with “could not find watermarker-jvm:0.1.0-SNAPSHOT”, you forgot
`publishToMavenLocal` in `watermarker/`. Fix that first, then retry.

### 3.3 `webinterface/` (depends on multiplatform `watermarker`)

```bash
# Precondition: cd ../watermarker && ./gradlew publishToMavenLocal
cd webinterface
./gradlew ktlintCheck
./gradlew test
./gradlew -t run              # dev server on http://localhost:3000
./gradlew clean zip           # production bundle
```

`gradle.properties` here pins `kotlinVersion` / `kvisionVersion` via system properties — do
not "upgrade" these casually; KVision is sensitive to the Kotlin version it ships with.

The full Docker path also works and is what CI verifies:
```bash
docker build -t innamark-webinterface -f webinterface/Dockerfile .
```

The repo-root **`docker-compose.yml` is a convenience entry point for newcomers**: a single
`docker compose up` builds the `watermarker` library and starts the webinterface on
http://localhost:8080 with no local JDK / Gradle / Node setup required. Use it for quick demos,
not for development iteration.

### 3.4 `docs/`

```bash
cd docs
yarn install --frozen-lockfile   # use this exact flag; CI does
yarn start                       # dev (hot reload)
yarn build                       # production; CI gate on PRs
```

Node 18 is mandatory (CI uses `actions/setup-node@v6` with `node-version: 18`).

## 4. CI workflows (`.github/workflows/`)

Each PR triggers only the workflow(s) whose path filter matches:

| Workflow file                                | Trigger paths       | Gates                                                       |
|----------------------------------------------|---------------------|-------------------------------------------------------------|
| `test_watermarker.yml`                       | `watermarker/**`    | `ktlintCheck`, `allTests` (JDK 21)                          |
| `test_cli.yml`                               | `cli/**`            | `ktlintCheck`, then `watermarker publishToMavenLocal`, then `cli test` |
| `test_webinterface.yml`                      | `webinterface/**`   | `ktlintCheck`, `watermarker publishToMavenLocal`, `webinterface test`, `docker build` |
| `test_docusaurus.yml`                        | PR to `main`        | `yarn install --frozen-lockfile && yarn build` in `docs/`   |
| `deploy_docusaurus.yml`                      | push to `main`      | Builds + deploys to GitHub Pages                            |
| `deploy_watermarker-source-code-docs.yml`    | manual              | Dokka → `gh-pages`                                          |

**Before opening a PR, replicate the matching workflow locally.** The CLI and webinterface
checks will fail unless you publish `watermarker` to Maven Local first — CI does this
explicitly; you must too.

## 5. Code style & conventions (enforced by ktlint and reviewers)

- 4-space indent, **hard wrap at 100 chars** (see `.editorconfig`).
- KDoc on every public function/method (used for Dokka generation).
- Every source file must keep the Fraunhofer license header.
- **When you create or significantly modify a file with AI assistance**, add directly under the
  license header:
  ```kotlin
  // This file was developed with AI assistance.
  ```
- Watermarker core library uses **value-based error handling** (`Status` / `Result<T>` with
  `appendStatus()` / `prependStatus()`) — **do not throw exceptions** there. CLI and
  webinterface may throw if convenient.
- Custom events subclass `Event.Warning` / `Event.Error`, supply a `source = "Class.func"`
  string, and override `getMessage()`.
- Avoid drive-by reformatting; keep diffs focused so ktlint and reviewers don’t reject the PR.

## 6. Architectural map (where to change what)

```text
- watermarker/src/
    - commonMain/kotlin/
        - watermarkers/text/
            - PlainTextWatermarker.kt  # core algorithm + DefaultTranscoding alphabet
            - TextWatermarker.kt       # public interface
        - types/
            - watermarks/              # Watermark, InnamarkTag, InnamarkTagBuilder
            - responses/               # Result<T>, Status (value-based error handling)
        - utils/                       # Compression, CRC32, ExtensionFunctions (common API)
    - jvmMain/kotlin/
        - watermarkers/file/          # FileWatermarker, TextFileWatermarker,
                                      # ZipFileWatermarker (deprecated)
        - types/files/                # WatermarkableFile, TextFile, ZipFile
        - utils/                      # FileHandling.kt -> SupportedFileType
                                      # (register new file extensions here);
                                      # JVM Compression/CRC32 actuals
    - jsMain/kotlin/utils/            # JS actuals for Compression, CRC32
    - commonTest/, jvmTest/, jsTest/  # mirror main; split into unitTest/integrationTest
                                      # (jvmTest also has resources/ with fixtures)
- cli/src/main/kotlin/
    - Main.kt                          # single entry, kotlinx-cli
- webinterface/src/jsMain/kotlin/
    - App.kt                           # KVision application root
    - WatermarkTextEmbedTab.kt         # embed flow (uses PlainTextWatermarker +
                                       # getMinimumInsertPositions for capacity)
    - WatermarkTextExtractTab.kt       # extract flow
    - WatermarkFileTab.kt              # file-based flow
- webinterface/webpack.config.d/       # webpack tweaks (bootstrap, css, file, handlebars)
- docs/docs/                           # numbered, kebab-case dirs:
    - 03-usage/{10-watermarker, 11-cli, 12-webinterface}/
    - 04-development/{10-watermarker, 11-cli, 12-webinterface, 99-contributing.md}/
- samples/                             # sample texts/zips used by tests & demos
- .github/workflows/                   # CI pipelines (see §4)
```

Key rules:

- `PlainTextWatermarker` rejects covers that already contain any alphabet char **or the
  separator char** (`ContainsAlphabetCharsError`). Capacity is driven by **number of regular
  spaces**, not total characters — preserve this when changing sizing.
- The watermark is intentionally embedded **multiple times** in the cover (separated by the
  separator char) for robustness against partial text loss. Do not "optimise" this away.
- Prefer `InnamarkTagBuilder` (e.g. `InnamarkTagBuilder.sizedCRC32(text).finish()`) over raw
  watermarks in new code.
- `ZipFileWatermarker` is **deprecated**; do not extend it.
- New JVM file types: implement `WatermarkableFile` + a `FileWatermarker`, then register the
  extension in `SupportedFileType` (`watermarker/src/jvmMain/kotlin/utils/FileHandling.kt`).
- Keep `commonMain`/`jvmMain`/`jsMain` APIs aligned so a watermark made on one platform extracts
  on the other.

## 7. Documentation expectations

- Update `docs/docs/...` whenever you change user-facing behavior. Folders and files use
  **numbered, kebab-case** names (e.g. `01-installation.md`). Add new files with the next
  sequential number, do not renumber existing ones unless necessary.
- New non-source assets (images, etc.) need a sibling `<name>.license` file per REUSE.

## 8. Pull request / commit conventions

- PR title (used as the squash-merge commit message) must be **conventional commits with the
  affected component as scope**, e.g. `feat(watermarker): add compression option`,
  `fix(webinterface): handle empty cover text`, `docs(cli): clarify --extract flag`.
- Every PR must link at least one issue.
- Keep PRs < ~1000 LOC.
- GPG-signed commits are recommended.

## 9. Known pitfalls (save yourself time)

1. **Do not run `./gradlew` from the repo root.** There is no root build; it will fail or pick
   up the wrong project.
2. **CLI/webinterface builds need `watermarker publishToMavenLocal` first.** Always.
3. Kotlin and KVision versions differ per subproject and are intentionally pinned (the
   webinterface lags behind because KVision constrains it). Read the build files; do not unify
   versions casually.
4. ktlint failures block CI. Run `./gradlew ktlintCheck` (or `ktlintFormat`) in the modified
   subproject before pushing.
5. Docusaurus build requires Node **18** specifically; newer LTS may work locally but CI is 18.
6. First Gradle invocation downloads hundreds of MB of dependencies — allow several minutes;
   do not abort.
7. The `samples/` directory contains binary `.zip` fixtures used by tests — do not regenerate
   them unintentionally.

---

Follow the steps in §3 in the order shown for each subproject and your changes will pass the
workflows in §4. Reach for `grep`/file search only when these instructions are silent on the
detail you need.
