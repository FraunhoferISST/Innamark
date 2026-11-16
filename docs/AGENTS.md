# AGENTS.md (docs)

<!-- This file was developed with AI assistance. -->

This file applies to `docs/`. Also read the repository-level `../AGENTS.md` for global rules.

## Scope
- Docusaurus 3 documentation site for all Innamark subprojects.
- This is not a Gradle project.

## Dev environment and commands
- Use Node 24 (LTS) and yarn.
- Run from `docs/`:
  - `yarn install --frozen-lockfile`
  - `yarn start`
  - `yarn build`
- Matching CI workflows: `.github/workflows/test_docusaurus.yml`,
  `.github/workflows/deploy_docusaurus.yml`.

## Content structure rules
- Keep numbered, kebab-case paths and filenames.
- Relevant roots:
  - `docs/docs/10-watermarker/`
  - `docs/docs/11-cli/`
  - `docs/docs/12-webinterface/`
- Add new docs with the next sequential number; do not renumber existing files unless required.
- Always write Markdown headings/titles in title case (e.g. `This Can Be an Example for a Title`),
  not sentence case. This also applies to admonition titles (e.g. `:::note ...`).

## Architecture docs (arc42)
- `docs/architecture/` is a second content area of the **same** Docusaurus site, not a separate
  site or build. Only the sidebar (`sidebarsArchitecture.js`) and the route (`/architecture/`)
  differ; both areas are built and deployed together by `yarn build`.
- The arc42 files use their own naming scheme (`01_introduction_and_goals.md`,
  `05_building-block-view/`, `08_concepts/`), which is the documented exception to the numbered
  kebab-case rule above. Follow the existing arc42 numbering; do not renumber to fit the usage docs.
- New ADRs go into `docs/architecture/09_architecture-decisions/` based on `adr-0_template.md` and
  must be registered in the table in that folder's `index.md`.
- Diagrams: PlantUML sources live in `docs/architecture/_diagrams/` (the `_` prefix excludes them
  from the build); render with `yarn diagrams` and commit the generated
  `docs/architecture/images/*.svg` together with the `.puml` source. Do not hand-edit SVGs.
- `yarn build` runs with `onBrokenLinks: 'throw'`, so moving or renaming a page requires fixing all
  inbound links, including those from `docs/architecture/` and vice versa.

## Update policy
- Any user-facing behavior change in `watermarker/`, `cli/`, or `webinterface/` should be reflected here.
- Keep examples and command snippets consistent with current build/test workflows.

## Assets and licensing
- New non-source assets (images and other binary files) require a sibling `<name>.license` file
  per REUSE (e.g. `my-figure.jpg.license` for `my-figure.jpg`).
- The AI-assistance marker (see `../AGENTS.md`) applies to docs too: in Markdown/MDX place it inside
  an HTML/MDX comment (`<!-- This file was developed with AI assistance. -->`) right after the
  license header, so it stays in source but is not rendered in the built Docusaurus site.

## Quality
- Prefer small, focused docs diffs.
- Keep terminology aligned with the implementation and the watermarking algorithm.
