# REST API v2 - Development Guide

This guide is specific to the **bibsonomy-rest-api-v2** module - the modern Kotlin REST API for BibSonomy.

> **Parent guide**: See `../CLAUDE.md` for project-wide guidance and architecture overview.

## Module Overview

**bibsonomy-rest-api-v2** is a Spring Boot 3 application written in Kotlin that provides a modern REST API for BibSonomy. It bridges to the legacy database layer while exposing a clean, well-typed API.

**Tech Stack**:
- Kotlin 1.9+
- Spring Boot 3.x
- Java 21 (with Java 23 toolchain for build)
- SpringDoc OpenAPI (API documentation)
- JUnit 5 + MockK (testing)

## Build & Run

### Prerequisites

- Java 23 toolchain configured (see `../toolchains.xml`)
- `JAVA_HOME` or `JAVA_23_HOME` environment variable set

### Build Commands

```bash
# From project root or this directory

# Build (skip tests)
JAVA_HOME=$JAVA_23_HOME mvn -Puse-toolchain-java23 -DskipTests -t ../toolchains.xml package

# Build with tests
JAVA_HOME=$JAVA_23_HOME mvn -Puse-toolchain-java23 -t ../toolchains.xml package

# Run tests only
mvn test

# Clean build
mvn clean package
```

### Run Locally

```bash
# Run with 'local' profile (uses local database config)
JAVA_HOME=$JAVA_23_HOME java -jar target/bibsonomy-rest-api-v2-4.1.0-SNAPSHOT.jar --spring.profiles.active=local

# Alternative: Use Spring Boot Maven plugin
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**Default ports**:
- API: `http://localhost:8080`
- OpenAPI docs: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Architecture & Patterns

### Layered Architecture

```
┌─────────────────────────────────────────┐
│ REST Controllers                        │  @RestController
│ - Request validation                    │  - Handle HTTP requests
│ - Response mapping                      │  - Return DTOs only
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│ Service Layer                           │  @Service
│ - Business logic orchestration          │  - Call LogicInterface
│ - Domain model → DTO mapping            │  - Handle Java nullability
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│ Legacy LogicInterface                   │  Injected from Spring XML
│ - Database operations                   │  - Returns domain models
│ - Transaction management                │  - Java 8 nullable types
└─────────────────────────────────────────┘
```

### Critical Patterns

#### 1. DTO Decoupling

**NEVER expose domain models** (`org.bibsonomy.model.*`) directly in the REST API.

```kotlin
// ❌ BAD - Exposes internal domain model
@GetMapping("/posts/{id}")
fun getPost(@PathVariable id: String): Post<Bookmark> {
    return logicInterface.getPost(id)  // WRONG!
}

// ✅ GOOD - Returns DTO
@GetMapping("/posts/{id}")
fun getPost(@PathVariable id: String): PostDto {
    val post = postService.getPost(id)
    return post  // Already a DTO
}
```

#### 2. Explicit Mapping (No Auto-Mapping)

**DO NOT use MapStruct, ModelMapper, or similar** auto-mapping libraries.

```kotlin
// DTOs live in: src/main/kotlin/.../dto/
data class PostDto(
    val id: String,
    val title: String,
    val url: String?,
    val description: String?,
    val tags: List<String>,
    val userName: String,
    val created: Instant
)

// Mapping functions as extension functions
fun Post<out Resource>.toPostDto(): PostDto {
    return PostDto(
        id = this.id ?: throw IllegalStateException("Post ID cannot be null"),
        title = this.resource?.title ?: "",
        url = (this.resource as? Bookmark)?.url,
        description = (this.resource as? Bookmark)?.description,
        tags = this.tags?.map { it.name } ?: emptyList(),
        userName = this.user?.name ?: "unknown",
        created = this.date?.toInstant() ?: Instant.now()
    )
}
```

#### 3. Service Layer Pattern

Services wrap `LogicInterface` and handle the bridge between legacy Java and modern Kotlin.

```kotlin
@Service
class PostService(
    private val logic: LogicInterface
) {
    fun getPost(userName: String, resourceHash: String): PostDto {
        // Build query params for legacy layer
        val grouping = GroupingEntity<Post<out Resource>>().apply {
            this.userName = userName
            this.resourceHash = resourceHash
        }

        // Call legacy layer
        val posts = logic.getPosts(grouping)

        // Handle nullable result (Java → Kotlin)
        val post = posts?.firstOrNull()
            ?: throw PostNotFoundException("Post not found: $userName/$resourceHash")

        // Map to DTO
        return post.toPostDto()
    }

    fun getPosts(
        userName: String? = null,
        tags: List<String>? = null,
        limit: Int = 20,
        offset: Int = 0
    ): List<PostDto> {
        val grouping = GroupingEntity<Post<out Resource>>().apply {
            this.userName = userName
            this.tags = tags?.map { Tag(it) }
            this.listEntity = ListEntity(limit, offset)
        }

        val posts = logic.getPosts(grouping) ?: emptyList()
        return posts.map { it.toPostDto() }
    }
}
```

#### 4. Controller Pattern

Controllers are thin - they delegate to services and handle HTTP concerns only.

```kotlin
@RestController
@RequestMapping("/api/v2/posts")
@Tag(name = "Posts", description = "Post management endpoints")
class PostController(
    private val postService: PostService
) {

    @GetMapping("/{userName}/{resourceHash}")
    @Operation(summary = "Get post by user and resource hash")
    fun getPost(
        @PathVariable userName: String,
        @PathVariable resourceHash: String
    ): ResponseEntity<PostDto> {
        val post = postService.getPost(userName, resourceHash)
        return ResponseEntity.ok(post)
    }

    @GetMapping
    @Operation(summary = "List posts with optional filters")
    fun listPosts(
        @RequestParam(required = false) userName: String?,
        @RequestParam(required = false) tags: List<String>?,
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int
    ): ResponseEntity<PostListDto> {
        val posts = postService.getPosts(userName, tags, limit, offset)
        return ResponseEntity.ok(PostListDto(posts, posts.size))
    }

    @PostMapping
    @Operation(summary = "Create a new post")
    fun createPost(
        @RequestBody @Valid request: CreatePostRequest,
        @RequestHeader("X-User") userName: String  // TODO: Replace with proper auth
    ): ResponseEntity<PostDto> {
        val post = postService.createPost(request, userName)
        return ResponseEntity.status(HttpStatus.CREATED).body(post)
    }
}
```

## Java Interop & Nullability

### Handling Legacy Java Nullability

Legacy domain models are Java 8 classes - **everything is potentially null**.

```kotlin
// ❌ BAD - Assumes non-null, will crash
fun Post<out Resource>.toDto(): PostDto {
    return PostDto(
        id = this.id,  // NullPointerException if null!
        title = this.resource.title  // Double NPE risk!
    )
}

// ✅ GOOD - Defensive nullability handling
fun Post<out Resource>.toDto(): PostDto {
    return PostDto(
        id = this.id ?: throw IllegalStateException("Post ID cannot be null"),
        title = this.resource?.title ?: "",
        url = (this.resource as? Bookmark)?.url,  // Safe cast + null check
        tags = this.tags?.map { it.name } ?: emptyList(),
        userName = this.user?.name ?: "unknown"
    )
}
```

### Safe Casting with Generics

```kotlin
// Handle Post<T extends Resource> safely
fun Post<out Resource>.toDto(): PostDto {
    return when (val res = this.resource) {
        is Bookmark -> PostDto(
            id = this.id ?: "",
            title = res.title ?: "",
            url = res.url,
            resourceType = "bookmark"
        )
        is BibTex -> PostDto(
            id = this.id ?: "",
            title = res.title ?: "",
            url = null,
            resourceType = "publication"
        )
        else -> throw IllegalArgumentException("Unknown resource type: ${res?.javaClass}")
    }
}
```

## Configuration & Spring Boot

### Spring XML Bridge

The REST API imports legacy Spring XML configuration to access `LogicInterface`.

```kotlin
@Configuration
@ImportResource("classpath:org/bibsonomy/database/bibsonomy-database-context.xml")
class DatabaseBridgeConfig {
    // LogicInterface bean is now available from legacy XML context
}
```

### Profiles

- **`local`**: Local development (uses local MySQL, mock auth)
- **`dev`**: Development logging (DEBUG for org.bibsonomy, org.springframework.web)
- **`test`**: Integration tests (uses test database)
- **`prod`**: Production (quieter logging: WARN root, INFO bibsonomy, WARN spring)

Profile-specific configs: `src/main/resources/application-{profile}.yml`

**Activate profiles:**
```bash
# Environment variable
export SPRING_PROFILES_ACTIVE=dev
java -jar target/bibsonomy-rest-api-v2-*.jar

# CLI flag
java -jar target/bibsonomy-rest-api-v2-*.jar --spring.profiles.active=dev

# Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Multiple profiles (comma-separated)
export SPRING_PROFILES_ACTIVE=local,dev
```

**Logging Levels by Profile:**
| Profile | root | org.bibsonomy | org.springframework.web |
|---------|------|---------------|-------------------------|
| (default) | INFO | INFO | INFO |
| dev | INFO | DEBUG | DEBUG |
| prod | WARN | INFO | WARN |

### CORS Configuration

CORS is configured for local development in `CorsConfig.kt`:

```kotlin
@Configuration
class CorsConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:5173")  // Vue dev server
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(true)
    }
}
```

## Testing Strategy

### Integration Tests (Preferred)

**Test actual HTTP endpoints** with real `LogicInterface` calls.

```kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PostControllerIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `GET post returns 200 with valid post data`() {
        val response = restTemplate.getForEntity(
            "/api/v2/posts/testuser/abc123",
            PostDto::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.userName).isEqualTo("testuser")
    }

    @Test
    fun `GET non-existent post returns 404`() {
        val response = restTemplate.getForEntity(
            "/api/v2/posts/testuser/nonexistent",
            ErrorResponse::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }
}
```

### Service Tests (When Needed)

Test service layer with real `LogicInterface` (injected from test context).

```kotlin
@SpringBootTest
@ActiveProfiles("test")
class PostServiceTest {

    @Autowired
    private lateinit var postService: PostService

    @Autowired
    private lateinit var logic: LogicInterface

    @Test
    fun `getPost returns DTO when post exists`() {
        // Arrange: Create test data via LogicInterface
        val testPost = createTestPost()
        logic.createPost(testPost, "testuser")

        // Act
        val result = postService.getPost("testuser", testPost.resource.hash)

        // Assert
        assertThat(result.userName).isEqualTo("testuser")
        assertThat(result.title).isEqualTo(testPost.resource.title)
    }
}
```

### Unit Tests (Minimal)

**Avoid excessive mocking**. Only unit test pure functions (e.g., mapping functions).

```kotlin
class PostMappingTest {

    @Test
    fun `toPostDto maps bookmark post correctly`() {
        val bookmark = Bookmark().apply {
            title = "Test Bookmark"
            url = "https://example.com"
        }
        val post = Post<Bookmark>().apply {
            id = "123"
            resource = bookmark
            user = User("testuser")
            date = Date()
        }

        val dto = post.toPostDto()

        assertThat(dto.id).isEqualTo("123")
        assertThat(dto.title).isEqualTo("Test Bookmark")
        assertThat(dto.url).isEqualTo("https://example.com")
    }
}
```

### Test Coverage Goals

- ✅ **All REST endpoints** (happy path + error cases)
- ✅ **Authentication/authorization flows** (when implemented)
- ✅ **Null-safety edge cases** (Java interop)
- ❌ **NOT** exhaustive unit test coverage (keep it practical)

## Common Development Tasks

### Adding a New REST Endpoint

1. **Create DTO** (if needed):
   ```bash
   src/main/kotlin/.../dto/YourDto.kt
   ```

2. **Create mapping function**:
   ```kotlin
   fun DomainModel.toDto(): YourDto = YourDto(...)
   ```

3. **Add service method** (or create new service):
   ```kotlin
   @Service
   class YourService(private val logic: LogicInterface) {
       fun getYourData(): YourDto {
           val data = logic.someMethod()
           return data.toDto()
       }
   }
   ```

4. **Create controller endpoint**:
   ```kotlin
   @RestController
   @RequestMapping("/api/v2/your-resource")
   class YourController(private val yourService: YourService) {
       @GetMapping
       fun get(): ResponseEntity<YourDto> {
           return ResponseEntity.ok(yourService.getYourData())
       }
   }
   ```

5. **Write integration test**:
   ```kotlin
   @SpringBootTest(webEnvironment = RANDOM_PORT)
   class YourControllerIntegrationTest {
       @Test
       fun `GET returns 200 with data`() { ... }
   }
   ```

6. **Test manually**:
   - Start app: `mvn spring-boot:run`
   - Check Swagger UI: http://localhost:8080/swagger-ui.html
   - Test endpoint: `curl http://localhost:8080/api/v2/your-resource`

### Working with Existing Endpoints

1. **Find the controller**: `src/main/kotlin/.../controller/`
2. **Check the service**: `src/main/kotlin/.../service/`
3. **Review DTOs**: `src/main/kotlin/.../dto/`
4. **Check integration tests**: `src/test/kotlin/.../`

### Debugging LogicInterface Calls

```kotlin
@Service
class PostService(private val logic: LogicInterface) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun getPosts(userName: String): List<PostDto> {
        val grouping = GroupingEntity<Post<out Resource>>().apply {
            this.userName = userName
        }

        logger.debug("Calling LogicInterface.getPosts with userName=$userName")
        val posts = logic.getPosts(grouping)
        logger.debug("Received ${posts?.size ?: 0} posts from LogicInterface")

        return posts?.map { it.toPostDto() } ?: emptyList()
    }
}
```

Enable debug logging: `application-local.yml`:
```yaml
logging:
  level:
    org.bibsonomy.rest: DEBUG
    org.bibsonomy.database: DEBUG
```

## Code Style & Best Practices

### Kotlin Style

```kotlin
// ✅ Data classes for DTOs
data class PostDto(
    val id: String,
    val title: String
)

// ✅ Immutable by default (val, not var)
val posts: List<PostDto> = service.getPosts()

// ✅ Constructor injection
@Service
class PostService(
    private val logic: LogicInterface
)

// ✅ Extension functions for mapping
fun Post<out Resource>.toDto(): PostDto = ...

// ✅ Named parameters for clarity
postService.getPosts(
    userName = "john",
    limit = 20,
    offset = 0
)
```

### Null Safety

```kotlin
// ✅ Use safe calls and Elvis operator
val title = post.resource?.title ?: "Untitled"

// ✅ Throw for truly unexpected nulls
val id = post.id ?: throw IllegalStateException("Post ID cannot be null")

// ✅ Use safe casts
val bookmark = resource as? Bookmark

// ❌ Avoid !! unless absolutely certain
val title = post.resource!!.title  // Will crash if null!
```

### HTTP Response Status Codes

```kotlin
// 200 OK - Successful GET/PUT
ResponseEntity.ok(data)

// 201 Created - Successful POST
ResponseEntity.status(HttpStatus.CREATED).body(data)

// 204 No Content - Successful DELETE
ResponseEntity.noContent().build()

// 400 Bad Request - Validation error
throw BadRequestException("Invalid input")

// 404 Not Found - Resource doesn't exist
throw PostNotFoundException("Post not found")

// 500 Internal Server Error - Unexpected error (let Spring handle)
```

## OpenAPI / Swagger Documentation

**Always document your endpoints** with SpringDoc annotations:

```kotlin
@RestController
@RequestMapping("/api/v2/posts")
@Tag(name = "Posts", description = "Post management operations")
class PostController(private val postService: PostService) {

    @GetMapping("/{userName}/{resourceHash}")
    @Operation(
        summary = "Get a specific post",
        description = "Retrieves a post by username and resource hash"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Post found"),
            ApiResponse(responseCode = "404", description = "Post not found")
        ]
    )
    fun getPost(
        @Parameter(description = "Username of post owner")
        @PathVariable userName: String,

        @Parameter(description = "SHA-256 hash of the resource")
        @PathVariable resourceHash: String
    ): ResponseEntity<PostDto> {
        return ResponseEntity.ok(postService.getPost(userName, resourceHash))
    }
}
```

Access docs at: http://localhost:8080/swagger-ui.html

## Common Gotchas

1. **LogicInterface transactions**: Don't start new transactions in service layer - `LogicInterface` manages them
2. **GroupingEntity complexity**: Building `GroupingEntity` can be tricky - check existing examples
3. **Resource type handling**: Remember `Post<T>` is generic - handle both `Bookmark` and `BibTex`
4. **Date conversions**: Legacy uses `java.util.Date` and Joda-Time - convert to `java.time.Instant` in DTOs
5. **Tag handling**: Tags are a `Set<Tag>` in domain model - map to `List<String>` in DTOs
6. **Authentication**: Currently mocked in `local` profile - will need proper implementation

## Error Handling

```kotlin
// Custom exceptions
class PostNotFoundException(message: String) : RuntimeException(message)
class InvalidPostException(message: String) : RuntimeException(message)

// Global exception handler
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(PostNotFoundException::class)
    fun handleNotFound(ex: PostNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(ex.message ?: "Not found"))
    }

    @ExceptionHandler(InvalidPostException::class)
    fun handleBadRequest(ex: InvalidPostException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(ex.message ?: "Invalid request"))
    }
}

data class ErrorResponse(val message: String)
```

## Performance Considerations

1. **Pagination**: Always support `limit` and `offset` for list endpoints
2. **Eager loading**: Be aware of lazy-loaded collections in domain models
3. **N+1 queries**: Check that legacy layer doesn't cause N+1 issues
4. **DTO mapping overhead**: Acceptable for cleanliness, but avoid nested loops

## Security (TODO)

Current state: **Authentication is mocked in local profile**

Future implementation needs:
- OAuth 2.0 / JWT token validation
- User context propagation to `LogicInterface`
- Permission checks for write operations
- Rate limiting

## Additional Resources

- **Parent guide**: `../CLAUDE.md` - Project-wide architecture and rules
- **Frontend guide**: `../bibsonomy-webapp-v2/CLAUDE.md` - How the frontend consumes this API
- **Legacy database**: `../bibsonomy-database/` - iBatis mappings and LogicInterface implementation
- **Domain model**: `../bibsonomy-model/` - Domain classes and LogicInterface definition
- **Spring Boot docs**: https://spring.io/projects/spring-boot
- **Kotlin docs**: https://kotlinlang.org/docs/home.html
- **SpringDoc OpenAPI**: https://springdoc.org/

---

**Remember**: This REST API is a bridge between modern Kotlin and legacy Java. Keep DTOs clean, handle nullability carefully, and test with real integrations.
