# AGENTS.md (docs)

This file applies to `docs/`. Also read the repository-level `../AGENTS.md` for global rules.

## Scope
- Docusaurus 3 documentation site for all Innamark subprojects.
- This is not a Gradle project.

## Dev environment and commands
- Use Node 18 and yarn.
- Run from `docs/`:
  - `yarn install --frozen-lockfile`
  - `yarn start`
  - `yarn build`
- Matching CI workflows: `.github/workflows/test_docusaurus.yml`,
  `.github/workflows/deploy_docusaurus.yml`.

## Content structure rules
- Keep numbered, kebab-case paths and filenames.
- Relevant roots:
  - `docs/docs/03-usage/`
  - `docs/docs/04-development/`
- Add new docs with the next sequential number; do not renumber existing files unless required.

## Update policy
- Any user-facing behavior change in `watermarker/`, `cli/`, or `webinterface/` should be reflected here.
- Keep examples and command snippets consistent with current build/test workflows.

## Assets and licensing
- New non-source assets (images and other binary files) require a sibling `<name>.license` file
  per REUSE (e.g. `my-figure.jpg.license` for `my-figure.jpg`).

## Quality
- Prefer small, focused docs diffs.
- Keep terminology aligned with the implementation and the watermarking algorithm.
