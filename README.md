<div align="center">
  <picture>
    <source width="340" media="(prefers-color-scheme: dark)" srcset="docs/static/img/branding/logo-sub/white/innamark_logo-sub_w.svg">
    <source width="340" media="(prefers-color-scheme: light)" srcset="docs/static/img/branding/logo-sub/black/innamark_logo-sub_b.svg">
    <img width="340" alt="Innamark (Invisible Watermarking) logo" 
src="docs/static/img/branding/logo-sub/black/innamark_logo-sub_b.svg">
  </picture>
  <br />
  <br />
  <img alt="GitHub Issues" src="https://img.shields.io/github/issues/FraunhoferISST/Innamark">
  <img alt="GitHub Pull Requests" src="https://img.shields.io/github/issues-pr/FraunhoferISST/Innamark">
  <img alt="GitHub repo size" src="https://img.shields.io/github/repo-size/FraunhoferISST/Innamark">
  <img alt="GitHub commit activity]" src="https://img.shields.io/github/commit-activity/t/FraunhoferISST/Innamark">
  <a href="https://fraunhoferisst.github.io/Innamark/">
    <img alt="Documentation" src="https://img.shields.io/badge/docs-online-green">
  </a>
  <img alt="GitHub Repo stars" src="https://img.shields.io/github/stars/FraunhoferISST/Innamark">
</div>

## Table of Contents

- [About](#about)
- [Documentation](#-documentation)
- [Structure](#-structure)
- [Getting Started](#getting-started)
    - [System Prerequisites](#system-prerequisites)
    - [Quick Start](#quick-start)
- [Contributing](#-contributing)
- [License](#-license)
- [Cite this Work](#-cite-this-work)
- [Team & Developers](#-team--developers)

## About

The *Invisible Watermarking* (Innamark) project aims to address some of the current
challenges in implementing data sovereignty solutions on a broader scale. The objective is to use
state-of-the-art digital watermarking techniques to embed metadata securely in the data being
exchanged, along with dedicated protocol-level checks for validation and enforcement. This enables
system-independent sovereignty checks to secure the data assets of the data owner without privacy
sacrifices.

This repository includes a generic **steganography / watermarking** library to hide any byte
encoded data in a cover text including two usage examples of a webinterface and command line
tool. The following example shows how the webinterface includes the watermark "Fraunhofer ISST"
inside a Lorem ipsum dummy cover text and extracts it afterward:
![Animated example of the webinterface](docs/static/img/webinterface-demo.gif)

## 📖 Documentation

All information from usage until development are collected and provided in our
[documentation](https://fraunhoferisst.github.io/Innamark/).

## 📁 Structure

This project uses a [monolithic repository approach](https://en.wikipedia.org/wiki/Monorepo) and
consists of different parts, located in different subfolders. The heart is a **watermarker
library**, located in the `watermarker` folder, used by other components like a CLI
tool or a webinterface shipped with this repo. Every part has its own `README` file to get further
information.

### Subfolder Overview

- **cli**: A command line tool to enable watermarking directly via a shell
- **docs**: The [documentation](https://fraunhoferisst.github.io/Innamark/) of all parts based on Docusaurus
- **samples**: Different examples of watermarked and non-watermarked files, mainly used for tests
- **watermarker**: The heart part of the repository: A Kotlin watermarker library for hiding text (a watermark) inside a cover text. The library works on the JVM (Java) and JavaScript (JS) platform
- **webinterface**: A frontend / GUI to use the watermarking inside a browser, build with
  [KVision](https://github.com/rjaros/kvision)

## 🚀 Getting Started

Detailed getting started guides are described for every component in their dedicated `README`
file, located in the corresponding subfolders. In the following, an easy start of the webinterface
with the watermarker library is described.

### System Prerequisites

The following things are needed to run this application:

- [Docker](https://docs.docker.com/engine/install/)
- [Docker Compose](https://docs.docker.com/compose/install/)

### Quick Start

To run the webinterface, just clone the repo locally and run the `docker-compose.yml` file in the
root directory of the project:

```shell
$ git clone https://github.com/FraunhoferISST/Innamark.git
$ cd Innamark
$ docker compose up
```

After the startup finished, try to visit the webinterface at http://localhost:8080

## ✍️ Contributing

Contributions to this project are greatly appreciated! Every contribution needs to accept the
Corporate Contributor License Agreement, located in the `CLA.md` file. For more details, see the
`CONTRIBUTING.md` file.

## ⚖️ License

This work is licensed under the Fraunhofer License (on the basis of the MIT license). See
`LICENSE` file for more information.

> [!NOTE]
> There are pending German and international (PCT) patent applications with the application numbers
> [DE102023125012.4](https://patents.google.com/patent/DE102023125012A1) and
> [WO2025056772A1](https://patents.google.com/patent/WO2025056772A1). In order to use the Innamark
> watermarker Software in the form published here, a patent license is required in addition to the
> license for the Software. See `LICENSE` for more information. In case of any questions or
> uncertainties, please contact us at innamark@isst.fraunhofer.de.

The initial project version was created within the scope of
the [Center of Excellence Logistics and It](https://ce-logit.com/).

## 📝 Cite this Work

The main concepts of the core Innamark watermarking algorithm are presented and compared against
related work in an open access _IEEE Access_ journal publication:

- Hellmeier et al., "Innamark: A Whitespace Replacement Information-Hiding Method", 2025, IEEE
  Access, vol. 13, pp. 123120-123135, doi: [10.1109/ACCESS.2025.3583591](https://doi.org/10.1109/ACCESS.2025.3583591)

To cite the journal article in LaTeX/BibTeX/BibLaTeX:

```bibtex
@article{Hellmeier.2025c,
    author = {Hellmeier, Malte and Norkowski, Hendrik and Schrewe, Ernst-Christoph and Qarawlus, Haydar and Howar, Falk},
    title = {{Innamark: A Whitespace Replacement Information-Hiding Method}},
    year = {2025},
    journal = {{IEEE Access}},
    volume = {13},
    pages = {123120--123135},
    issn = {2169-3536},
    doi = {10.1109/ACCESS.2025.3583591}
}
```

## 👨👩 Team & Developers

### Active

- [Malte Hellmeier](https://github.com/mhellmeier) (Fraunhofer ISST)
- [Haydar Qarawlus](https://github.com/hqarawlus) (Fraunhofer ISST)
- [Joris Schiphorst](https://github.com/Schiphorst-ISST) (Fraunhofer ISST)
- [Ernst-Christoph Schrewe](https://github.com/eschrewe) (Fraunhofer ISST)

### Alumni

- [Hendrik Norkowski](https://github.com/hnorkowski)
- [David Gemen](https://github.com/gemdav)
