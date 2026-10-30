<!--
 Copyright (c) 2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.

 This work is licensed under the Fraunhofer License (on the basis of the MIT license)
 that can be found in the LICENSE file.
-->

<!-- This file was developed with AI assistance. -->

# Architecture Diagrams

PlantUML sources for the arc42 architecture documentation. This folder is
prefixed with `_` so Docusaurus excludes it from the build and sidebar (see
`GlobExcludeDefault` in `@docusaurus/utils`).

## Workflow

1. Author or edit a `.puml` file here. Open the diagram with `@startuml`, then
   `!include style.puml` to apply the shared look, then the diagram body, and
   close with `@enduml`. The `!include` must be inside the `@startuml` block so
   the `skinparam` directives take effect.
2. Render all diagrams to SVG:

   ```bash
   yarn diagrams
   ```

   This runs the official PlantUML Docker image (`ghcr.io/plantuml/plantuml`,
   bundles Java + Graphviz) and writes `*.svg` into `../images/`.
3. Commit both the `.puml` source and the rendered `.svg` (plus its
   `.svg.license` sibling per REUSE).
4. Embed the SVG in the arc42 section, e.g. in `03_context_and_scope.md`:

   ```markdown
   ![Context and Scope](./images/03_context.svg)
   ```

## Conventions

- One `.puml` file per diagram, named after the arc42 section it belongs to
  (e.g. `03_context.puml`, `05_building_block_overview.puml`).
- `style.puml` holds shared `skinparam`, colors, and theme — include it, do not
  duplicate styles per diagram.
- Rendered SVGs live in `../images/` and carry a sibling `.svg.license` file
  (Fraunhofer License).
- Generated images are owned by the authors of the `.puml` sources (see the
  PlantUML FAQ); PlantUML itself is a build-time tool and is not distributed as
  a dependency of the documentation site.
