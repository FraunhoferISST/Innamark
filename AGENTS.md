# AGENTS.md

## Big picture
- This repo is a monorepo with **separate Gradle builds per subproject**. Work inside: `watermarker/`, `cli/`, `webinterface/`, `docs/`.
- `watermarker/` is the core **Kotlin Multiplatform library** (JVM + JS targets). Shared algorithm code lives in `watermarker/src/commonMain/kotlin`; platform-specific code in `src/jvmMain` and `src/jsMain`.
- **`docs/` is the central documentation** covering usage and development for all subprojects (Docusaurus site).
- The **main data flow** is: text input → `PlainTextWatermarker` applies whitespace-replacement encoding → output is imperceptibly watermarked text. Optional: `InnamarkTagBuilder`/`InnamarkTag` prepend metadata (size, CRC32, SHA3-256 hash).
- `TextFileWatermarker` delegates to `PlainTextWatermarker` for text file watermarking.
- **⚠️ `ZipFileWatermarker` is deprecated** and should not be the focus of new development. Use text-based watermarking instead.
- `cli/` is a thin JVM wrapper over the published `watermarker-jvm` artifact. `webinterface/` is a KVision JS frontend using the multiplatform `watermarker` artifact.

## The Innamark Watermarking Algorithm

### Core Concept
Innamark **replaces whitespaces in a cover text with similarly-looking Unicode whitespaces** to embed a watermark. This is imperceptible to humans but recoverable by the extraction algorithm.

### Whitespace Alphabet
The "Alphabet" is the set of Unicode whitespaces used for encoding. Defined in `DefaultTranscoding` (`PlainTextWatermarker.kt`):
- Punctuation space (`\u2008`)
- Thin space (`\u2009`)
- Narrow-no-break space (`\u202F`)
- Medium mathematical space (`\u205F`)

These four characters form a base-4 encoding scheme. The watermark capacity of any text depends on **the number of regular spaces available**, not total character count.

### InnamarkTag: Metadata & Flags
To add robustness and functionality, `InnamarkTag` prepends a **1-byte tag** to the watermark that encodes 8 feature flags:
- **Bit 0 (Custom)**: Reserved for custom implementations
- **Bit 1 (Compressed)**: Content is DEFLATE-compressed
- **Bit 2 (Sized)**: 4-byte size field follows the tag
- **Bit 3 (CRC32)**: 4-byte CRC32 checksum field follows
- **Bit 4 (SHA3-256)**: 32-byte SHA3-256 hash field follows
- **Bits 5–7**: Unused (reserved)

**Layout in watermark bytes**: `[tag byte][optional flag fields][watermark content]`

Example: A CRC32-verified, compressed watermark has tag `0x50` (bits 1 & 3 set) followed by the CRC32 checksum, then compressed content.

### Watermark Extraction
During extraction, the first byte tells the algorithm which optional fields to expect and how to interpret the rest. This enables extraction of watermarks from unknown sources if they follow the spec.

### Why InnamarkTag by Default
Use `InnamarkTagBuilder` to create watermarks with the desired features (e.g., `InnamarkTagBuilder.sizedCRC32(text)`). This is the **default & recommended approach** for new code. Raw watermarks are rarely needed.

**Reference:** IEEE Access paper "Innamark: A Whitespace Replacement Information-Hiding Method" (2025): https://doi.org/10.1109/ACCESS.2025.3583591

## Critical workflows
- **Build/test the library** from `watermarker/`: `./gradlew build`.
- **Publish locally** (required by other subprojects): `cd watermarker && ./gradlew publishToMavenLocal`.
- **Build the CLI fat jar** from `cli/`: `./gradlew shadowJar`; dev run: `./gradlew run --args="--help"`.
- **Run the web UI** from `webinterface/`: `./gradlew -t run` (development mode, serves on `http://localhost:3000`). Production artifact: `./gradlew clean zip`.
- **Docker build**: `docker compose up` from repo root; builds webinterface container (publishes `watermarker` to Maven local, then builds frontend).
- **Documentation site** (`docs/`): `yarn install`, `yarn start` (dev), `yarn build` (production). Separate from Gradle builds.

## Project-specific patterns

### Error Handling (Watermarker Library)
- The **watermarker library** uses value-based error handling: return `Status` (for operations with no return value) or `Result<T>` (for operations with a return value) instead of throwing exceptions.
- Compose statuses via `appendStatus()` / `prependStatus()` (`watermarker/src/commonMain/kotlin/types/responses/Status.kt`, `Result.kt`).
- **Note:** Other projects (CLI, webinterface) *can* but are not required to follow this pattern. Only the core library strictly enforces it.

### Custom Events
- Keep event classes close to the feature that emits them; subclass `Event.Warning` or `Event.Error`.
- Provide a `source` string like `"ClassName.functionName"` for traceable error origins.
- Each Warning/Error must override `getMessage()` to provide a user-readable explanation.

### Watermark Creation & Usage
- **Use `InnamarkTagBuilder` by default** to create watermarks with feature combinations (compression, size, CRC32, SHA3-256).
  - E.g., `InnamarkTagBuilder.compressedSizedCRC32("my watermark").finish()` creates a watermark with compression + size + checksum.
- Raw watermarks (using `RawInnamarkTag` or plain bytes) are rarely needed; only use when you have specific technical requirements.

### Text Watermarking
- `PlainTextWatermarker` assumes insert positions are regular **spaces** by default.
- It **rejects covers already containing watermark alphabet chars** (raises `ContainsAlphabetCharsError`).
- Watermark **capacity depends on whitespace count**, not total character count. Agents computing capacity must account for this.

### File Type Support (JVM-Only)
- To add support for a new file type, implement `WatermarkableFile` + a matching `FileWatermarker`.
- Register the file extension in `SupportedFileType` (`watermarker/src/jvmMain/kotlin/utils/FileHandling.kt`).
- Example: `TextFileWatermarker` wraps `PlainTextWatermarker` to handle `.txt` files.

---

## UI and integration

### Web UI (Active)
- **Two functional tabs** (both text-based):
  1. **Embed Tab**: Embed a watermark string into a cover text. Computes watermark capacity in real-time and warns if watermark is too large.
  2. **Extract Tab**: Extract and verify watermarks from watermarked text. Shows the most frequent watermark and detailed breakdown of encoding types.
- Both tabs use `PlainTextWatermarker` and compute capacity via `PlainTextWatermarker.getMinimumInsertPositions(...)`.
- **Preserve that behavior** if you modify watermark sizing rules.

### JS Specifics
- JS-specific compression/CRC implementations live in `watermarker/src/jsMain/kotlin/utils`.
- Keep common APIs aligned with the JVM side to avoid subtle bugs during extraction.

---

## Git and contribution workflow

For complete contribution guidelines, see `CONTRIBUTING.md`.

**Key practices:**
- **Conventional commits format** ([spec](https://www.conventionalcommits.org/)): PR titles follow `<type>(<component>): <description>` (e.g., `feat(watermarker): add compression`).
- **Squash and merge**: PRs are squashed with PR number and title included automatically.
- **Branch naming**: Use descriptive names (e.g., `feat/compression-option`, `fix/memory-leak`).
- **Issue linking**: Every PR must link to at least one GitHub issue.
- **GPG signing**: Recommended for verified commits.
- **Code review**: Requires approval from Committer team before merging.
- **PR size**: Keep changes focused; aim for < 1000 lines per PR.

---

## Code style and conventions

### License & Attribution
- Every source file carries the **Fraunhofer license header** (Fraunhofer License on basis of MIT).
- **When a file is modified or developed with AI assistance**, add a comment at the top (below the license header):
  ```kotlin
  // This file was developed with AI assistance.
  ```
  This helps maintainers and future developers understand the code's origins.

### Kotlin Style
- Follow **Kotlin Coding Conventions** with these adjustments:
  - **Hard wrap at 100 characters** (not 120).
  - **KDoc on every method/function** (required for automatic doc generation).
  - **4-space indentation** (not tabs).
  - Reference: `CONTRIBUTING.md` for detailed setup in IntelliJ.

### Linting & Formatting
- **Ktlint** is configured in every Gradle subproject and enforces style.
- Avoid unrelated reformatting when editing files; keep changes focused.
- IntelliJ's "Reformat on Save" and "Optimize Imports" (Actions on Save) help maintain consistency.

### Testing Strategy
- Tests are split by **target** (`commonTest`, `jvmTest`, `jsTest`) and by **level** (`unitTest`, `integrationTest`).
- Existing tests in `watermarker/src/*Test/kotlin` and samples in `samples/` serve as behavior references.
- Add tests for any new feature or bug fix.

### Documentation
- Central documentation lives in `docs/` (Docusaurus).
- Docs content is organized with **numbered, kebab-case paths** (e.g., `docs/docs/03-usage/10-watermarker/01-installation.md`).
- When adding docs, preserve this structure and number new files sequentially.

### Additional AI Guidance
- For GitHub Copilot users specifically, see `.github/copilot-instructions.md`.
- Other AI agents (Claude, Gemini, etc.) should also reference that file for Copilot-specific patterns and preferences.
