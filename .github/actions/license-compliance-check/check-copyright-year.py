# SPDX-FileCopyrightText: 2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e. V.
#
# SPDX-License-Identifier: LicenseRef-Fraunhofer-License

# This file was developed with AI assistance.

import os
import re
import subprocess
import sys
from datetime import datetime, timezone

SOURCE_GLOBS = (
    "**/*.css", "**/*.html", "**/*.java", "**/*.js", "**/*.kt",
    "**/*.kts", "**/*.md", "**/*.mdx", "**/*.properties", "**/*.py",
    "**/*.sh", "**/*.sql", "**/*.toml", "**/*.ts", "**/*.tsx",
    "**/*.xml", "**/*.yaml", "**/*.yml", "**/*Dockerfile*",
)
EXCLUDED_GLOBS = (
    "**/.git/**", "**/build/**", "**/dist/**", "**/node_modules/**",
    "docs/api/**", "Innamark/**", "microsoft-office-extensions/common/lib/**",
)
COPYRIGHT_RE = re.compile(
    r"(?:Copyright \(c\)|SPDX-FileCopyrightText:)",
    re.IGNORECASE,
)
YEAR_RE = re.compile(r"\b\d{4}\b")


def git_paths(*args):
    output = subprocess.check_output(["git", *args])
    return {os.fsdecode(path) for path in output.split(b"\0") if path}


def main(base):
    paths = git_paths(
        "ls-files", "-z", "--", *(f":(glob){glob}" for glob in SOURCE_GLOBS),
        *(f":(exclude,glob){glob}" for glob in EXCLUDED_GLOBS),
    )
    changed = git_paths("diff", "--name-only", "--diff-filter=ACMRT", "-z", f"{base}...HEAD", "--")
    year = str(datetime.now(timezone.utc).year)
    failed = False

    for path in sorted(paths & changed):
        if not os.path.isfile(path):
            continue
        with open(path, "r", encoding="utf-8-sig", errors="replace") as source:
            text = source.read(2048)
        match = COPYRIGHT_RE.search(text)
        if match:
            years = YEAR_RE.findall(text[match.start():match.start() + 200])
            if years and years[-1] != year:
                print(f"{path}: copyright year must end in {year}")
                failed = True

    return int(failed)


if __name__ == "__main__":
    if len(sys.argv) != 2:
        sys.exit("usage: check-copyright-year.py BASE_REVISION")
    raise SystemExit(main(sys.argv[1]))