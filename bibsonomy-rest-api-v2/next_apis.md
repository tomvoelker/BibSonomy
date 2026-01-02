# Next APIs Needed

This document tracks the REST API endpoints that need to be implemented to support the frontend features. It's organized by priority based on what's needed for the homepage and core user flows.

## Currently Implemented

| Endpoint | Description | Status |
|----------|-------------|--------|
| `GET /api/v2/posts` | List posts (bookmarks & publications) with filtering | Done |
| `GET /api/v2/posts/{postId}` | Get post details by resource hash | Done |
| `GET /api/v2/tags` | List tags with frequency data (tag cloud) | Done |

## Priority 1: Authentication & User Session

These are needed for the Login, Register, and Logout buttons in the Jumbotron and navigation.

### `POST /api/v2/auth/login`
**Purpose**: Authenticate user with username/password and return a session token.

**Request Body**:
```json
{
  "username": "string",
  "password": "string"
}
```

**Response** (200 OK):
```json
{
  "token": "jwt-or-session-token",
  "user": {
    "name": "string",
    "realName": "string",
    "email": "string",
    "groups": ["string"]
  },
  "expiresAt": "ISO-8601 timestamp"
}
```

**Error Responses**:
- 401 Unauthorized: Invalid credentials
- 400 Bad Request: Missing required fields

---

### `POST /api/v2/auth/logout`
**Purpose**: Invalidate the current session/token.

**Headers**:
- `Authorization: Bearer <token>`

**Response** (204 No Content): Success

---

### `GET /api/v2/auth/me`
**Purpose**: Get the currently authenticated user's details.

**Headers**:
- `Authorization: Bearer <token>`

**Response** (200 OK):
```json
{
  "name": "string",
  "realName": "string",
  "email": "string",
  "groups": ["string"],
  "apiKey": "string (optional, for API access)"
}
```

**Error Responses**:
- 401 Unauthorized: No valid session

---

### `POST /api/v2/users`
**Purpose**: Register a new user account.

**Request Body**:
```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "realName": "string (optional)"
}
```

**Response** (201 Created):
```json
{
  "name": "string",
  "email": "string",
  "created": "ISO-8601 timestamp"
}
```

**Error Responses**:
- 400 Bad Request: Validation errors
- 409 Conflict: Username or email already exists

---

## Priority 2: User Profiles & Posts

These are needed for viewing user profile pages and their posts.

### `GET /api/v2/users/{username}`
**Purpose**: Get a user's public profile information.

**Response** (200 OK):
```json
{
  "name": "string",
  "realName": "string (if public)",
  "postCount": 123,
  "tagCount": 456,
  "groups": ["string"],
  "registered": "ISO-8601 timestamp"
}
```

**Error Responses**:
- 404 Not Found: User doesn't exist

---

### `GET /api/v2/users/{username}/tags`
**Purpose**: Get a user's tags (for user-specific tag cloud).

**Query Parameters**:
- `limit` (int, default: 50): Maximum tags to return
- `minFreq` (int, optional): Minimum usage frequency

**Response** (200 OK):
```json
[
  {
    "name": "string",
    "count": 123
  }
]
```

---

## Priority 3: Tag Pages

These are needed when clicking on a tag in the tag cloud or on posts.

### `GET /api/v2/tags/{tagName}`
**Purpose**: Get details about a specific tag including related tags.

**Response** (200 OK):
```json
{
  "name": "string",
  "count": 123,
  "relatedTags": [
    {
      "name": "string",
      "count": 45
    }
  ]
}
```

Note: Posts with this tag can be fetched via `GET /api/v2/posts?tags={tagName}` (already implemented).

---

## Priority 4: Post Management (CRUD)

These are needed for authenticated users to create, edit, and delete posts.

### `POST /api/v2/posts`
**Purpose**: Create a new post (bookmark or publication).

**Headers**:
- `Authorization: Bearer <token>`

**Request Body** (Bookmark):
```json
{
  "resourceType": "bookmark",
  "url": "https://example.com",
  "title": "string",
  "description": "string (optional)",
  "tags": ["tag1", "tag2"],
  "visibility": "public|private|group",
  "groups": ["group1 (if visibility=group)"]
}
```

**Request Body** (Publication/BibTeX):
```json
{
  "resourceType": "bibtex",
  "bibtex": "@article{...}",
  "tags": ["tag1", "tag2"],
  "visibility": "public|private|group"
}
```

**Response** (201 Created): PostDto

---

### `PUT /api/v2/posts/{postId}`
**Purpose**: Update an existing post.

**Headers**:
- `Authorization: Bearer <token>`

**Request Body**: Same as POST, but partial updates allowed

**Response** (200 OK): Updated PostDto

**Error Responses**:
- 403 Forbidden: Not the post owner
- 404 Not Found: Post doesn't exist

---

### `DELETE /api/v2/posts/{postId}`
**Purpose**: Delete a post.

**Headers**:
- `Authorization: Bearer <token>`

**Response** (204 No Content): Success

**Error Responses**:
- 403 Forbidden: Not the post owner
- 404 Not Found: Post doesn't exist

---

## Priority 5: Search

### `GET /api/v2/search`
**Purpose**: Full-text search across posts, users, and tags.

**Query Parameters**:
- `q` (string, required): Search query
- `type` (string, default: "all"): "posts", "users", "tags", or "all"
- `limit` (int, default: 20)
- `offset` (int, default: 0)

**Response** (200 OK):
```json
{
  "posts": [PostDto],
  "users": [UserDto],
  "tags": [TagDto],
  "totalPosts": 123,
  "totalUsers": 45,
  "totalTags": 67
}
```

Note: Basic search is already supported via `GET /api/v2/posts?search=...` but a dedicated endpoint allows more flexibility.

---

## Priority 6: Groups

### `GET /api/v2/groups`
**Purpose**: List groups the current user is a member of.

**Headers**:
- `Authorization: Bearer <token>`

**Response** (200 OK):
```json
[
  {
    "name": "string",
    "description": "string",
    "memberCount": 12,
    "isPrivate": false
  }
]
```

---

### `GET /api/v2/groups/{groupName}`
**Purpose**: Get group details.

**Response** (200 OK):
```json
{
  "name": "string",
  "description": "string",
  "memberCount": 12,
  "isPrivate": false,
  "postCount": 456
}
```

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
