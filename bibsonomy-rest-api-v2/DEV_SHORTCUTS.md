# REST API v2 – Bootstrapping Shortcuts and Caveats

This document captures the intentional shortcuts/stubs we introduced to get the modern Spring Boot API running on top of the legacy BibSonomy stack. Use it as a checklist for unwinding stubs and restoring real functionality.

## Why these stubs exist
- The legacy XML bean graph depends on webapp-only components, plugins, templating, file logic, and search providers that are not wired here.
- We needed the service to start and serve responses quickly to validate the Kotlin/API layer.
- Stubs are **temporary scaffolding**: they bypass missing pieces and disable certain features to allow the app to boot.

## Inventory of shortcuts (severity noted)
- ~~**Dummy admin user (High)**~~ **REMOVED**  
  - Location: `LegacyLogicConfig.kt` **(replaced by request-scoped LogicInterface via auth)**  
  - What: Injected a synthetic admin `User` into `DBLogic` so permission checks don’t NPE.  
  - Impact: All calls ran as admin; masked real auth/permissions. Now replaced by legacy Basic+API-key auth.

- ~~**System tags stubbed out (High)**~~ **PARTIALLY RESTORED**  
  - Location: `LegacyBeanAliasesConfig.kt`  
  - What: SystemTagFactory now wires search/markup tags; executable tags remain empty to avoid legacy DB dependencies for now.  
  - Impact: Search/markup system tag behaviors are re-enabled; executable system tags remain disabled.

- ~~**Validator/File logic stubs (High)**~~ **PARTIALLY RESTORED**  
  - Location: `LegacyBeanAliasesConfig.kt`  
  - What: Validators use real legacy beans; file logic uses `DummyFileLogic` to avoid the servlet-only `ServerFileLogic`.  
  - Impact: Validation is real; file handling remains minimal (no uploads/previews).

- **Permission specialUserTagMap parsed (Medium)**  
  - Location: `LegacyBeanAliasesConfig.kt`  
  - What: Parses `system.specialUsersTagMap` (fallback empty) and applies it to `permissionDatabaseManager`.  
  - Impact: Special user tag behavior restored when properties are provided; still falls back to empty if missing.

- **Plugin registry no-op (Medium/High)**  
  - Location: `LegacyPluginStubConfig.kt`  
  - What: Replaces `databasePluginManager` with empty `DatabasePluginRegistry`.  
  - Impact: Legacy plugins/jobs are not executed.

- **Gold-standard stubs (Medium)**  
  - Location: `LegacyGoldStandardStubConfig.kt`  
  - What: Gold-standard chains/managers/info service replaced with no-ops.  
  - Impact: Gold-standard features disabled.

- **Search/metadata stubs (Medium)**  
  - Locations: `LegacySearchConfig.kt`, `bibsonomy-search-elasticsearch-lite-context.xml`, `NoOpResourceSearch.java`.  
  - What: Elasticsearch/search DB wiring is replaced by no-op searchers; avoids broken legacy search bytecode and ES dependency.  
  - Impact: Search endpoints return empty results; metadata providers use empty search; re-enable by swapping back to the real search contexts.

- **Total count optional (Low)**  
  - Locations: `PostsController.kt`, `PostService.kt`  
  - What: `includeTotal=false` by default; only computes `getPostStatistics` when the flag is true.  
  - Impact: Default responses avoid the heavy full-result count (expensive on millions of posts). Clients must opt-in to `includeTotal=true` when they truly need totalCount.

- **Post detail by hash + owner (Low)**  
  - Locations: `PostsController.kt`, `PostService.kt`, OpenAPI  
  - What: `/api/v2/posts/{postId}` uses the legacy `getPostDetails(hash, owner)`; `postId` is the resource hash (intra/inter), not the numeric contentId, and `user` query param disambiguates the owner.  
  - Impact: Numeric contentId lookup is not implemented; callers must supply the hash (and owner for private posts). Future work would need a DB/API hook to fetch by contentId.

- **Resource-type fallback (Low/Medium)**  
  - Locations: `PostsController.kt`, `PostService.kt`  
  - What: Accepts `resourceType`/`resourcetype`, defaults to `all`, coerces unknown to `bibtex`.  
  - Impact: `all` merges bibtex+bookmarks in-memory (sorted/limited in code) to avoid legacy mixed-type query support; total counts are optional and expensive (`includeTotal=true`).

## Progress snapshot (posts)
- List posts: implemented with optional auth (public by default, private when authenticated), sorting, tags/search, resourceType all/bibtex/bookmark, optional `includeTotal`.
- Post detail: implemented via hash + owner; anonymous can see public; authenticated owner can see private. No contentId lookup yet.

- ~~**Security allow-all (High)**~~ **REMOVED**  
  - Location: `SecurityConfig.kt`  
  - What: All requests permitted; no real auth.  
  - Impact: Unsafe for anything beyond local dev. Now replaced by legacy Basic+API-key auth enforcement.

## How to unwind (suggested order)
1) **Restore real auth**: replace permit-all; propagate the actual authenticated user into `DBLogic` (remove dummy admin).  
2) **System tags**: wire real executable/search/markup tags (import or re-create minimal necessary ones) instead of empty sets. *(Search/markup restored; executable still deferred due to missing dependencies.)*  
3) **Validators & file logic**: swap stubs for real validators and file logic; load proper configuration (paths, limits).  
4) **Permission config**: populate `specialUserTagMap` with real values.  
5) **Plugins**: reintroduce required database plugins and remove the no-op registry.  
6) **Gold-standard**: restore gold-standard chains/managers/info service if needed.  
7) **Search/metadata**: wire real search beans and metadata providers; remove empty stubs. **(Done)**  
8) **Resource-type handling**: enforce/handle “all” properly or return 400 on unsupported types instead of forcing bibtex.  
9) **Hygiene**: add integration tests (posts bibtex/bookmark, auth-required paths), health checks, and remove temporary warnings.

## Current behavior to expect
- API boots and serves `/api/v2/posts` with legacy Basic+API-key auth (dummy admin removed).  
- Search/markup system tag processing restored; executable system tags still disabled.  
- Real validation and file handling in place; plugin side effects and gold-standard features still disabled.  
- Search beans are no-op; metadata providers are fed by the stub searchers (empty results).  
- Resource type defaults to bibtex unless explicitly set to bookmark.

Keep this list updated as you remove stubs and wire real components.***
