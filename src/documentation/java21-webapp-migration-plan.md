# Java 21 Webapp Migration Analysis And Plan

Date: 2026-03-02  
Branch: `codex/codex-vibes-a-migration`

## Scope

This document captures:

1. The current Java 21 migration status for `bibsonomy-webapp`.
2. The failure baseline comparison against the Java 8 Bertha report.
3. The remaining technical gaps to make the webapp "really Java 21 native".
4. An iterative migration plan in realistic chunks.

## Baseline Comparison (Java 8 vs Java 21)

Baseline report source:

- `https://bertha.cs.uni-kassel.de/bib-static/master/latest/testresults/widgets/summary.json`
- `https://bertha.cs.uni-kassel.de/bib-static/master/latest/testresults/data/suites.json`

Extracted Java 8 failing/broken tests in baseline: `14` (Bertha snapshot from 2025-09-11).

After the Java 21 CI rerun on 2026-03-02 with fresh DB setup:

- Current failing tests: `12`
- New failures introduced by Java 21 migration: `0`
- Baseline failures still present: `12`
- Previously failing baseline tests that are now passing: `2`
  - `org.bibsonomy.layout.util.JabRefModelConverterTest#testDecode`
  - `org.bibsonomy.recommender.tag.simple.TagsOfPreviousPostsTagRecommenderTest#testRecommendation`

Generated diff artifacts:

- `target/java21-baseline-diff.md`
- `target/java21-baseline-diff.json`

Comparison utility:

- `misc/scripts/compare_failures_against_bertha.py`

## Regressions Fixed During Migration

Only newly introduced regressions were addressed:

1. `org.bibsonomy.scraper.junit.RemoteTestAssertTest#compareDifferentBibtexStringUrlsButEqualRedirectedUrl`
   - Cause: redirect target drift and `bibsonomy.org` DDOS/protection effects.
   - Fix: switched test URL pair to stable redirect on `biblicious.org`.

2. `tags.FunctionsTest#testGetDate`
   - Cause: locale provider behavior change (`new Locale("gb")`) under modern JDKs.
   - Fix: use deterministic `Locale.US` in assertion path.

3. `org.bibsonomy.util.QRCodeRendererTest#testTemplatePDF`
   - Cause: legacy jPod dependency requires `sun.misc.Service` (not available on modern JDKs).
   - Fix: conditional test assumption; test skips when unsupported runtime class is absent.

## Current Java 21 Reality

### What works now

- Full project compiles on Java 21.
- `bibsonomy-webapp` packages on Java 21 (`clean package`).
- CI-style test suite runs with a fresh MariaDB container and produces deterministic failure set.
- Local smoke startup for webapp on Java 21 passes:
  - `misc/scripts/run_webapp_smoke_local.sh`
  - `org.bibsonomy.webapp.WebappStartupSmokeTest`
- GitHub workflow includes:
  - GitLab-style CI run,
  - Bertha baseline diff check (fails only on new regressions),
  - dedicated Java 21 webapp startup smoke job.
- No repository-level `--add-opens` usage remains in scripts/workflows.

### What is still legacy / high risk

1. Legacy application framework layer:
   - Spring `3.2.17.RELEASE`
   - Spring Security `3.2.9.RELEASE`
   - Old Spring Security SAML extension snapshot in webapp.

2. Legacy persistence stack:
   - iBATIS 2 (`ibatis-sqlmap`) is still foundational across database/search/recommender/opensocial modules.
   - iBATIS CGLIB enhancement had to be disabled to avoid module-access failures.

3. Legacy PDF/QR dependency:
   - `jPodRenderer 5.3` path is not Java 21 compatible without internal JDK classes.

4. Aging dependency surface:
   - log4j 1.x
   - old MySQL connector line
   - mixed old servlet/jsp/tomcat-era test stack
   - mixed `commons-dbcp` and `commons-dbcp2` legacy

These are the core reasons the system can run/build on Java 21, but is not yet on a modern long-term-stable platform footing.

### Observed runtime warnings in smoke startup

- Embedded Tomcat 7 shutdown on Java 21 triggers reflective cleanup warnings (`InaccessibleObjectException` in `java.lang` / `sun.rmi.transport` during stop).
- Elasticsearch connection is attempted during startup and fails in local smoke when ES is unavailable (currently non-fatal for smoke success).
- Recommender schema mismatch (`recommender_status.local` expected by recommender-core vs `type` in seeded schema) was observed; smoke DB setup now applies a compatibility `local` column so startup succeeds.
- Full HTML page rendering checks against `/` currently fail under embedded Tomcat 7 + Java 21 due JSP/tagx function-prefix resolution (`fn` in tag files). This is now a tracked blocker for chunk 2.

## Iterative Plan (No `--add-opens`, realistic chunks)

### Chunk 1 (done): Baseline guard and migration stabilization

- Establish Java 8 baseline failure set from Bertha report.
- Ensure only real regressions are fixed.
- Keep local CI-style runner deterministic with fresh DB setup.

### Chunk 2 (in progress): Runtime smoke for real webapp operation on Java 21

- Done:
  - dedicated smoke path that starts embedded webapp on Java 21 with fresh MariaDB,
  - smoke wired into GitHub Actions as dedicated job,
  - recommender DB bootstrap compatibility fix for embedded startup (`local` column bridge in smoke DB setup).
- Remaining in this chunk:
  - replace current auth-level API check with a DB-backed functional login check (seeded test user),
  - ensure smoke validates a page path that requires successful DB-backed rendering, not only auth handshake,
  - resolve JSP/tagx `fn` function resolution failures in embedded Tomcat 7 on Java 21 so rendered pages can be used as stable smoke assertions.

Outcome target: "webapp actually runs with DB-backed behavior", not only startup/auth filter response.

### Chunk 3 (done): Replace Java-incompatible QR/PDF runtime

- Replaced jPod-based QR embedding implementation with Apache PDFBox rendering/content-stream path.
- Removed temporary JDK8-only `sun.misc.Service` test assumption; QR tests now execute on Java 21.
- Verified QR module tests and full local CI baseline comparison (`New failures: 0`).

Outcome target: QR functionality restored natively on Java 21.

### Chunk 4: iBATIS migration by bounded domains

- Introduce MyBatis 3 side-by-side migration track.
- Migrate one bounded domain at a time (start with search or a limited DB manager set).
- Keep SQL parity checks and query result snapshots during transitions.

Outcome target: remove iBATIS 2 and CGLIB enhancement reliance.

### Chunk 5: Spring/Security modernization (two-step)

- Step 5a: Spring/Security uplift to maintained versions with minimal API churn first.
- Step 5b: move to Jakarta-aligned stack where required by runtime/container goals.
- Replace deprecated SAML extension with maintained equivalent.

Outcome target: framework supportable on Java 21 long-term.

### Chunk 6: Dependency hardening and cleanup

- Remove log4j1 and other unmaintained transitive runtime baggage.
- Align JDBC/datasource stack; remove old `commons-dbcp` leftovers.
- Update old servlet/jsp/test container dependencies where still required.

Outcome target: reduced security and maintenance risk.

### Chunk 7: Final hardening gates

- Remove temporary compatibility workarounds and assumptions.
- Enforce CI checks that detect reintroduction of module-access hacks.
- Keep baseline-diff reporting for stability visibility.

Outcome target: Java 21-native operation with clean CI signal.

## Exit Criteria For "Really Works With Java 21"

The migration should be considered complete only when all are true:

1. Webapp runtime smoke passes on Java 21 in CI and local.
2. No dependency on JDK internals (`sun.*`) in runtime features.
3. No iBATIS 2/CGLIB enhancement dependency remains.
4. Framework and security stack are on maintained lines.
5. No `--add-opens`/module-access workarounds are required.
