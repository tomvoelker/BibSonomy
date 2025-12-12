---
name: BibSonomy Modernization Strategy
overview: Create a gradual modernization plan for BibSonomy, starting with a new Kotlin REST API module that reuses core database/model modules, followed by a modern React frontend, while keeping the legacy system operational.
todos:
  - id: setup-kotlin-module
    content: Create new Maven module bibsonomy-rest-api-v2 with Kotlin, Java 21, and Spring Boot 3.x
    status: pending
  - id: test-model-dependency
    content: Add bibsonomy-model dependency and verify Kotlin can use Java POJOs correctly
    status: pending
  - id: create-db-bridge
    content: Create Spring Boot configuration that bridges to old bibsonomy-database LogicInterface
    status: pending
  - id: implement-first-endpoint
    content: Implement GET /api/v2/posts endpoint as proof of concept
    status: pending
  - id: setup-frontend
    content: Initialize React + TypeScript frontend project with API client
    status: pending
  - id: define-mvp-scope
    content: Define exact MVP feature set with stakeholders
    status: pending
  - id: test-deployment
    content: Test running new API alongside legacy webapp on same server
    status: pending
---

# BibSonomy Modernization Strategy

## Current System Analysis

### Architecture Overview

- **Language**: Java 8 (compiler target 1.8)
- **Framework**: Spring 3.2.17 (very old, from 2014)
- **ORM**: iBatis (deprecated, replaced by MyBatis)
- **Database**: MySQL with iBatis SQL maps
- **Web Framework**: Spring MVC with JSP/tagx templates
- **Build**: Maven multi-module project (27 modules)
- **REST API**: Existing but tightly coupled to legacy architecture

### Core Modules Structure

1. **bibsonomy-model** - Domain entities (Post, User, Resource, Tag, etc.)
2. **bibsonomy-database** - Database access layer (iBatis)
3. **bibsonomy-common** - Shared utilities
4. **bibsonomy-rest-server** - Existing REST API (legacy)
5. **bibsonomy-webapp** - Main web application (complex, 3500+ files)
6. **Many specialized modules**: scraper, recommender, search, etc.

### Core Domain Entities

- **Post**: Links User + Resource (Bookmark/Publication) + Tags + Groups
- **User**: Authentication, profiles, settings
- **Resource**: Bookmark or BibTeX Publication
- **Tag**: User-defined tags
- **Group**: User groups with permissions
- **Person**: Author/researcher entities (CRIS integration)

## Feasibility Assessment

### ✅ Feasible Aspects

1. **Database reuse**: The database layer can be reused via JAR dependencies
2. **Model reuse**: Domain models are POJOs and can be shared
3. **Gradual migration**: Can run both systems in parallel
4. **REST API exists**: Current API provides reference for endpoints

### ⚠️ Challenges

1. **Java 8 → Java 21 compatibility**: Old dependencies may not work
2. **Spring 3.2 → Modern Spring**: Major breaking changes
3. **iBatis dependency**: Old ORM, but database layer can be wrapped
4. **Complex dependencies**: 27 modules with interdependencies
5. **Database schema**: Complex with many migrations (4.0.0+)

## 

### Recommended Approach

### Phase 0: API Design Investigation (Step 0)

**Before implementation**: Thoroughly analyze current REST API and webapp to design clean API schema

**Activities**:

- Document all current REST endpoints (`/api/posts`, `/api/users`, `/api/tags`, etc.)
- Analyze request/response patterns
- Identify pain points and inconsistencies
- Design new API schema with:
  - RESTful conventions
  - Consistent naming
  - Proper HTTP methods
  - Clear resource hierarchy
  - Versioning strategy (`/api/v2/...`)
- Document API design decisions

**Deliverable**: API design document with endpoint specifications

### Phase 1: New Kotlin REST API Module (MVP)

**Create**: `bibsonomy-rest-api-v2` (or `bibsonomy-api-kotlin`)

**Strategy**:

- Use Kotlin with Java 21
- Modern framework: **Spring Boot 3.x** (recommended - see discussion below)
- Reuse `bibsonomy-model` and `bibsonomy-database` as JAR dependencies
- Create thin adapter layer to bridge old database layer to new API
- Start with core read-only endpoints (MVP)

**Reusable Modules**:

- ✅ `bibsonomy-model` - Pure POJOs, fully reusable
- ✅ `bibsonomy-database` - Can be used via dependency injection
- ✅ `bibsonomy-common` - Utilities (may need compatibility layer)
- ⚠️ `bibsonomy-rest-server` - Reference only, don't reuse code

**New Dependencies**:

- Kotlin 1.9+
- Spring Boot 3.2+
- Jackson for JSON (already in use)
- Modern HTTP client

### Phase 2: Modern Frontend

**Technology**: React + TypeScript

**Approach**:

- Standalone SPA that calls new REST API
- Can be deployed separately (CDN/static hosting)
- Progressive enhancement: start with read-only, add write operations incrementally

### Phase 3: Gradual Feature Migration

1. Start with read-only operations (GET endpoints)
2. Add authentication (reuse existing auth mechanisms)
3. Add write operations (POST/PUT/DELETE)
4. Migrate features incrementally

## Implementation Plan

### Step 1: Setup New Module Structure

```
bibsonomy-rest-api-v2/
├── pom.xml (or build.gradle.kts)
├── src/main/kotlin/
│   └── org/bibsonomy/api/
│       ├── Application.kt (main entry)
│       ├── config/
│       │   ├── DatabaseConfig.kt (bridge to old DB layer)
│       │   └── SecurityConfig.kt
│       ├── controllers/
│       │   ├── PostsController.kt
│       │   ├── UsersController.kt
│       │   └── TagsController.kt
│       ├── services/
│       │   └── (adapter layer to old LogicInterface)
│       └── dto/
│           └── (API DTOs, separate from domain models)
└── src/main/resources/
    └── application.yml
```

### Step 2: Database Access Bridge

**Challenge**: Old system uses iBatis with Spring 3.2, new system needs Spring Boot 3.x

**Solution**: Create adapter layer

- Inject old `LogicInterface` from `bibsonomy-database` module
- Wrap in Kotlin service layer
- Map domain models to DTOs
- Handle transaction boundaries

**Key Files to Reference**:

- `bibsonomy-database/src/main/resources/org/bibsonomy/bibsonomy-database-context.xml` - Spring config
- `bibsonomy-model/src/main/java/org/bibsonomy/model/logic/LogicInterface.java` - Main interface

### Step 3: MVP Endpoints (After API Design)

**Priority 1 (Read-only MVP)**:

- `GET /api/v2/posts` - List posts (homepage feed)
- `GET /api/v2/posts/{id}` - Get post details
- `GET /api/v2/users/{username}` - Get user profile
- `GET /api/v2/users/{username}/posts` - User's posts
- Authentication: OAuth compatible with existing system

**Priority 2 (After frontend MVP)**:

- `POST /api/v2/posts` - Create post
- `PUT /api/v2/posts/{id}` - Update post
- `DELETE /api/v2/posts/{id}` - Delete post
- Groups endpoints (later)

### Step 4: Frontend MVP

**Technology Recommendation**:

- **React 18+** with **TypeScript**
- **Vite** for build tool (fast, simple)
- **React Router** for navigation
- **TanStack Query (React Query)** for API state management (AI-friendly, clean patterns)
- **UI Framework**: **shadcn/ui** + **Tailwind CSS** (modern, customizable, AI-friendly)
  - Alternative: **Chakra UI** (also good, more opinionated)
- **Zod** for runtime type validation (works great with TypeScript)

**Why this stack**:

- AI agents work well with React + TypeScript (clear patterns)
- shadcn/ui is copy-paste components (easy to customize, no heavy framework)
- TanStack Query handles caching/refetching automatically
- Vite = fast dev experience

**Structure**:

```
bibsonomy-frontend/
├── package.json
├── tsconfig.json
├── vite.config.ts
├── tailwind.config.js
├── src/
│   ├── App.tsx
│   ├── components/
│   │   ├── ui/ (shadcn components)
│   │   ├── PostList.tsx
│   │   ├── PostCard.tsx
│   │   └── UserProfile.tsx
│   ├── pages/
│   │   ├── HomePage.tsx
│   │   ├── PostDetailPage.tsx
│   │   └── ProfilePage.tsx
│   ├── services/
│   │   └── api.ts (API client with TanStack Query)
│   └── types/
│       └── models.ts (TypeScript types from API)
```

## Critical Issues & Solutions

### Issue 1: Java Version Compatibility

**Problem**: Old modules compiled for Java 8, new module needs Java 21

**Solution**:

- Keep old modules as-is (Java 8 compiled JARs)
- New module uses Java 21 but can depend on Java 8 JARs
- Test compatibility early

### Issue 2: Spring Version Mismatch

**Problem**: `bibsonomy-database` uses Spring 3.0 (for iBatis), new module needs Spring Boot 3.x

**Solution**:

- Spring Boot 3.x can import old Spring XML configs via `@ImportResource`
- Load `bibsonomy-database-context.xml` in separate ApplicationContext
- Bridge beans via dependency injection
- **Note**: `bibsonomy-common` has NO Spring dependency, fully reusable
- `bibsonomy-database` uses Spring but only for bean wiring, can be bridged

### Issue 3: Database Connection Sharing

**Problem**: Two applications accessing same database

**Solution**:

- **For now**: Use same connection pool configuration (reuse old DB module)
- **Later**: Consider separate connection pools or read replicas
- Coordinate via database transactions
- **Note**: This is a problem for later, not blocking MVP

### Issue 4: Authentication

**Problem**: Reuse existing auth mechanisms

**Solution**:

- ✅ **Implement OAuth compatible with existing system** (recommended)
- Enables users to authenticate once, use both systems
- Allows public beta deployment alongside old system
- Later: Add username/password flow for frontend-only users

## First Steps (Immediate Actions)

### Step 0: API Design Investigation

1. **Document current REST API**

   - List all endpoints from `bibsonomy-rest-server/strategy/` handlers
   - Document URL patterns, methods, request/response formats
   - Identify current API structure: `/api/posts`, `/api/users/{username}/posts`, etc.

2. **Analyze webapp usage**

   - Review homepage controller and what data it needs
   - Review post detail page requirements
   - Review profile page requirements
   - Document data requirements for MVP

3. **Design new API schema**

   - Create OpenAPI/Swagger spec (or at least markdown doc)
   - Define resource hierarchy
   - Design request/response DTOs
   - Plan versioning strategy

### Step 1: Create new Maven module `bibsonomy-rest-api-v2`

   - Add to root `pom.xml` modules list
   - Configure Kotlin plugin
   - Set Java 21 as target
   - Add Spring Boot 3.2+ dependency (or Ktor if chosen)

### Step 2: Test module dependency

   - Add `bibsonomy-model` as dependency
   - Verify it compiles and can be used from Kotlin
   - Test basic serialization (Jackson)

### Step 3: Create database bridge

   - Study `bibsonomy-database-context.xml`
   - Create Spring Boot `@Configuration` that imports old Spring XML via `@ImportResource`
   - Test injection of `LogicInterface` from old module

### Step 4: Implement first endpoint

   - `GET /api/v2/posts` (read-only, no auth initially)
   - Return JSON list of posts
   - Verify data flow: Database → LogicInterface → Controller → JSON

### Step 5: Setup frontend project

   - Initialize React + TypeScript + Vite project
   - Add shadcn/ui + Tailwind
   - Add TanStack Query
   - Create API client
   - Display posts list (homepage)

## Authentication Strategy

**Approach**: Implement OAuth compatible with existing system

**Why this works**:

- ✅ Users can authenticate once, use both old and new systems
- ✅ Enables public beta deployment alongside old system
- ✅ Gradual migration path
- ✅ Later: Add username/password flow for frontend-only users

**Implementation**:

- Reuse existing OAuth token validation logic
- Or: Call old REST API for token validation initially
- Or: Extract OAuth logic to shared module

**Nothing speaks against it** - this is the right approach for gradual migration.

## MVP Scope (Confirmed)

**Phase 1 - Read-only MVP**:

- ✅ Authentication (OAuth compatible)
- ✅ Homepage: Display posts (replicating homepage, nicer UI)
- ✅ Post details page
- ✅ Profile page

**Phase 2** (After frontend MVP):

- POST/PUT/DELETE for posts
- Groups functionality

**Timeline**: Good job over rushing, but maintain sane scope. Iterate quickly on MVP, then expand.