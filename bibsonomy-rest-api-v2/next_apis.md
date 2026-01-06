# Next APIs Needed

This document tracks the REST API endpoints that need to be implemented to support the frontend features. It's organized by priority based on what's needed for the homepage and core user flows.

## Currently Implemented

| Endpoint | Description | Status |
|----------|-------------|--------|
| `GET /api/v2/posts` | List posts (bookmarks & publications) with filtering | ✅ Done |
| `GET /api/v2/posts/{postId}` | Get post details by resource hash | ✅ Done |
| `POST /api/v2/posts` | Create a new post (bookmark or publication) | ✅ Done |
| `PUT /api/v2/posts/{postId}` | Update an existing post | ✅ Done |
| `DELETE /api/v2/posts/{postId}` | Delete a post | ✅ Done |
| `GET /api/v2/tags` | List tags with frequency data (tag cloud) | ✅ Done |
| `GET /api/v2/tags/{tagName}` | Get tag details with related tags | ✅ Done |
| `POST /api/v2/auth/login` | Authenticate user with username/API key | ✅ Done |
| `POST /api/v2/auth/logout` | Invalidate session (no-op for stateless auth) | ✅ Done |
| `GET /api/v2/auth/me` | Get currently authenticated user | ✅ Done |
| `POST /api/v2/users` | Register a new user | ✅ Done |
| `GET /api/v2/users/{username}` | Get user's public profile | ✅ Done |
| `GET /api/v2/users/{username}/tags` | Get user's tags | ✅ Done |
| `GET /api/v2/search` | Full-text search across posts, users, tags | ✅ Done |
| `GET /api/v2/groups` | List authenticated user's groups | ✅ Done |
| `GET /api/v2/groups/{groupName}` | Get group details | ✅ Done |

## API Details

All endpoints listed above are now implemented. Below are the original specifications for reference.

---

### Authentication Endpoints (Priority 1) ✅

All authentication endpoints are implemented:
- `POST /api/v2/auth/login` - Uses Basic Auth with username + API key
- `POST /api/v2/auth/logout` - No-op for stateless auth
- `GET /api/v2/auth/me` - Returns authenticated user details
- `POST /api/v2/users` - Registers new user with password hashing

---

### User Endpoints (Priority 2) ✅

User profile and tag endpoints are implemented:
- `GET /api/v2/users/{username}` - Returns profile with post/tag counts
- `GET /api/v2/users/{username}/tags` - Returns user's tags sorted by frequency

---

### Tag Endpoints (Priority 3) ✅

Tag details endpoint implemented:
- `GET /api/v2/tags/{tagName}` - Returns tag with related tags

---

### Post CRUD Endpoints (Priority 4) ✅

Full CRUD operations for posts:
- `POST /api/v2/posts` - Create bookmark or publication
- `PUT /api/v2/posts/{postId}` - Update post (owner only)
- `DELETE /api/v2/posts/{postId}` - Delete post (owner only)

---

### Search Endpoint (Priority 5) ✅

Full-text search implemented:
- `GET /api/v2/search?q=...` - Search across posts, users, and tags

---

### Group Endpoints (Priority 6) ✅

Group listing and details implemented:
- `GET /api/v2/groups` - List authenticated user's groups
- `GET /api/v2/groups/{groupName}` - Get group details with post count

---

## Implementation Notes

### Authentication Strategy
The legacy system uses session-based auth. For the new API, consider:
1. **JWT tokens** - Stateless, works well with SPA
2. **Session cookies** - Simpler, but requires CORS configuration
3. **API keys** - For programmatic access (already exists in legacy)

Recommendation: Use JWT tokens for the SPA with an optional API key fallback for programmatic access.

### Legacy LogicInterface Methods
Most of these endpoints can be implemented using existing `LogicInterface` methods:

| Endpoint | LogicInterface Method |
|----------|----------------------|
| User profile | `getUsers(GroupingEntity)` |
| User tags | `getTags(GroupingEntity)` with user filter |
| Create post | `createPost(Post, String)` |
| Update post | `updatePost(Post, String)` |
| Delete post | `deletePost(String, String)` |
| Search | `getPosts(GroupingEntity)` with search param |
| Groups | `getGroups(GroupingEntity)` |

### Frontend Consumers
These APIs are consumed by the Vue 3 frontend (`bibsonomy-webapp-v2`):
- `usePosts.ts` - Posts composable
- `useTags.ts` - Tags composable
- `useAuth.ts` - Authentication composable (to be created)
- `useUsers.ts` - User profiles composable (to be created)

---

## Changelog

- **2026-01-02**: Initial document created during homepage cleanup sprint
