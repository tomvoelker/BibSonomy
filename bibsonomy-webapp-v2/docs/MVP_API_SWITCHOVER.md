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
- Implemented endpoints: `GET /api/v2/posts`, `GET /api/v2/posts/{postId}` only. No tags endpoints.
- Response shape: `PaginatedPostList{items[], totalCount, offset, limit}` where each item has `resource` nested (`bookmark | bibtex`), `tags{name,count,countPublic}`, `groups{name,displayName}`, `user{username,realName}`, `createdAt/updatedAt`, `visibility`.
- No CORS config; browser calls from Vite (5173) would need proxy or CORS allowed.
- Auth: GET posts is permitted anonymously; Basic auth supported for private data.

### Frontend
- Default is mocked: `VITE_ENABLE_MOCKS=true` starts MSW (`src/mocks/handlers/posts.ts`), so no real HTTP yet.
- Expected shape: `{ posts: [...], pagination{total,offset,limit} }`; each post has top-level `resourceType (publication|bookmark)`, `title`, `url`, `bibTexData`, `user{id,name}`, `groups[{id,name}]`, `tags[{name,globalCount}]`, `createdAt/updatedAt`. Some components read `post.date`.
- Sidebar tags are hardcoded in `src/components/layout/Sidebar.vue`.

---

## Backend Work (align to OpenAPI and homepage needs)
1) **Posts list/detail shape**
   - Add response DTO that matches OpenAPI and frontend needs: wrap as `posts` + `pagination` (total, offset, limit).
   - Flatten resource fields: map `resourceType` to `publication|bookmark`, expose `title`, `url`, `bibTexData` fields, keep ids as strings if needed.
   - Map tag counts to `globalCount`/`countPublic` or adjust frontend accordingly.
   - Files: `PostMapper.kt` (mapping), `PostsController.kt` (response type), potentially a new adapter DTO.

2) **Tags endpoint**
   - Implement `GET /api/v2/tags` (and optionally `/tags/{tagname}`) returning at least `{ name, count/globalCount }`.
   - Source data: legacy `logic.getTags`/statistics or a minimal popular-tags query; for MVP, even a stubbed limited list via logic is fine.
   - Files: new controller `TagsController.kt`, service, mapper if needed.

3) **CORS**
   - Allow `http://localhost:5173` (and optional `http://localhost:4173`) on `/api/v2/**`.
   - Add configuration bean (e.g., `@Bean fun corsFilter()` or `WebMvcConfigurer`) under `bibsonomy-rest-api-v2/src/main/kotlin/org/bibsonomy/api/config`.

4) **Auth**
   - Keep GET posts/tags anonymous per spec; no change required for homepage. Ensure 401 handling remains consistent if auth is added later.

5) **Validation**
   - Run `mvn spring-boot:run` from `bibsonomy-rest-api-v2`.
   - `curl http://localhost:8080/api/v2/posts?limit=5` → confirm shape matches new DTO.
   - `curl http://localhost:8080/api/v2/tags?limit=20` → confirm tag list.

---

## Frontend Work
1) **Disable mocks and point to API**
   - In `.env.development`: set `VITE_ENABLE_MOCKS=false`, ensure `VITE_API_BASE_URL=http://localhost:8080/api/v2` (or use relative `/api/v2` with proxy/CORS).
   - Entry point: `src/main.ts` (mock toggle).

2) **Shape alignment**
   - Update `src/types/models.ts` to match the API DTOs (resource polymorphism, tag counts, pagination fields).
   - In `src/api/posts.ts`, map `PaginatedPostList` → UI model if backend stays as `items/totalCount`; or change Zod schemas to expect the new wrapped shape if backend is adjusted.
   - Remove/replace usage of `post.date` in components (`PostMeta.vue`) with `createdAt`.
   - Ensure `resourceType` uses API values (`bibtex|bookmark`), or translate to `publication|bookmark` in a mapper.

3) **Tags consumption**
   - Replace hardcoded tags in `src/components/layout/Sidebar.vue` with data from `GET /api/v2/tags`.
   - Add a simple fetch (Vue Query) and fallback to static tags on error.

4) **Error/loading UX**
   - Keep existing loading states; add minimal error handling (console/log or banner) if API fails.

5) **Validation**
   - Run frontend with mocks off: `VITE_ENABLE_MOCKS=false bun dev` (or `npm run dev`).
   - Confirm homepage shows real posts split into bookmarks/publications and the tag cloud comes from API.
   - Verify no 404/CORS errors in the browser console.

---

## Acceptance Checklist
- Backend returns posts in agreed shape (list + pagination, resource fields flattened/mapped).
- Backend exposes `GET /api/v2/tags` with real data.
- CORS allows the Vite dev origin (or a dev proxy is configured).
- Frontend uses real API (mocks disabled) and renders posts and tags without runtime errors.
- Basic smoke: `GET /api/v2/posts` works anonymous; homepage loads with real data.***
