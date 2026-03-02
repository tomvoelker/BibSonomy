#!/usr/bin/env python3
"""Compare local surefire failures against Bertha Java 8 baseline failures."""

from __future__ import annotations

import argparse
import glob
import json
import sys
import urllib.request
import xml.etree.ElementTree as ET
from dataclasses import asdict, dataclass
from datetime import datetime, timezone
from pathlib import Path
from typing import Dict, Iterable, List, Set, Tuple


DEFAULT_BASELINE_URL = (
    "https://bertha.cs.uni-kassel.de/bib-static/master/latest/testresults/data/suites.json"
)


@dataclass(frozen=True)
class TestKey:
    classname: str
    name: str


@dataclass
class TestFailure:
    key: TestKey
    status: str
    source: str
    message: str


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Compare local surefire failures against Bertha baseline failures."
    )
    parser.add_argument(
        "--baseline-url",
        default=DEFAULT_BASELINE_URL,
        help=f"Allure suites.json URL (default: {DEFAULT_BASELINE_URL})",
    )
    parser.add_argument(
        "--workspace",
        default=".",
        help="Workspace root to scan for surefire reports (default: current directory).",
    )
    parser.add_argument(
        "--markdown-output",
        default="",
        help="Optional markdown report output path.",
    )
    parser.add_argument(
        "--json-output",
        default="",
        help="Optional JSON report output path.",
    )
    parser.add_argument(
        "--fail-on-new",
        action="store_true",
        help="Exit with code 1 when new failures are detected.",
    )
    return parser.parse_args()


def fetch_json(url: str) -> dict:
    with urllib.request.urlopen(url, timeout=30) as response:
        return json.load(response)


def collect_uid_names(root: dict) -> Dict[str, str]:
    uid_name: Dict[str, str] = {}
    stack: List[dict] = [root]
    while stack:
        node = stack.pop()
        if not isinstance(node, dict):
            continue
        uid = node.get("uid")
        name = node.get("name")
        if isinstance(uid, str) and isinstance(name, str):
            uid_name[uid] = name
        children = node.get("children")
        if isinstance(children, list):
            stack.extend(children)
    return uid_name


def collect_baseline_failures(data: dict) -> Dict[TestKey, str]:
    uid_name = collect_uid_names(data)
    failures: Dict[TestKey, str] = {}
    stack: List[dict] = [data]
    while stack:
        node = stack.pop()
        if not isinstance(node, dict):
            continue
        status = node.get("status")
        if status in {"failed", "broken"}:
            parent_uid = node.get("parentUid")
            classname = uid_name.get(parent_uid, "?")
            name = node.get("name", "?")
            failures[TestKey(classname=classname, name=name)] = status
        children = node.get("children")
        if isinstance(children, list):
            stack.extend(children)
    return failures


def collect_current_failures(workspace: Path) -> Dict[TestKey, TestFailure]:
    failures: Dict[TestKey, TestFailure] = {}
    pattern = str(workspace / "**/target/surefire-reports/TEST-*.xml")
    for report in glob.glob(pattern, recursive=True):
        report_path = Path(report)
        try:
            root = ET.parse(report_path).getroot()
        except ET.ParseError:
            continue
        suite_name = root.attrib.get("name", "")
        for testcase in root.findall("testcase"):
            failure = testcase.find("failure")
            error = testcase.find("error")
            if failure is None and error is None:
                continue
            entry = failure if failure is not None else error
            status = "failure" if failure is not None else "error"
            key = TestKey(
                classname=testcase.attrib.get("classname", suite_name),
                name=testcase.attrib.get("name", ""),
            )
            message = ""
            if entry is not None:
                message = entry.attrib.get("message", "")[:400]
            failures[key] = TestFailure(
                key=key,
                status=status,
                source=str(report_path.relative_to(workspace)),
                message=message,
            )
    return failures


def sorted_keys(values: Iterable[TestKey]) -> List[TestKey]:
    return sorted(values, key=lambda x: (x.classname, x.name))


def build_markdown(
    baseline_url: str,
    current: Dict[TestKey, TestFailure],
    baseline: Dict[TestKey, str],
    new_failures: List[TestKey],
    still_failing: List[TestKey],
    resolved: List[TestKey],
) -> str:
    lines = [
        "# Java 21 vs Java 8 Baseline Failure Diff",
        "",
        f"- Generated (UTC): {datetime.now(timezone.utc).isoformat()}",
        f"- Baseline source: `{baseline_url}`",
        f"- Current failing testcases: `{len(current)}`",
        f"- Baseline failing/broken testcases: `{len(baseline)}`",
        f"- New failures: `{len(new_failures)}`",
        f"- Baseline failures still present: `{len(still_failing)}`",
        f"- Baseline failures currently absent: `{len(resolved)}`",
        "",
        "## New Failures",
    ]

    if not new_failures:
        lines.append("- none")
    else:
        for key in new_failures:
            entry = current[key]
            lines.append(
                f"- `{key.classname}#{key.name}` ({entry.status}) from `{entry.source}`"
            )
            if entry.message:
                lines.append(f"  - message: `{entry.message}`")

    lines.extend(["", "## Baseline Failures Still Present"])
    if not still_failing:
        lines.append("- none")
    else:
        for key in still_failing:
            entry = current[key]
            lines.append(
                f"- `{key.classname}#{key.name}` (baseline `{baseline[key]}`, current `{entry.status}`)"
            )

    lines.extend(["", "## Baseline Failures Currently Absent"])
    if not resolved:
        lines.append("- none")
    else:
        for key in resolved:
            lines.append(f"- `{key.classname}#{key.name}` (baseline `{baseline[key]}`)")

    return "\n".join(lines) + "\n"


def write_if_requested(path_value: str, content: str) -> None:
    if not path_value:
        return
    path = Path(path_value)
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")


def main() -> int:
    args = parse_args()
    workspace = Path(args.workspace).resolve()

    try:
        baseline_data = fetch_json(args.baseline_url)
    except Exception as exc:  # noqa: BLE001 - keep script robust in CI environments
        print(f"ERROR: Unable to fetch baseline from {args.baseline_url}: {exc}", file=sys.stderr)
        return 2

    baseline = collect_baseline_failures(baseline_data)
    current = collect_current_failures(workspace)

    current_keys: Set[TestKey] = set(current.keys())
    baseline_keys: Set[TestKey] = set(baseline.keys())

    new_failures = sorted_keys(current_keys - baseline_keys)
    still_failing = sorted_keys(current_keys & baseline_keys)
    resolved = sorted_keys(baseline_keys - current_keys)

    print(
        f"Current failing testcases: {len(current_keys)} | "
        f"Baseline failing testcases: {len(baseline_keys)} | "
        f"New failures: {len(new_failures)}"
    )
    if new_failures:
        print("New failures introduced:")
        for key in new_failures:
            item = current[key]
            print(f"- {key.classname}#{key.name} ({item.status}) [{item.source}]")

    markdown = build_markdown(
        baseline_url=args.baseline_url,
        current=current,
        baseline=baseline,
        new_failures=new_failures,
        still_failing=still_failing,
        resolved=resolved,
    )
    write_if_requested(args.markdown_output, markdown)

    if args.json_output:
        json_payload = {
            "generated_utc": datetime.now(timezone.utc).isoformat(),
            "baseline_url": args.baseline_url,
            "counts": {
                "current": len(current_keys),
                "baseline": len(baseline_keys),
                "new": len(new_failures),
                "still_present": len(still_failing),
                "baseline_absent": len(resolved),
            },
            "new_failures": [asdict(current[key]) for key in new_failures],
            "baseline_still_present": [
                {
                    "classname": key.classname,
                    "name": key.name,
                    "baseline_status": baseline[key],
                    "current_status": current[key].status,
                    "source": current[key].source,
                }
                for key in still_failing
            ],
            "baseline_absent": [
                {
                    "classname": key.classname,
                    "name": key.name,
                    "baseline_status": baseline[key],
                }
                for key in resolved
            ],
        }
        write_if_requested(args.json_output, json.dumps(json_payload, indent=2, sort_keys=True) + "\n")

    if args.fail_on_new and new_failures:
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
