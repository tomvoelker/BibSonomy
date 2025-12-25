# MVP Homepage: Switch from Mocks to REST API v2

Plan to make the homepage work against the real `bibsonomy-rest-api-v2` following `bibsonomy-rest-api-v2/docs/openapi.yaml`.

---

## References
- API spec: `bibsonomy-rest-api-v2/docs/openapi.yaml` (see `/api/v2/posts`, `/api/v2/tags`)
- Backend code: `bibsonomy-rest-api-v2/src/main/kotlin/org/bibsonomy/api/controller/PostsController.kt`, `PostService.kt`, `PostMapper.kt`
- Frontend API: `bibsonomy-webapp-v2/src/api/posts.ts`, `src/types/models.ts`
- Frontend data usage: `src/pages/HomePage.vue`, `src/components/layout/Sidebar.vue`
- Env config: `bibsonomy-webapp-v2/.env.development`

---

## Current State

### Backend
- Implemented endpoints: `GET /api/v2/posts`, `GET /api/v2/posts/{postId}`, `GET /api/v2/tags`.
- Response shape (per OpenAPI + current mapper): `PaginatedPostList{items[], totalCount, offset, limit}` where each item has:
  - `resource` with discriminator `resourceType: bookmark|bibtex`
  - `tags{name,count,countPublic}` (mapped from `Tag.globalcount/usercount`)
  - `groups{name,displayName}`, `user{username,realName}`, `createdAt/updatedAt`, `visibility`, `id` (contentId)
- CORS configured for `http://localhost:5173` and `http://localhost:4173` on `/api/v2/**`.
- Auth: GET posts is permitted anonymously; Basic auth supported for private data.

### Frontend
- Default dev config uses real API: `VITE_ENABLE_MOCKS=false` in `.env.development`.
- UI models aligned to OpenAPI: `PaginatedPostList{items,totalCount,offset,limit}`, nested `resource` with `resourceType: bibtex|bookmark`, `UserRefDto`, and `TagDto` fields.
- Sidebar tag cloud uses `GET /api/v2/tags` (limit 50) with a static fallback on error and alphabetical display order.

### Mismatches to Resolve
- None for MVP scope; remaining work is validation against a running backend.

---

## Backend Work (align to OpenAPI and homepage needs)
1) **Posts list/detail shape**
   - Keep `PaginatedPostList` and `PostDto` per OpenAPI; confirm required fields used by homepage are present in `PostMapper.kt`.
   - If frontend prefers flat fields, add a frontend adapter layer instead of changing API shape.
   - Files to verify: `bibsonomy-rest-api-v2/src/main/kotlin/org/bibsonomy/api/mapper/PostMapper.kt`, `bibsonomy-rest-api-v2/src/main/kotlin/org/bibsonomy/api/controller/PostsController.kt`.

2) **Tags endpoint**
   - ✅ Implemented `GET /api/v2/tags` returning `{ name, count, countPublic }` using legacy-style selection (frequency-based, max 50 when no minFreq is set).
   - Files: `bibsonomy-rest-api-v2/src/main/kotlin/org/bibsonomy/api/controller/TagsController.kt`, `bibsonomy-rest-api-v2/src/main/kotlin/org/bibsonomy/api/service/TagService.kt`.

3) **CORS**
   - ✅ Allow `http://localhost:5173` and `http://localhost:4173` on `/api/v2/**`.
   - Files: `bibsonomy-rest-api-v2/src/main/kotlin/org/bibsonomy/api/config/ApiCorsConfig.kt`, `bibsonomy-rest-api-v2/src/main/kotlin/org/bibsonomy/api/config/SecurityConfig.kt`.

4) **Auth**
   - Keep GET posts/tags anonymous per spec; no change required for homepage. Ensure 401 handling remains consistent if auth is added later.

5) **Validation**
   - Run `mvn spring-boot:run` from `bibsonomy-rest-api-v2`.
   - `curl http://localhost:8080/api/v2/posts?limit=5` → confirm shape matches new DTO.
   - `curl http://localhost:8080/api/v2/tags?limit=20` → confirm tag list.
   - ✅ Verified `mvn -Puse-toolchain-java23 -DskipTests -t ./toolchains.xml -f bibsonomy-rest-api-v2/pom.xml package` and app startup with `--spring.profiles.active=local` (port 8080).
   - ✅ Verified `GET /api/v2/posts?limit=1` and `GET /api/v2/tags?limit=5` return OpenAPI-compatible JSON.

---

## Frontend Work
1) **Disable mocks and point to API**
   - ✅ Set `VITE_ENABLE_MOCKS=false` and `VITE_API_BASE_URL=http://localhost:8080/api/v2` in `.env.development`.
   - Entry point: `src/main.ts` (mock toggle).

2) **Shape alignment**
   - ✅ Updated `src/types/models.ts` to match API DTOs (nested `resource`, `resourceType: bibtex|bookmark`, `TagDto.count/countPublic`, `UserRefDto.username/realName`, pagination fields).
   - ✅ UI now consumes `PaginatedPostList{items,totalCount,offset,limit}` directly and uses `createdAt` in `PostMeta.vue`.
   - Files: `bibsonomy-webapp-v2/src/types/models.ts`, `bibsonomy-webapp-v2/src/pages/HomePage.vue`, `bibsonomy-webapp-v2/src/components/post/PostMeta.vue`.
   - ✅ Homepage now queries `resourceType=bookmark` and `resourceType=bibtex` separately to match legacy per-type recency.

3) **Tags consumption**
   - ✅ Sidebar pulls tags from `GET /api/v2/tags` via Vue Query with a static fallback on error and alphabetical ordering.
   - Files: `bibsonomy-webapp-v2/src/components/layout/Sidebar.vue`, `bibsonomy-webapp-v2/src/api/tags.ts`, `bibsonomy-webapp-v2/src/composables/useTags.ts`.

4) **Error/loading UX**
   - Keep existing loading states; add minimal error handling (console/log or banner) if API fails.

5) **Validation**
   - Run frontend with mocks off: `VITE_ENABLE_MOCKS=false bun dev` (or `npm run dev`).
   - Confirm homepage shows real posts split into bookmarks/publications and the tag cloud comes from API.
   - Verify no 404/CORS errors in the browser console.
   - ✅ `npm run type-check` passes.
   - ✅ `npm run dev` starts Vite on `http://localhost:5173/`.

---

## Acceptance Checklist
- Backend returns posts in OpenAPI shape (`items/totalCount/offset/limit`, `resourceType` discriminator).
- Backend exposes `GET /api/v2/tags` with real data.
- CORS allows the Vite dev origin (or a dev proxy is configured).
- Frontend uses real API (mocks disabled) and renders posts and tags without runtime errors.
- Basic smoke: `GET /api/v2/posts` works anonymous; homepage loads with real data.
