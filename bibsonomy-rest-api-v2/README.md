# BibSonomy REST API v2

Modern Kotlin-based REST API for BibSonomy, built with Spring Boot 3.x and Java 21.

## Overview

This module provides a clean, JSON-first REST API v2 that:
- Reuses legacy `bibsonomy-model` and `bibsonomy-database` modules via JAR dependencies
- Uses DTOs decoupled from domain models
- Implements OAuth2/JWT authentication
- Provides OpenAPI 3.0 specification
- Follows modern RESTful conventions

## Architecture

```
REST API v2 (Spring Boot 3.x, Kotlin)
    ↓ (adapts)
Legacy Database Layer (Spring 3.2, iBatis)
    ↓
MySQL Database
```

## Key Design Principles

1. **JSON-first** - Default response format is JSON (XML optional for legacy compatibility)
2. **Versioned URLs** - All endpoints use `/api/v2/...`
3. **DTO Decoupling** - Never expose domain models directly; explicit mapping required
4. **Integration Testing** - REST-level tests with real HTTP endpoints
5. **Null-Safety** - Kotlin null-safety enforced throughout

## Technology Stack

- **Kotlin 1.9+**
- **Java 21**
- **Spring Boot 3.2+**
- **Jackson (with Kotlin module)**
- **SpringDoc OpenAPI 3.0**

## Documentation

- [OpenAPI Specification](docs/openapi.yaml) - Complete API specification
- [Feature Scope](docs/FEATURE_SCOPE.md) - MVP vs deferred features
- [Removed Features](docs/REMOVED_FEATURES.md) - Features excluded from v2 with codebase references
- [Deferred Features](docs/DEFERRED_FEATURES.md) - Features deferred to post-MVP with codebase references

## Build & Run

**Build:**
```bash
mvn clean install
```

**Run locally:**
```bash
mvn spring-boot:run
```

**Run tests:**
```bash
mvn test
```

**Generate OpenAPI docs:**
```bash
# After starting the application, visit:
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
```

## API Documentation

Once running, interactive API documentation is available at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml

## Project Structure

```
bibsonomy-rest-api-v2/
├── docs/                           # Documentation
│   ├── openapi.yaml                # OpenAPI 3.0 specification
│   ├── FEATURE_SCOPE.md            # MVP feature scope
│   ├── REMOVED_FEATURES.md         # Excluded features reference
│   └── DEFERRED_FEATURES.md        # Post-MVP features reference
├── src/main/kotlin/
│   └── org/bibsonomy/api/v2/
│       ├── Application.kt          # Spring Boot main
│       ├── config/                 # Configuration classes
│       │   ├── DatabaseConfig.kt   # Bridge to legacy DB layer
│       │   ├── SecurityConfig.kt   # OAuth2/JWT config
│       │   └── OpenApiConfig.kt    # OpenAPI configuration
│       ├── controllers/            # REST controllers
│       │   ├── PostsController.kt
│       │   ├── UsersController.kt
│       │   ├── TagsController.kt
│       │   └── GroupsController.kt
│       ├── dto/                    # API DTOs (request/response)
│       │   ├── PostDto.kt
│       │   ├── UserDto.kt
│       │   ├── TagDto.kt
│       │   └── GroupDto.kt
│       ├── services/               # Service layer (adapts LogicInterface)
│       │   ├── PostService.kt
│       │   ├── UserService.kt
│       │   └── TagService.kt
│       ├── mappers/                # Domain ↔ DTO mapping
│       │   ├── PostMapper.kt
│       │   ├── UserMapper.kt
│       │   └── TagMapper.kt
│       └── exceptions/             # Custom exceptions
│           └── ApiExceptions.kt
└── src/test/kotlin/
    └── org/bibsonomy/api/v2/
        ├── controllers/            # REST integration tests
        └── services/               # Service integration tests
```

## Authentication

API v2 uses **OAuth2/JWT** authentication (not HTTP Basic Auth like v1).

**OpenID Connect** is supported for external authentication providers.

**Legacy compatibility:** Until token issuance is finished, v2 accepts the legacy v1 Basic + API-key header (`Authorization: Basic base64(username:apikey)`) and propagates the authenticated user into `LogicInterface`.

**Note:** LDAP and SAML were supported in legacy system but removed in v2. If needed, these can be re-added via custom authentication providers.

## MVP Feature Scope

See [FEATURE_SCOPE.md](docs/FEATURE_SCOPE.md) for complete details.

**Core Features:**
- Posts (bookmarks + publications)
- Tagging (free-form, recommendations, related tags)
- Users (registration, profile, settings)
- Groups (create, join, manage, hierarchy)
- Search & Discovery (full-text, filters, popular posts)
- Import/Export (all formats: JSON, BibTeX, CSV, RSS, EndNote, CSL, etc.)
- Documents (PDF upload/download)
- BibTeX key navigation
- Gold Standard/Community Posts

**Removed Features:**
- Friends/Followers social networking
- Clipboard/Saved Searches
- Recommendations engine
- Post sharing via Inbox

**Deferred to Post-MVP:**
- CRIS (research information system)
- External service synchronization
- Statistics pages
- Concepts/Relations (tag hierarchies)

## Development

See [CLAUDE.md](../CLAUDE.md) in repository root for development guidelines.

**Key patterns:**

1. **Database access via LogicInterface:**
   ```kotlin
   @Service
   class PostService(private val logic: LogicInterface) {
       fun getPosts(params: PostQueryParams): List<PostDto> {
           val posts = logic.getPosts(buildGroupingEntity(params))
           return posts.map { it.toDto() }
       }
   }
   ```

2. **Explicit DTO mapping:**
   ```kotlin
   fun Post<out Resource>.toDto(): PostDto {
       return PostDto(
           id = this.id ?: throw IllegalStateException("Post ID required"),
           title = this.resource?.title ?: "",
           // ... explicit field mapping
       )
   }
   ```

3. **Integration testing:**
   ```kotlin
   @SpringBootTest(webEnvironment = RANDOM_PORT)
   class PostControllerIntegrationTest {
       @Test
       fun `GET posts returns 200 with post list`() {
           // Test actual HTTP endpoints
       }
   }
   ```

## License

LGPL 3.0 (matches parent project)
