# BibSonomy REST API v2

Modern Kotlin-based REST API for BibSonomy, built with Spring Boot 3.x and Java 21.

## Overview

This module provides a clean, JSON-first REST API v2 that:
- Reuses legacy `bibsonomy-model` and `bibsonomy-database` modules via JAR dependencies
- Uses DTOs decoupled from domain models
- Implements OAuth2/JWT authentication
- Provides OpenAPI 3.0 specification
- Follows modern RESTful conventions

## Requirements

### Minimum Versions

- **Java 21** or higher (required)
- **Maven 3.6+** (recommended)
- **MySQL 5.7+** (database)

### Java Version Requirement

⚠️ **Important**: This module requires **Java 21** as the minimum version.

The REST API v2 is built on Spring Boot 3.2+, which requires Java 17 as an absolute minimum. We target Java 21 to leverage:
- Virtual Threads (Project Loom) for improved concurrency
- Pattern Matching enhancements
- Record patterns and sealed classes
- Modern JDK security and performance improvements

**Compatibility Note**: The legacy BibSonomy modules (bibsonomy-model, bibsonomy-database) still require Java 8. This module bridges the gap using Maven toolchains to build with both Java 8 (legacy) and Java 21 (REST API v2) in the same project.

### Build Tool Configuration

When building this module, ensure you have Java 21 configured:

```bash
# Set JAVA_HOME to Java 21
export JAVA_HOME=/path/to/java-21

# Or use Maven toolchains (recommended)
mvn -t ../toolchains.xml clean install
```

See `toolchains.xml` in the repository root for multi-JDK build configuration.

## Architecture

```
REST API v2 (Spring Boot 3.x, Kotlin)
    ↓ (adapts)
Legacy Database Layer (Spring 3.2, iBatis)
    ↓
MySQL Database
```

## Framework & Infrastructure Changes

This module represents a major technology upgrade from the legacy BibSonomy system. Understanding these changes is critical for migration and development.

### Core Framework Migration

| Component | Legacy System | REST API v2 | Notes |
|-----------|--------------|-------------|-------|
| **Java Version** | Java 8 | **Java 21** | Minimum requirement; enables modern language features |
| **Spring Framework** | Spring 3.2.x | **Spring Boot 3.2.x** | Major version jump; auto-configuration enabled |
| **Language** | Java | **Kotlin 1.9+** | Null-safety, concise syntax, coroutines support |
| **ORM** | iBatis 2.x | iBatis 2.x (reused) | Legacy database layer unchanged |
| **API Style** | REST v1 (XML-first) | REST v2 (JSON-first) | Modern RESTful conventions |
| **Auth** | HTTP Basic Auth | **OAuth2/JWT** | Token-based authentication |
| **API Docs** | None | **OpenAPI 3.0** | Interactive Swagger UI |
| **Servlet** | Servlet 2.5 | **Servlet 6.0** (Jakarta) | Jakarta EE namespace migration |
| **Build** | Maven (Java 8) | **Maven Toolchains** | Multi-JDK build support |

### Key Technology Upgrades

**Spring Boot 3.2+**
- Auto-configuration for most components
- Embedded Tomcat server (no WAR deployment needed)
- Actuator for health checks and metrics
- Native support for OpenAPI documentation
- Jakarta EE namespace (`javax.*` → `jakarta.*`)

**Kotlin 1.9+**
- Null-safety enforced at compile time
- Data classes for DTOs (automatic equals/hashCode/copy)
- Extension functions for cleaner mapping code
- Coroutines ready (not used in MVP but available)

**OAuth2/JWT Authentication**
- Stateless token-based authentication
- No session management required
- OpenID Connect support for external providers
- Removed: HTTP Basic Auth, LDAP, SAML (can be re-added if needed)

**Jackson JSON Processing**
- Kotlin module for native Kotlin support
- Automatic null handling
- ISO-8601 date formatting by default
- Polymorphic type handling for Post<Resource>

### Database Layer Compatibility

⚠️ **Important**: The REST API v2 **reuses** the legacy database layer without modification:

- `bibsonomy-model` (Java 8) - Domain models unchanged
- `bibsonomy-database` (Java 8) - iBatis XML queries unchanged
- `LogicInterface` - Business logic interface preserved

This design allows:
- Zero database migration required
- Gradual feature migration from legacy to new API
- Both systems can run side-by-side accessing the same database
- Proven business logic is preserved and tested

**Trade-off**: Kotlin null-safety must handle legacy nullable types carefully using `?.`, `!!`, and null checks.

### Breaking Changes from Legacy API v1

**Authentication**
- Old: HTTP Basic Auth on every request
- New: OAuth2 token-based (get token once, reuse)

**Response Format**
- Old: XML by default, JSON optional
- New: JSON by default, XML removed (can be re-added)

**URL Structure**
- Old: `/api/users/{username}/posts`
- New: `/api/v2/users/{username}/posts` (versioned URLs)

**Error Responses**
- Old: Custom XML error format
- New: Standard JSON error format with problem details

**Removed Features** (see [REMOVED_FEATURES.md](docs/REMOVED_FEATURES.md))
- Friends/Followers social networking
- Clipboard/Saved Searches
- Recommendation engine endpoints
- LDAP/SAML authentication (OAuth2 only)

### Migration Path

For teams migrating from legacy API v1 to v2:

1. **Update client authentication** from Basic Auth to OAuth2/JWT
2. **Change base URL** from `/api/` to `/api/v2/`
3. **Parse JSON responses** instead of XML
4. **Handle new error format** (JSON problem details)
5. **Update JDK** to Java 21 for building/running
6. **Review removed features** and plan alternatives if needed

See the [OpenAPI specification](docs/openapi.yaml) for complete API reference.

## Key Design Principles

1. **JSON-first** - Default response format is JSON (XML optional for legacy compatibility)
2. **Versioned URLs** - All endpoints use `/api/v2/...`
3. **DTO Decoupling** - Never expose domain models directly; explicit mapping required
4. **Integration Testing** - REST-level tests with real HTTP endpoints
5. **Null-Safety** - Kotlin null-safety enforced throughout

## Technology Stack

- **Kotlin 1.9.25** (language)
- **Java 21** (minimum required JDK)
- **Spring Boot 3.2.12** (framework)
- **Jackson** with Kotlin module (JSON processing)
- **SpringDoc OpenAPI 3.0** (API documentation)
- **Maven 3.6+** with toolchains (build tool)

## Documentation

- [OpenAPI Specification](docs/openapi.yaml) - Complete API specification
- [Feature Scope](docs/FEATURE_SCOPE.md) - MVP vs deferred features
- [Removed Features](docs/REMOVED_FEATURES.md) - Features excluded from v2 with codebase references
- [Deferred Features](docs/DEFERRED_FEATURES.md) - Features deferred to post-MVP with codebase references

## Build & Run

⚠️ **Prerequisite**: Ensure Java 21 is installed and `JAVA_HOME` is set correctly.

**Build:**
```bash
# Standard build (requires JAVA_HOME=Java 21)
mvn clean install

# Or use toolchains for multi-JDK environment
JAVA_HOME=$JAVA_23_HOME mvn -t ../toolchains.xml clean install
```

**Run locally:**
```bash
# Standard run
mvn spring-boot:run

# Or run the JAR directly
java -jar target/bibsonomy-rest-api-v2-4.1.0.jar

# With specific Spring profile
java -jar target/bibsonomy-rest-api-v2-4.1.0.jar --spring.profiles.active=local
```

**Run tests:**
```bash
mvn test

# Skip tests during build
mvn clean install -DskipTests
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
