# Post Identifier Design

## Overview

The BibSonomy REST API v2 uses **resource hashes** (MD5 hex strings) as the primary identifier for posts, matching the legacy REST API v1 behavior. This document explains the design rationale, implementation, and usage.

## Identifier Format

**Type**: `String` (32-character lowercase hexadecimal MD5 hash)
**Pattern**: `^[a-f0-9]{32}$`
**Example**: `5d41402abc4b2a76b9719d911017c592`

## Hash Calculation

### For Bookmarks
```
hash = MD5(bookmark.url)
```
The hash is the MD5 checksum of the bookmark URL. This ensures:
- Same URL → Same hash (natural deduplication)
- Multiple users bookmarking the same URL share the same hash

### For Publications (BibTeX)
```
hash = MD5(title + authors + year + ...)
```
The hash is calculated from bibliographic fields (title, authors, year, etc.). This enables:
- Same publication → Same hash (deduplication across institutions)
- Different editions/versions may have different hashes

## API Usage

### Retrieving Posts

#### List posts (returns hashes)
```http
GET /api/v2/posts?user=alice&limit=10
```

Response:
```json
{
  "posts": [
    {
      "id": "a1b2c3d4e5f6789012345678901234ab",
      "user": {"username": "alice"},
      "resource": {
        "resourceType": "bookmark",
        "url": "https://example.com",
        "title": "Example",
        "urlHash": "a1b2c3d4e5f6789012345678901234ab"
      },
      ...
    }
  ]
}
```

**Note**: `PostDto.id` matches `resource.urlHash` for bookmarks and `resource.resourceHash` for publications.

#### Get specific post by hash
```http
GET /api/v2/posts/a1b2c3d4e5f6789012345678901234ab
```

#### Disambiguate with user parameter
If multiple users have the same resource (same hash):
```http
GET /api/v2/posts/a1b2c3d4e5f6789012345678901234ab?user=alice
```

### Relationship Endpoints

All relationship operations use hashes consistently:

#### Add reference
```http
POST /api/v2/community/a1b2c3...ab/references
Content-Type: application/json

{
  "targetPostId": "5d4140...92"
}
```

#### Remove reference
```http
DELETE /api/v2/community/a1b2c3...ab/references/5d4140...92
```

#### Add part-of relation
```http
POST /api/v2/community/a1b2c3...ab/part-of
Content-Type: application/json

{
  "parentPostId": "7e8f90...cd"
}
```

## Design Rationale

### Why Hash-Based Identifiers?

#### ✅ Advantages

1. **Natural Deduplication**
   - Same resource → Same hash
   - Works across users, institutions, systems
   - No duplicate entries for the same publication/bookmark

2. **RESTful Resource Identification**
   - Hash represents the resource itself, not a user's copy
   - Consistent with REST principles (resource = addressable entity)
   - Self-describing URLs

3. **Legacy API Compatibility**
   - Matches REST API v1 behavior (`/api/posts/{userName}/{resourceHash}`)
   - Existing clients can migrate with minimal changes
   - Familiar pattern for current users

4. **No Database Lookups for Shared Resources**
   - Hash is content-addressable
   - Can verify resource identity without DB queries
   - Efficient caching and CDN integration

5. **Client Simplicity**
   - Client receives hash in response → Can use it in subsequent requests
   - No need to extract different identifiers for different operations
   - Single identifier type across entire API

#### ⚠️ Trade-offs

1. **Ambiguity for Shared Resources**
   - **Problem**: Multiple users can have the same resource (same hash)
   - **Solution**: Optional `user` query parameter disambiguates
   - **Example**: `GET /api/v2/posts/{hash}?user={username}`
   - **Impact**: Minor - most use cases specify user context

2. **Hash Opacity**
   - **Problem**: Hash is not human-readable
   - **Mitigation**: Resource title/URL included in response
   - **Impact**: Minimal - IDs are for machines, not humans

### Why NOT Integer IDs?

**Rejected Alternative**: Using database `contentId` (integer) as identifier

#### ❌ Disadvantages

1. **No Deduplication**
   - Same publication added by 100 users → 100 different IDs
   - Relationship graph becomes fragmented
   - Citation analysis breaks down

2. **Breaks Legacy Compatibility**
   - Legacy API v1 uses hashes
   - Major breaking change for existing clients
   - Migration pain for established workflows

3. **Exposes Internal Database IDs**
   - Leaks implementation details
   - Sequential IDs can reveal creation order
   - Security/privacy concerns

4. **Prevents Content-Addressed Storage**
   - Can't use CDN/cache without DB lookup
   - Hash-based URLs enable efficient distribution
   - Integer IDs require central database

## Domain Model Mapping

### Domain Model (Java)
```java
public class Post<R extends Resource> {
    private Integer contentId;      // Database PK (internal use only)
    private R resource;             // Has interHash/intraHash
    private User user;
    private Date date;
    // ...
}

public abstract class Resource {
    private String interHash;       // Less specific (shared across users)
    private String intraHash;       // More specific (user-specific)
    // ...
}

public class Bookmark extends Resource {
    private String url;
    // intraHash = MD5(url)
    // interHash = intraHash (bookmarks use same for both)
}

public class BibTex extends Resource {
    private String title;
    private List<PersonName> author;
    // intraHash = MD5(title + authors + year + ...)
    // interHash = MD5(title + authors + ...)  (less specific)
}
```

### DTO Mapping (Kotlin)
```kotlin
data class PostDto(
    val id: String,                 // = resource.interHash ?? resource.intraHash
    val user: UserRefDto,
    val resource: ResourceDto,      // BookmarkDto or BibTexDto
    // ...
)

data class BookmarkDto(
    val url: String,
    val title: String,
    val urlHash: String             // = intraHash (matches PostDto.id)
) : ResourceDto

data class BibTexDto(
    val resourceHash: String,       // = interHash ?? intraHash (matches PostDto.id)
    val bibtexKey: String?,
    val title: String,
    // ...
) : ResourceDto
```

### Mapping Logic
```kotlin
fun Post<out Resource>.toDto(): PostDto {
    val resourceHash = resource.interHash ?: resource.intraHash
        ?: throw IllegalStateException("Resource must have hash")

    return PostDto(
        id = resourceHash,           // Hash becomes primary identifier
        user = user.toRefDto(),
        resource = resource.toDto(),
        // ...
    )
}
```

## Migration Guide

### For API Clients

**Before (hypothetical v2 with integer IDs):**
```javascript
// Get posts
const response = await fetch('/api/v2/posts?user=alice');
const post = response.data.posts[0];

// BROKEN: Can't use integer ID to fetch details
// Integer ID: post.id = 12345
// Endpoint expects hash: GET /api/v2/posts/{hash}
```

**After (current v2 with hashes):**
```javascript
// Get posts
const response = await fetch('/api/v2/posts?user=alice');
const post = response.data.posts[0];

// ✅ WORKS: Use hash directly
const hash = post.id;  // "a1b2c3d4e5f6..."
await fetch(`/api/v2/posts/${hash}?user=alice`);

// ✅ WORKS: Hash matches resource hash
const bookmarkHash = post.resource.urlHash;
const publicationHash = post.resource.resourceHash;
// bookmarkHash === post.id (for bookmarks)
// publicationHash === post.id (for publications)
```

### For Frontend Developers

**Constructing URLs:**
```typescript
// List view: Extract hash from post
interface Post {
  id: string;  // Hash, not integer
  user: { username: string };
  resource: Bookmark | BibTex;
}

// Detail view: Use hash directly
function viewPostDetails(post: Post) {
  const url = `/posts/${post.id}?user=${post.user.username}`;
  navigate(url);
}

// Relationship operations
function addReference(sourceHash: string, targetHash: string) {
  return api.post(`/community/${sourceHash}/references`, {
    targetPostId: targetHash  // String hash, not integer
  });
}
```

## Consistency Validation

All post identifiers are now consistent throughout the API:

| Location | Type | Example |
|----------|------|---------|
| `PostDto.id` | `String` | `"a1b2c3...ab"` |
| `BookmarkDto.urlHash` | `String` | `"a1b2c3...ab"` |
| `BibTexDto.resourceHash` | `String` | `"a1b2c3...ab"` |
| `postIdParam` (path) | `String` | `/posts/a1b2c3...ab` |
| `targetPostId` (body) | `String` | `{"targetPostId": "a1b2..."}` |
| `targetPostId` (path) | `String` | `/references/a1b2c3...ab` |
| `parentPostId` (body) | `String` | `{"parentPostId": "a1b2..."}` |
| `parentPostId` (path) | `String` | `/part-of/a1b2c3...ab` |
| `PostRefDto.id` | `String` | `"a1b2c3...ab"` |

**Validation pattern**: `^[a-f0-9]{32}$` (32-character MD5 hex)

## Technical Implementation

### Fail-Fast Validation

The mapper validates hash presence at DTO creation:

```kotlin
fun Post<out Resource>.toDto(): PostDto {
    val resourceHash = resource.interHash ?: resource.intraHash
        ?: throw IllegalStateException(
            "Post resource must have a hash (interHash or intraHash) " +
            "(contentId: $contentId, user: ${user.name}, resourceType: ${resource.javaClass.simpleName})"
        )
    // ...
}
```

**Benefits:**
- Prevents invalid DTOs from being created
- Fails at service layer (before response serialization)
- Clear error messages for debugging
- No silent data corruption

### Hash Fallback Strategy

```kotlin
val hash = resource.interHash ?: resource.intraHash
```

**Rationale:**
- `interHash`: Less specific, shared across users (preferred for deduplication)
- `intraHash`: More specific, user-specific (fallback)
- Both are MD5 hashes, same format
- Consistent with legacy API behavior

## See Also

- [OpenAPI Specification](./openapi.yaml) - Full API documentation
- [Legacy API Documentation](../../bibsonomy-rest-server/docs/) - REST API v1 reference
- [Domain Model](../../bibsonomy-model/src/main/java/org/bibsonomy/model/) - Java domain classes
- [DTO Package](../src/main/kotlin/org/bibsonomy/api/dto/) - Kotlin DTOs

## Questions?

**Q: Can I use integer IDs anywhere?**
A: No. All post identifiers in API v2 are hash strings. The internal `contentId` is never exposed.

**Q: What if the hash is ambiguous (multiple users)?**
A: Use the `user` query parameter: `GET /api/v2/posts/{hash}?user={username}`

**Q: Are hashes stable?**
A: Yes. Hash = MD5(resource content). Same content → Same hash, always.

**Q: Can hashes collide?**
A: Extremely unlikely (MD5 collision probability ≈ 2^-128). If concerned, the API validates uniqueness at creation.

**Q: Why not UUIDs?**
A: Content-addressable hashes enable deduplication. UUID = random, no content relationship.

**Q: Is this RESTful?**
A: Yes. Hash identifies the resource itself (not a user's copy), matching REST principles.
