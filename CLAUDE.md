# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

BibSonomy is a social bookmark and publication sharing system developed since 2006. It allows users to share and organize both web bookmarks and bibliographic publications using a tagging system.

**License**: LGPL 3.0 (most modules), AGPL 3.0 (webapp), GPL (bibtex-parser)

## ⚡ Modernization in Progress

**Read this first**: BibSonomy is undergoing a gradual modernization. The codebase contains:

1. **Legacy System** (Java 8, Spring 3.2, iBatis) - **Maintained but NOT modernized**
2. **New Modern System** (Kotlin, Java 21, Spring Boot 3, Vue 3) - **Active development**

See `.cursor/plans/bibsonomy_modernization_strategy_50e37204.plan.md` for full details.

### Current Architecture Strategy

```
┌─────────────────────────────────────┐
│ NEW: Vue 3 + TypeScript Frontend    │  (bibsonomy-frontend)
│ (Vite build tool, vue-i18n)         │
│ (@tanstack/vue-query, Tailwind CSS) │
└──────────────┬──────────────────────┘
               │ REST API
┌──────────────▼──────────────────────┐
│ NEW: Kotlin REST API v2              │  (bibsonomy-rest-api-v2)
│ (Spring Boot 3.x, Java 21, Kotlin)   │
└──────────────┬──────────────────────┘
               │ Reuses via JAR deps
┌──────────────▼──────────────────────┐
│ LEGACY: Core Domain & Database       │  (bibsonomy-model, bibsonomy-database)
│ (Java 8, iBatis, Spring 3.2)         │  (bibsonomy-common)
└──────────────────────────────────────┘
               │
┌──────────────▼──────────────────────┐
│ MySQL Database                       │
└──────────────────────────────────────┘

PARALLEL OPERATION:
┌──────────────────────────────────────┐
│ LEGACY: JSP/Spring MVC Webapp        │  (bibsonomy-webapp)
│ (Kept operational, not modernized)   │
└──────────────────────────────────────┘
```

**Key Principle**: New code in Kotlin/Vue; legacy code preserved as-is for stability.

## Code Quality & Testing Philosophy

### Testing Strategy (Critical - Read This!)

**Integration over Unit Tests**: Prefer integration-style tests that verify real behavior over brittle mocks.

**For Legacy Modules** (`bibsonomy-database`, `bibsonomy-webapp`, etc.):
- ✅ Integration tests that pin behavior of legacy modules
- ✅ Tests that verify actual database interactions work
- ❌ **DO NOT** create brittle mocks of iBatis mappers
- ❌ **DO NOT** nag about outdated dependencies or Java version (Java 8 is required)
- ❌ **DO NOT** suggest framework rewrites or dependency upgrades unless blocking correctness/security

**For New REST API v2** (`bibsonomy-rest-api-v2`):
- ✅ REST-level integration tests (test actual HTTP endpoints)
- ✅ Service-level integration tests (test service layer with real LogicInterface)
- ✅ Cover error paths and authentication flows
- ✅ Test null-safety and Kotlin contracts
- ❌ Avoid excessive unit mocks - test real behavior

**For Frontend** (`bibsonomy-frontend`):
- ✅ Minimal testing focused on critical interactions and regressions
- ✅ Test user flows and API integration
- ❌ **DO NOT** create exhaustive snapshot test suites
- ❌ Keep test burden light - this is AI-generated code with clear patterns

### Code Quality Standards

**For New Kotlin Code** (`bibsonomy-rest-api-v2`):
- ✅ **Enforce null-safety** - use non-nullable types by default
- ✅ **DTOs decoupled from domain models** - never expose domain POJOs directly in API
- ✅ **Explicit mapping** between domain models and DTOs (no auto-mapping magic)
- ✅ **Consistent HTTP semantics** (proper status codes, error responses)
- ✅ **Java 8 interop** when calling legacy modules (handle nullability, use `.kt` extension functions carefully)
- ✅ **Constructor injection** preferred over field injection
- ✅ **Immutable data classes** for DTOs and value objects
- ✅ **Thorough integration tests** for all API surface

**For Frontend Vue Code**:
- ✅ TypeScript with strict mode
- ✅ Vue 3 SFCs with Composition API
- ✅ @tanstack/vue-query for server state management
- ✅ Pinia for shared UI/client state (auth, prefs) when needed
- ✅ vue-i18n for translations; semantic HTML and ARIA for a11y
- ✅ Zod (or lightweight validators) for runtime API validation
- ✅ Headless UI + Tailwind (or UnoCSS) for accessible, lean UI primitives
- ✅ Focus on correctness and API integration over test coverage

**For Legacy Code** (`bibsonomy-database`, `bibsonomy-webapp`):
- ✅ Preserve backward compatibility - **DO NOT** break existing APIs
- ✅ No schema or SQL changes unless explicitly requested
- ✅ Watch for transaction and cache side effects
- ✅ Ensure MySQL portability and no data loss
- ✅ Check for XSS risks in JSP outputs
- ✅ Keep taglibs and i18n behavior intact
- ❌ **DO NOT** suggest modernization or refactoring
- ❌ **DO NOT** upgrade dependencies unless blocking a bugfix

### Security Checks

- ✅ No credentials/tokens/API keys in code (except test fixtures)
- ✅ Check for XSS vulnerabilities in web outputs
- ✅ Validate authentication/authorization in new endpoints
- ✅ SQL injection protection (already handled by iBatis, but verify in new code)

## Build Commands

### Maven (Legacy Java Modules)

**Full Build**:
```bash
mvn clean install
```

**Build without tests**:
```bash
mvn clean install -DskipTests
```

**Run tests**:
```bash
mvn test
```

**Run tests with coverage**:
```bash
# Prepare agent and run tests
mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install -DskipTests
mvn test org.jacoco:jacoco-maven-plugin:report-aggregate

# Coverage report: coverage/target/site/jacoco-aggregate/index.html
```

**Single module tests**:
```bash
mvn test -pl bibsonomy-<module-name>
```

**Specific test class**:
```bash
mvn test -pl bibsonomy-<module-name> -Dtest=TestClassName
```

**Specific test method**:
```bash
mvn test -pl bibsonomy-<module-name> -Dtest=TestClassName#testMethodName
```

**Deploy legacy webapp to Tomcat** (if applicable):
```bash
mvn -f bibsonomy-webapp/pom.xml tomcat7:deploy
# or to redeploy:
mvn -f bibsonomy-webapp/pom.xml tomcat7:redeploy
```

**Generate Javadoc**:
```bash
mvn javadoc:aggregate
```

### Kotlin REST API v2 (When Created)

**Run API locally** (after module setup):
```bash
cd bibsonomy-rest-api-v2
mvn spring-boot:run
```

**Run API tests**:
```bash
cd bibsonomy-rest-api-v2
mvn test
```

**Build API JAR**:
```bash
cd bibsonomy-rest-api-v2
mvn clean package
# JAR will be in target/bibsonomy-rest-api-v2-<version>.jar
```

### Frontend (When Created)

**Note**: Using **Bun** (modern, faster) or **npm** (traditional, safer). Commands shown with Bun - replace with `npm` if preferred.

**Install dependencies**:
```bash
cd bibsonomy-frontend
bun install
# or: npm install
```

**Run development server**:
```bash
bun run dev
# or: npm run dev
# Opens at http://localhost:5173 (Vite default port)
```

**Build for production**:
```bash
bun run build
# or: npm run build
# Output in dist/
```

**Run tests**:
```bash
bun test
# or: npm test
```

**Type check**:
```bash
bun run type-check
# or: npm run type-check
# or directly: bunx tsc --noEmit (or npx tsc --noEmit)
```

## Developing New Modules

### Creating the Kotlin REST API v2 Module

**Key Requirements**:
- Java 21 target
- Kotlin 1.9+
- Spring Boot 3.2+
- Reuse `bibsonomy-model` and `bibsonomy-database` as dependencies

**Critical Pattern - Database Access Bridge**:

The new Kotlin module must bridge to the legacy database layer:

```kotlin
// Example configuration
@Configuration
@ImportResource("classpath:org/bibsonomy/database/bibsonomy-database-context.xml")
class DatabaseBridgeConfig {

    @Bean
    fun bibsonomyService(logicInterface: LogicInterface): BibsonomyService {
        return BibsonomyService(logicInterface)
    }
}

// Service layer wraps legacy LogicInterface
@Service
class BibsonomyService(
    private val logic: LogicInterface  // Injected from legacy Spring XML
) {
    fun getPosts(params: PostQueryParams): List<PostDto> {
        val grouping = /* build GroupingEntity from params */
        val posts = logic.getPosts(grouping)
        return posts.map { it.toDto() }  // Map domain -> DTO
    }
}
```

**DTOs Must Be Decoupled**:
- Never expose `org.bibsonomy.model.*` domain classes directly in REST API
- Create explicit DTO classes in Kotlin
- Write explicit mapping functions (no auto-mapping libraries)
- Handle nullability conversion (Java → Kotlin)

**Example DTO Pattern**:
```kotlin
// API DTO (exposed to clients)
data class PostDto(
    val id: String,
    val title: String,
    val url: String?,
    val tags: List<TagDto>,
    val user: UserDto,
    val created: Instant
)

// Mapping extension function
fun Post<out Resource>.toDto(): PostDto {
    return PostDto(
        id = this.id ?: throw IllegalStateException("Post ID cannot be null"),
        title = this.resource?.title ?: "",
        url = (this.resource as? Bookmark)?.url,
        tags = this.tags?.map { it.toDto() } ?: emptyList(),
        user = this.user.toDto(),
        created = this.date?.toInstant() ?: Instant.now()
    )
}
```

**Testing the API**:
- Write REST integration tests using `@SpringBootTest` + `MockMvc` or `TestRestTemplate`
- Test actual HTTP requests/responses
- Include authentication in tests
- Test error cases (404, 400, 401, 403, 500)

### Creating the Vue Frontend

**Tech Stack** (decided after discussion):

**Core (Required)**:
- **Vue 3 (SFC + Composition API)** - UI framework
- **TypeScript** - Type safety (critical for AI-generated code)
- **Vite** - Build tool (modern, fast)
- **vue-router** - Client-side routing for SPA
- **vue-i18n** - Internationalization (German/English support)

**Highly Recommended**:
- **@tanstack/vue-query** - Server state management (caching, dedupe, retries, pagination)
- **Pinia** - Shared client/UI state (auth tokens, prefs, global filters)
- **Tailwind CSS** (or UnoCSS) + **Headless UI** - Accessible primitives with small footprint
- **Zod** - Runtime type validation for API responses

**Package Manager**: Bun (modern, faster) or npm (traditional, safer)

**NOT included** (avoid unnecessary dependencies):
- ❌ Redux/Vuex (Pinia + vue-query cover needs)
- ❌ Heavy UI frameworks unless requested (keep bundle lean)

**Project Structure**:
```
bibsonomy-frontend/
├── public/
│   └── locales/          # i18n translation files
│       ├── en/
│       │   └── translation.json
│       └── de/
│           └── translation.json
├── src/
│   ├── components/
│   │   ├── ui/           # Headless UI wrappers
│   │   ├── PostCard.vue
│   │   ├── PostList.vue
│   │   └── UserProfile.vue
│   ├── pages/
│   │   ├── HomePage.vue
│   │   ├── PostDetailPage.vue
│   │   └── ProfilePage.vue
│   ├── composables/
│   │   ├── useApi.ts     # API client with vue-query
│   │   └── useAuth.ts    # Auth helpers (Pinia integration)
│   ├── store/
│   │   └── auth.ts       # Pinia store
│   ├── plugins/
│   │   ├── i18n.ts       # vue-i18n configuration
│   │   └── query.ts      # vue-query client
│   └── types/
│       └── models.ts     # TypeScript types
├── package.json
├── tsconfig.json
├── vite.config.ts
└── tailwind.config.js    # If using Tailwind
```

**API Integration Pattern** (with @tanstack/vue-query):
```typescript
// composables/usePosts.ts
import { useQuery } from '@tanstack/vue-query'

export function usePosts(params: PostQueryParams) {
  return useQuery({
    queryKey: ['posts', params],
    queryFn: async () => {
      const response = await fetch(`/api/v2/posts?${new URLSearchParams(params)}`)
      if (!response.ok) throw new Error('Failed to fetch posts')
      const data = await response.json()
      return data as Post[] // Or z.array(PostSchema).parse(data)
    }
  })
}
```

**Internationalization Pattern** (vue-i18n):
```typescript
// plugins/i18n.ts
import { createI18n } from 'vue-i18n'
import en from '../locales/en/translation.json'
import de from '../locales/de/translation.json'

export const i18n = createI18n({
  legacy: false,
  locale: 'de',         // default German (like legacy webapp)
  fallbackLocale: 'en',
  messages: { en, de }
})

// In a component
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()
const switchToEn = () => (locale.value = 'en')
```

**Translation Files**:
```json
// public/locales/en/translation.json
{
  "post": {
    "title": "Post",
    "created": "Created",
    "tags": "Tags"
  },
  "nav": {
    "home": "Home",
    "profile": "Profile"
  }
}

// public/locales/de/translation.json
{
  "post": {
    "title": "Beitrag",
    "created": "Erstellt",
    "tags": "Tags"
  },
  "nav": {
    "home": "Startseite",
    "profile": "Profil"
  }
}
```

**Testing Frontend**:
- Focus on integration tests with real API calls (can use MSW for mocking)
- Test critical user flows (login, create post, view profile)
- Test language switching (i18n)
- Keep snapshot tests minimal
- Test accessibility with jest-axe or similar
- Use Vitest (Vite's test runner) - faster than Jest

## Test Configuration

**Legacy Module Tests** use MariaDB/MySQL databases.

GitLab CI configuration (`.gitlab-ci.yml`) shows required test databases:
- `bibsonomy_unit_test`
- `main_db`
- `item_recommender_db`
- `tag_recommender_db`

Test settings: `misc/scripts/settings.xml` (profile: `bibsonomy-test-settings`)

**New Kotlin API Tests** should use:
- Spring Boot test slices (`@WebMvcTest`, `@SpringBootTest`)
- Testcontainers for database (if needed for integration tests)
- In-memory H2 for quick unit tests (if compatible)
- Real database for full integration tests

## Architecture Overview

### New Modern Architecture (Active Development)

**Component Diagram**:
```
┌───────────────────────────────────────────────────────┐
│ Vue 3 Frontend (bibsonomy-frontend)                   │
│ - Vue 3 + TypeScript + Vite (build tool)              │
│ - vue-i18n (German/English)                           │
│ - @tanstack/vue-query (API state), Tailwind (styling) │
│ - Pinia (auth/prefs)                                  │
└─────────────────┬─────────────────────────────────────┘
                  │ HTTP/JSON API
┌─────────────────▼─────────────────────────────────────┐
│ Kotlin REST API v2 (bibsonomy-rest-api-v2)            │
│ - Spring Boot 3.x Controllers                         │
│ - DTOs (explicit mapping from domain models)          │
│ - Authentication/Authorization                        │
└─────────────────┬─────────────────────────────────────┘
                  │ Service Layer
┌─────────────────▼─────────────────────────────────────┐
│ Kotlin Service Layer (in bibsonomy-rest-api-v2)       │
│ - Wraps LogicInterface from legacy database layer     │
│ - Maps domain models → DTOs                           │
│ - Handles transactions and error mapping              │
└─────────────────┬─────────────────────────────────────┘
                  │ LogicInterface calls
┌─────────────────▼─────────────────────────────────────┐
│ LEGACY: Business Logic (bibsonomy-database/DBLogic)   │
│ - Java 8, Spring 3.2                                  │
│ - Implements LogicInterface                           │
│ - Orchestrates database managers                      │
└─────────────────┬─────────────────────────────────────┘
                  │ Database Manager calls
┌─────────────────▼─────────────────────────────────────┐
│ LEGACY: Database Layer (bibsonomy-database)           │
│ - iBatis SQL mappers                                  │
│ - Entity managers (BibTexDatabaseManager, etc.)       │
│ - Chain of Responsibility query processing            │
└─────────────────┬─────────────────────────────────────┘
                  │ SQL queries
┌─────────────────▼─────────────────────────────────────┐
│ MySQL Database                                         │
└───────────────────────────────────────────────────────┘
```

**Key Patterns in New Architecture**:

1. **DTO Decoupling**: REST API never exposes internal domain models directly
2. **Adapter Pattern**: Kotlin service layer adapts legacy `LogicInterface` to modern API
3. **Dependency Reuse**: New modules depend on `bibsonomy-model` and `bibsonomy-database` JARs
4. **Parallel Operation**: New API runs alongside legacy webapp (both access same database)

### Legacy Architecture (Maintained, Not Modernized)

**Layered Structure**:

```
Presentation Layer (bibsonomy-webapp [JSP/Spring MVC], bibsonomy-rest-server [legacy REST API])
                    ↓
Business Logic Layer (DBLogic implementing LogicInterface)
                    ↓
Data Access Layer (Database Managers via iBatis)
                    ↓
Database (MySQL via iBATIS 2.x)
```

**Note**: This legacy architecture is preserved as-is. New development should use the modern Kotlin API instead.

### Core Module Responsibilities (Legacy + Shared)

**Foundation Modules:**
- **bibsonomy-model**: Domain model (`Resource`, `Post<T>`, `BibTex`, `Bookmark`, `User`, `Group`, `Tag`) and service interfaces (`LogicInterface`)
- **bibsonomy-common**: Utilities, exceptions, enums used across all modules

**Data Access:**
- **bibsonomy-database-common**: Database abstraction layer (`DBSessionFactory`, `DBSession`, type handlers)
- **bibsonomy-database**: Concrete iBATIS implementation with database managers for each entity type

**Business Logic:**
- **DBLogic** (in bibsonomy-database): Implements `LogicInterface`, orchestrates database managers, enforces business rules

**Presentation:**
- **bibsonomy-webapp**: Spring MVC web application (WAR), uses JSP/JSTL, Spring Security, DWR for AJAX
- **bibsonomy-rest-server**: REST API implementation (servlet-based)
- **bibsonomy-web-common**: Shared web utilities

**Supporting Modules:**
- **bibsonomy-bibtex-parser**: Parse BibTeX files (GPL licensed)
- **bibsonomy-search** / **bibsonomy-search-elasticsearch**: Full-text search via Elasticsearch
- **bibsonomy-recommender**: Tag and publication recommendations
- **bibsonomy-importer** / **bibsonomy-exporter**: Import/export functionality
- **bibsonomy-scraper** / **bibsonomy-scrapingservice**: Web scraping for metadata
- **bibsonomy-layout**: Rendering and formatting utilities
- **bibsonomy-logging**: User activity logging
- **bibsonomy-wiki**: Wiki functionality
- **bibsonomy-synchronization**: External service sync (Mendeley, Zotero)

### Key Technologies

- **Java 8**
- **Spring Framework 3.2** (Spring MVC, Spring Security 3.2, Spring ORM)
- **iBATIS 2.x** (legacy SQL mapper, predecessor to MyBatis)
- **MySQL 5.x** with Apache Commons DBCP2
- **Maven** for build management
- **JaCoCo** for code coverage
- **JUnit 4** for testing

### Important Architectural Patterns

**1. Logic Interface Pattern**
- `LogicInterface` (bibsonomy-model) defines business operations
- `DBLogic` (bibsonomy-database) implements the interface
- Web and REST layers depend only on `LogicInterface`, not concrete implementations
- Created via `LogicInterfaceFactory`

**2. Chain of Responsibility for Queries**
Database queries are processed via chains configured in Spring:
- `publicationChain`: Handles BibTeX/publication queries
- `bookmarkChain`: Handles bookmark queries
- `userChain`, `tagChain`, `groupChain`, etc.

Each chain element handles specific query patterns (e.g., by resource hash, by tag name, by user).

**3. Generic Resource System**
```java
abstract class Resource { }
class BibTex extends Resource { }
class Bookmark extends Resource { }
class Post<T extends Resource> { }
```
This allows type-safe handling of both publications and bookmarks through a unified interface.

**4. Plugin Architecture**
- `DatabasePluginRegistry` manages plugins
- Plugins handle cross-cutting concerns: Logging, Clipboard, Discussion, GoldStandard, CRIS
- Plugins execute during database operations via hooks

**5. Database Managers**
Each entity type has a specialized manager:
- `BibTexDatabaseManager`: Publications
- `BookmarkDatabaseManager`: Web bookmarks
- `UserDatabaseManager`: User accounts
- `GroupDatabaseManager`: Groups/collaboration
- `TagDatabaseManager`: Tagging system
- `PersonDatabaseManager`: Researchers/persons (CRIS integration)
- `StatisticsDatabaseManager`: Metrics
- `DiscussionDatabaseManager`: Comments/reviews

### Configuration Files

**iBATIS SQL Mappings** (bibsonomy-database):
- `SqlMapConfig.xml`: Main iBATIS configuration
- Entity mappings: `BibTex.xml`, `Bookmark.xml`, `User.xml`, `Tag.xml`, etc.

**Spring Configuration** (bibsonomy-webapp):
- `web.xml`: Servlet/filter configuration
- `bibsonomy-servlet-*.xml`: Spring MVC contexts (actions, admin, ajax, database, security)

**Test Configuration**:
- `TestDatabaseMainContext.xml`, `TestDatabaseBaseContext.xml`: Spring test contexts
- `TestSqlMapConfig.xml`: iBATIS test configuration
- `database-test.properties`: Test database properties

## Development Notes

### Working with Database Layer

The database layer uses **iBATIS 2.x** (not MyBatis). Key differences:
- SQL mappings are in XML files, not annotations
- Uses older `SqlMapClient` API
- Type handlers for custom type conversions
- Dynamic SQL via `<dynamic>`, `<isNotNull>`, `<iterate>` tags

When modifying queries:
1. Locate the relevant XML file (e.g., `BibTex.xml` for publications)
2. Find or add the SQL statement with a unique ID
3. Update the corresponding database manager to call the statement
4. Consider chain processing if adding new query patterns

### Spring Configuration

Spring 3.2 uses XML-based configuration (pre-annotation era). To add new beans:
1. Locate the appropriate Spring context file
2. Add bean definition with proper dependencies
3. For web components, use `bibsonomy-servlet-*.xml` files
4. For database components, use Spring contexts in bibsonomy-database

### Testing

Tests follow JUnit 4 conventions:
- Test classes typically end with `Test` or `TestSuite`
- Database tests require test database setup (see `TestDatabaseBaseContext.xml`)
- Use `@Test` annotations (JUnit 4, not JUnit 5)
- Integration tests may require database initialization scripts

### Module Dependencies

Dependencies flow in one direction:
```
webapp/rest-server → web-common → database → database-common → model → common
```

Never create circular dependencies between modules.

## Common Development Tasks

### Working on New Kotlin REST API v2

**Adding a new REST endpoint**:

1. **Create DTO** in `bibsonomy-rest-api-v2/src/main/kotlin/.../dto/`
   ```kotlin
   data class PostDto(
       val id: String,
       val title: String,
       // ... fields
   )
   ```

2. **Create mapping function** from domain model → DTO
   ```kotlin
   fun Post<out Resource>.toDto(): PostDto = PostDto(...)
   ```

3. **Create/update service** to call `LogicInterface`
   ```kotlin
   @Service
   class PostService(private val logic: LogicInterface) {
       fun getPost(id: String): PostDto {
           val post = logic.getPost(id)
           return post.toDto()
       }
   }
   ```

4. **Create controller**
   ```kotlin
   @RestController
   @RequestMapping("/api/v2/posts")
   class PostController(private val postService: PostService) {
       @GetMapping("/{id}")
       fun getPost(@PathVariable id: String): PostDto {
           return postService.getPost(id)
       }
   }
   ```

5. **Write integration test**
   ```kotlin
   @SpringBootTest(webEnvironment = RANDOM_PORT)
   class PostControllerIntegrationTest {
       @Test
       fun `GET post by id returns 200 with post data`() {
           // Test actual HTTP endpoint
       }
   }
   ```

**Querying data from legacy layer**:
- Inject `LogicInterface` from legacy database module
- Use `LogicInterface.getPosts()`, `getUsers()`, etc.
- Handle nullability carefully (legacy Java → Kotlin)
- Map results to DTOs before returning from API

**Authentication**:
- Implement OAuth token validation compatible with legacy system
- Or: Call legacy REST API for token validation initially
- Or: Share authentication logic via common module

### Working on Vue Frontend

**Adding a new page**:

1. **Define Zod schema** for API data in `src/types/schemas.ts`
2. **Create composable** in `src/composables/useApi.ts` (or specific file) using @tanstack/vue-query
3. **Create page component** in `src/pages/YourPage.vue`
4. **Add route** to `src/router/index.ts`
5. **Wire Pinia store** if shared UI/auth state is needed
6. **Write integration test** for critical flows

**Calling the REST API**:
```typescript
// composables/usePosts.ts
import { useQuery } from '@tanstack/vue-query'
import { PostListSchema } from '@/types/schemas'

export function usePosts(params: PostQueryParams) {
  return useQuery({
    queryKey: ['posts', params],
    queryFn: async () => {
      const response = await fetch(`/api/v2/posts?${buildQuery(params)}`)
      if (!response.ok) throw new Error('Failed to fetch')
      const data = await response.json()
      return PostListSchema.parse(data) // Zod validation
    }
  })
}
```

### Working with Legacy Modules (When Necessary)

**⚠️ IMPORTANT: Only modify legacy modules for critical bugfixes or to support new API requirements.**

**Adding a new field to domain model** (rare):

1. Update domain class in `bibsonomy-model` (e.g., `BibTex.java`)
2. Update iBATIS XML mapping in `bibsonomy-database` (e.g., `BibTex.xml`)
3. Update database schema (write migration script)
4. Update database managers if query logic changes
5. If exposed via new API: Create DTO mapping in Kotlin module

**Adding a database query** (if no existing query works):

1. Add SQL statement to appropriate iBATIS XML file with unique ID
2. Add method to corresponding database manager
3. If needed, add element to relevant query chain in Spring config
4. Expose via `LogicInterface` if needed by new API

**DO NOT**:
- Modernize legacy code "while you're there"
- Upgrade Spring or iBatis dependencies
- Refactor legacy JSPs or Spring MVC controllers
- Add new features to legacy webapp (use new API instead)

## Code Style Notes

**Modern Kotlin Code**:
- Immutable data classes for DTOs (`val`, not `var`)
- Null-safe by default (use `?` only when truly optional)
- Extension functions for mapping (e.g., `fun Post.toDto()`)
- Constructor injection (`class Service(private val dep: Dep)`)
- Meaningful variable names, no abbreviations
- Prefer sealed classes for sum types (e.g., `sealed class Result<T>`)

**Modern Vue/TypeScript Code**:
- Vue 3 SFCs with Composition API
- TypeScript strict mode
- vue-i18n for translations (German/English)
- @tanstack/vue-query for server state (no Redux/Vuex for API data)
- Pinia for shared UI/client state when needed
- Tailwind CSS utility classes (or UnoCSS/plain CSS)
- Semantic HTML with accessibility in mind
- Vitest for testing (Vite's test runner)

**Legacy Java Code**:
- **Lombok** used for boilerplate (`@Getter`, `@Setter`, `@ToString`)
- **Joda-Time** for dates (pre-Java 8 `java.time`)
- Java conventions with meaningful names
- SQL externalized in iBATIS XML files, not embedded in Java

## Quick Reference

### Key Files & Locations

**Modernization Plan**:
- `.cursor/plans/bibsonomy_modernization_strategy_50e37204.plan.md` - Full modernization strategy

**Code Quality Configuration**:
- `.coderabbit.yaml` - Code review rules and quality standards

**Legacy Core**:
- `bibsonomy-model/src/main/java/org/bibsonomy/model/logic/LogicInterface.java` - Main business logic interface
- `bibsonomy-database/src/main/resources/org/bibsonomy/database/bibsonomy-database-context.xml` - Spring bean config
- `bibsonomy-database/src/main/resources/org/bibsonomy/database/common/SqlMapConfig.xml` - iBatis config

**Legacy SQL Mappings**:
- `bibsonomy-database/src/main/resources/sql/` - iBatis XML SQL maps

**Test Configuration**:
- `.gitlab-ci.yml` - CI/CD pipeline (database setup, test commands)
- `misc/scripts/settings.xml` - Maven test settings

**New Modules** (when created):
- `bibsonomy-rest-api-v2/` - Kotlin Spring Boot API
- `bibsonomy-frontend/` - Vue 3 + TypeScript frontend

### Domain Model Quick Reference

**Core Entities** (`bibsonomy-model`):
- `Post<T extends Resource>` - Generic post containing a resource
- `Resource` - Abstract base (extended by `BibTex` and `Bookmark`)
- `BibTex` - Publication/bibliographic entry
- `Bookmark` - Web bookmark
- `User` - User account
- `Tag` - User-defined tag
- `Group` - User group with permissions
- `Person` - Author/researcher (CRIS integration)

**LogicInterface Methods** (key operations):
- `getPosts(GroupingEntity)` - Query posts
- `getUsers(GroupingEntity)` - Query users
- `createPost(Post<?>, String)` - Create new post
- `updatePost(Post<?>, String)` - Update post
- `deletePost(String, String)` - Delete post

### When to Use What

| Task | Use |
|------|-----|
| New REST API endpoint | Create in `bibsonomy-rest-api-v2` (Kotlin) |
| New UI page/feature | Create in `bibsonomy-frontend` (Vue 3 + TypeScript) |
| Bug in legacy webapp | Fix in `bibsonomy-webapp` (minimal change) |
| New database query | Add to `bibsonomy-database` iBatis XML + expose via `LogicInterface` |
| New domain field | Add to `bibsonomy-model` + update iBatis XML + create DTO mapping |
| Integration test | Test real behavior (HTTP endpoints, database, etc.) |
| Unit test | Avoid - prefer integration tests |

### Common Gotchas

1. **Java 8 → Kotlin nullability**: Legacy Java models are nullable by default. Handle with `?.` and `!!` carefully.
2. **Spring XML import**: Use `@ImportResource` to load legacy Spring XML configs in Spring Boot.
3. **iBatis SQL IDs**: Must be unique across all XML files. Use descriptive prefixes.
4. **Transaction boundaries**: `LogicInterface` manages transactions. Don't start new transactions in Kotlin service layer.
5. **DTO mapping**: Always map explicitly. No auto-mapping libraries (MapStruct, etc.).
6. **Legacy dependencies**: Don't upgrade Spring 3.2 or iBatis in legacy modules.
7. **Frontend API errors**: Use Zod to validate API responses - catch breaking changes early.

### Getting Help

- **Modernization questions**: See `.cursor/plans/bibsonomy_modernization_strategy_50e37204.plan.md`
- **Legacy architecture**: Search for patterns in existing modules
- **Domain model**: Read `bibsonomy-model` classes and JavaDoc
- **Database queries**: Check iBatis XML files in `bibsonomy-database/src/main/resources/sql/`
- **Code quality**: Review `.coderabbit.yaml` for expectations
