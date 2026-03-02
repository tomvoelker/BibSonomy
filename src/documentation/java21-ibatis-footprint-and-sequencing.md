# Java 21 iBATIS Footprint And Sequencing

Date: 2026-03-02  
Branch: `codex/codex-vibes-a-migration`

## Snapshot

This snapshot was collected from the current branch after the bounded logging migration.

`bibsonomy-logging` has already been removed from iBATIS runtime wiring:

- no `SqlMapClientFactoryBean` in webapp logging servlet config,
- no `BibLog.xml` / `SqlMapConfigLogger.xml`,
- JDBC insert via `LoggingDatabaseManager`.

## Remaining iBATIS Surface (by module)

Counts are based on repository grep:

- `import com.ibatis` in Java sources,
- `<!DOCTYPE sqlMap...>` mapping XMLs,
- `<!DOCTYPE sqlMapConfig...>` config XMLs,
- `SqlMapClientFactoryBean` Spring bean definitions.

| Module | Java `com.ibatis` imports | `sqlMap` XML files | `sqlMapConfig` XML files | `SqlMapClientFactoryBean` defs |
| --- | ---: | ---: | ---: | ---: |
| `bibsonomy-database-common` | 33 | 0 | 0 | 0 |
| `bibsonomy-database` | 2 | 60 | 2 | 2 |
| `bibsonomy-search` | 0 | 42 | 15 | 5 |
| `bibsonomy-recommender` | 0 | 10 | 3 | 2 |
| `bibsonomy-opensocial` | 2 | 2 | 1 | 0 |
| `bibsonomy-webapp` | 0 | 0 | 0 | 2 |

## Remaining Spring iBATIS Wiring

- `bibsonomy-database/src/main/resources/org/bibsonomy/bibsonomy-database-context.xml`
  - `mainSqlMapClient`
- `bibsonomy-search/src/main/resources/org/bibsonomy/*.xml`
  - post/person/group/project/crislink search clients
- `bibsonomy-recommender/src/main/resources/org/bibsonomy/*.xml`
  - recommender main/log clients
- `bibsonomy-webapp/src/main/webapp/WEB-INF/`
  - `bibsonomy-servlet-database.xml` (`slaveSqlMapClient`)
  - `bibsonomy-servlet-opensocial.xml` (`oAuthSqlMapClient`)

## Risk Notes

1. `bibsonomy-database-common` is the critical anchor.
   It contains the DB session abstraction (`DBSessionImpl`) and many iBATIS type handlers, including references to iBATIS internals.
2. Search/recommender use many independent SQL-map contexts.
   They are better bounded migration candidates than the core database context.
3. The webapp still instantiates iBATIS clients for slave DB and OAuth/opensocial.
   These should be moved only after downstream contexts are migrated.

## Iterative Sequencing (Realistic Chunks)

### Chunk A (done): Remove smallest runtime island

- Done in this branch: logging moved from iBATIS to JDBC.
- Validation gate:
  - Java 21 smoke passes,
  - full local CI baseline diff reports `New failures: 0`.

### Chunk B: Add side-by-side MyBatis infrastructure

- Add MyBatis 3 + mybatis-spring dependencies without removing iBATIS yet.
- Introduce a parallel session factory path for one bounded domain only.
- Keep existing iBATIS contexts untouched outside pilot scope.
- Validation gate:
  - pilot context boots,
  - no regression in baseline diff.

### Chunk C: Migrate `bibsonomy-search` first

Why first:

- many isolated SQL contexts,
- high iBATIS XML count but less central transaction semantics than main DB logic.

Plan:

1. migrate one search context (e.g., project/crislink info) to MyBatis mapper XML,
2. keep read result parity checks against current SQL-map outputs,
3. migrate remaining search contexts in sequence.

Validation gate:

- search module tests stay at baseline behavior,
- no new failures in global baseline diff.

### Chunk D: Migrate recommender and opensocial contexts

- Recommender SQL-map clients (`main` and `log`) migrated next.
- OpenSocial SQL-map config migrated after recommender.
- Validation gate:
  - recommender and opensocial tests stable against baseline.

### Chunk E: Migrate core database session layer

This is the hardest step:

- replace iBATIS-backed `DBSessionImpl`/factory path in `bibsonomy-database-common`,
- migrate type handlers and statement execution semantics,
- then migrate `bibsonomy-database` SQL maps.

Validation gate:

- database module tests remain at baseline failure set only,
- no new failures in full project baseline diff.

### Chunk F: Remove iBATIS stack completely

- remove `ibatis-sqlmap`,
- delete `SqlMapClientFactoryBean` usage across all modules,
- remove iBATIS XML configs and obsolete adapters.

Validation gate:

- zero iBATIS references in source/config,
- Java 21 smoke + CI baseline diff green (no new failures).

## Guardrails For Every Chunk

1. Keep all migrations bounded to one domain at a time.
2. Always run:
   - `misc/scripts/run_webapp_smoke_local.sh`
   - `misc/scripts/run_ci_tests_local.sh`
   - `misc/scripts/compare_failures_against_bertha.py --fail-on-new`
3. Reject changes that increase `New failures` above `0`.
