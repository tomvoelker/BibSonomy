#!/usr/bin/env python3
"""Report remaining iBATIS footprint in the repository."""

from __future__ import annotations

import argparse
import json
from collections import OrderedDict
from datetime import datetime, timezone
from pathlib import Path
from typing import Dict, List

PATTERNS = OrderedDict(
    [
        ("java_ibatis_imports", "import com.ibatis"),
        ("xml_sqlmap_doctype", "<!DOCTYPE sqlMap"),
        ("xml_sqlmapconfig_doctype", "<!DOCTYPE sqlMapConfig"),
        ("spring_sqlmap_factory", "SqlMapClientFactoryBean"),
    ]
)


def count_files_with_pattern(module_path: Path, pattern: str) -> int:
    count = 0
    for file_path in module_path.rglob("*"):
        if not file_path.is_file():
            continue
        if "target" in file_path.parts:
            continue
        try:
            content = file_path.read_text(encoding="utf-8", errors="ignore")
        except OSError:
            continue
        if pattern in content:
            count += 1
    return count


def build_report(workspace: Path) -> Dict[str, object]:
    modules = sorted(path for path in workspace.glob("bibsonomy-*") if path.is_dir())
    module_stats: List[Dict[str, object]] = []

    totals = {metric: 0 for metric in PATTERNS}
    for module in modules:
        stats = {"module": module.name}
        non_zero = False
        for metric, pattern in PATTERNS.items():
            value = count_files_with_pattern(module, pattern)
            stats[metric] = value
            totals[metric] += value
            if value:
                non_zero = True
        if non_zero:
            module_stats.append(stats)

    return {
        "generated_at_utc": datetime.now(timezone.utc).isoformat(),
        "workspace": str(workspace),
        "metrics": list(PATTERNS.keys()),
        "modules": module_stats,
        "totals": totals,
    }


def to_markdown(report: Dict[str, object]) -> str:
    lines = []
    lines.append("# iBATIS Footprint Report")
    lines.append("")
    lines.append(f"- Generated (UTC): `{report['generated_at_utc']}`")
    lines.append(f"- Workspace: `{report['workspace']}`")
    lines.append("")

    lines.append("| Module | Java imports | sqlMap XML | sqlMapConfig XML | SqlMapClientFactoryBean |")
    lines.append("| --- | ---: | ---: | ---: | ---: |")
    for module in report["modules"]:
        lines.append(
            "| {module} | {java_ibatis_imports} | {xml_sqlmap_doctype} | "
            "{xml_sqlmapconfig_doctype} | {spring_sqlmap_factory} |".format(**module)
        )
    totals = report["totals"]
    lines.append(
        "| **Total** | **{java_ibatis_imports}** | **{xml_sqlmap_doctype}** | "
        "**{xml_sqlmapconfig_doctype}** | **{spring_sqlmap_factory}** |".format(**totals)
    )
    lines.append("")
    return "\n".join(lines)


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--workspace",
        default=".",
        help="Repository root to scan (default: current directory).",
    )
    parser.add_argument("--json-output", default=None, help="Optional JSON output path.")
    parser.add_argument("--markdown-output", default=None, help="Optional markdown output path.")
    args = parser.parse_args()

    workspace = Path(args.workspace).resolve()
    report = build_report(workspace)

    summary = (
        "iBATIS footprint totals: "
        f"imports={report['totals']['java_ibatis_imports']}, "
        f"sqlMap={report['totals']['xml_sqlmap_doctype']}, "
        f"sqlMapConfig={report['totals']['xml_sqlmapconfig_doctype']}, "
        f"factoryBeans={report['totals']['spring_sqlmap_factory']}"
    )
    print(summary)

    if args.json_output:
        json_path = Path(args.json_output)
        json_path.parent.mkdir(parents=True, exist_ok=True)
        json_path.write_text(json.dumps(report, indent=2), encoding="utf-8")

    if args.markdown_output:
        md_path = Path(args.markdown_output)
        md_path.parent.mkdir(parents=True, exist_ok=True)
        md_path.write_text(to_markdown(report), encoding="utf-8")


if __name__ == "__main__":
    main()
