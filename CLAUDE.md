# CLAUDE.md

This file provides guidance to Claude Code when working with the BibSonomy codebase.

> **Note**: This is the top-level guide. For module-specific guidance, see:
> - `bibsonomy-rest-api-v2/CLAUDE.md` - Kotlin REST API v2 development
> - `bibsonomy-webapp-v2/CLAUDE.md` - Vue 3 frontend development

## Project Overview

**BibSonomy** is a social bookmark and publication sharing system (est. 2006). It allows users to organize and share web bookmarks and bibliographic publications using a collaborative tagging system.

**License**: LGPL 3.0 (most modules), AGPL 3.0 (webapp), GPL (bibtex-parser)

## ⚡ Modernization in Progress

**CRITICAL**: BibSonomy is undergoing gradual modernization with two parallel systems:

1. **Legacy System** (Java 8, Spring 3.2, iBatis) - **Maintained but NOT modernized**
2. **New Modern System** (Kotlin, Java 21, Spring Boot 3, Vue 3) - **Active development**

See `.cursor/plans/bibsonomy_modernization_strategy_50e37204.plan.md` for full strategy.

### Architecture at a Glance

```
┌─────────────────────────────────────┐
│ NEW: Vue 3 Frontend                 │  bibsonomy-webapp-v2/
│ (TypeScript, Vite, vue-query)       │  └─ See bibsonomy-webapp-v2/CLAUDE.md
└──────────────┬──────────────────────┘
               │ REST API
┌──────────────▼──────────────────────┐
│ NEW: Kotlin REST API v2              │  bibsonomy-rest-api-v2/
│ (Spring Boot 3, Java 21)             │  └─ See bibsonomy-rest-api-v2/CLAUDE.md
└──────────────┬──────────────────────┘
               │ Reuses via JAR deps
┌──────────────▼──────────────────────┐
│ LEGACY: Domain & Database Layer      │  bibsonomy-model/
│ (Java 8, iBatis, Spring 3.2)         │  bibsonomy-database/
└──────────────┬──────────────────────┘  bibsonomy-common/
               │
┌──────────────▼──────────────────────┐
│ MySQL Database                       │
└──────────────────────────────────────┘

LEGACY (parallel, not modernized):
┌──────────────────────────────────────┐
│ JSP/Spring MVC Webapp                │  bibsonomy-webapp/
└──────────────────────────────────────┘
```

**Key Principle**: New code in Kotlin/Vue. Legacy code preserved as-is for stability.

## Critical Development Rules

### 🎯 Where to Add New Features

| Task | Where | Module-Specific Docs |
|------|-------|---------------------|
| New REST API endpoint | `bibsonomy-rest-api-v2/` (Kotlin) | See `bibsonomy-rest-api-v2/CLAUDE.md` |
| New UI page/feature | `bibsonomy-webapp-v2/` (Vue 3) | See `bibsonomy-webapp-v2/CLAUDE.md` |
| Bug in legacy webapp | `bibsonomy-webapp/` (minimal fix only) | ⚠️ No modernization |
| New database query | `bibsonomy-database/` iBatis XML + expose via `LogicInterface` | Rare - check if existing query works |
| New domain field | `bibsonomy-model/` + iBatis XML + DTO mapping | Very rare - discuss first |

### 🚫 What NOT to Do

**Legacy Modules** (`bibsonomy-database`, `bibsonomy-webapp`, `bibsonomy-model`):
- ❌ DO NOT modernize "while you're there"
- ❌ DO NOT upgrade Spring 3.2, iBatis, or Java 8
- ❌ DO NOT refactor unless fixing a critical bug
- ❌ DO NOT add new features (use new modern modules instead)
- ❌ DO NOT complain about outdated dependencies (it's intentional)

**New Modules** (`bibsonomy-rest-api-v2`, `bibsonomy-webapp-v2`):
- ❌ DO NOT expose domain models (`org.bibsonomy.model.*`) directly in REST API
- ❌ DO NOT use auto-mapping libraries (MapStruct, etc.) - explicit mapping only
- ❌ DO NOT create exhaustive test suites - focus on integration tests

### ✅ Testing Philosophy

**Integration over Unit Tests**: Test real behavior, not mocks.

- **Legacy modules**: Pin behavior with integration tests, avoid brittle mocks
- **REST API v2**: Test actual HTTP endpoints, real LogicInterface calls
- **Frontend**: Test critical user flows and API integration, keep coverage light

## Quick Build & Run

### REST API v2

```bash
# Build (skip tests)
cd bibsonomy-rest-api-v2
JAVA_HOME=$JAVA_23_HOME mvn -Puse-toolchain-java23 -DskipTests -t ../toolchains.xml package

# Run locally with 'local' profile
JAVA_HOME=$JAVA_23_HOME java -jar target/bibsonomy-rest-api-v2-4.1.0.jar --spring.profiles.active=local
```

See `bibsonomy-rest-api-v2/CLAUDE.md` for detailed guidance.

### Frontend (Vue 3)

```bash
# Development server
cd bibsonomy-webapp-v2
bun install
bun run dev  # Opens at http://localhost:5173

# Build for production
bun run build
```

See `bibsonomy-webapp-v2/CLAUDE.md` for detailed guidance.

### Legacy Modules

```bash
# Full build
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Run specific module tests
mvn test -pl bibsonomy-<module-name>
```

## Core Domain Model Quick Reference

**Key Entities** (`bibsonomy-model`):
- `Post<T extends Resource>` - Generic post (publication or bookmark)
- `Resource` - Abstract base for `BibTex` and `Bookmark`
- `BibTex` - Publication/bibliographic entry
- `Bookmark` - Web bookmark
- `User` - User account
- `Tag` - User-defined tag
- `Group` - User group with permissions

**LogicInterface** - Main business logic interface (implemented by `DBLogic`):
- `getPosts(GroupingEntity)` - Query posts
- `getUsers(GroupingEntity)` - Query users
- `createPost(Post<?>, String)` - Create post
- `updatePost(Post<?>, String)` - Update post
- `deletePost(String, String)` - Delete post

## Key Technologies

**Modern Stack** (active development):
- **REST API v2**: Kotlin 1.9+, Spring Boot 3.x, Java 21
- **Frontend**: Vue 3, TypeScript, Vite, @tanstack/vue-query, Tailwind CSS, vue-i18n

**Legacy Stack** (maintained, not modernized):
- **Core**: Java 8, Spring 3.2, iBATIS 2.x
- **Database**: MySQL 5.x
- **Webapp**: JSP/JSTL, Spring MVC, DWR
- **Build**: Maven, JaCoCo, JUnit 4

## Architecture Patterns

### 1. DTO Decoupling (REST API v2)
REST API never exposes internal domain models directly. Always create explicit DTOs with mapping functions.

### 2. Adapter Pattern (Service Layer)
Kotlin service layer adapts legacy `LogicInterface` to modern REST API.

### 3. Dependency Reuse
New modules depend on `bibsonomy-model` and `bibsonomy-database` JARs - no code duplication.

### 4. Parallel Operation
New API and legacy webapp run side-by-side, both accessing the same MySQL database.

## Security Checklist

- ✅ No credentials/tokens/API keys in code (except test fixtures)
- ✅ Check for XSS vulnerabilities in web outputs
- ✅ Validate authentication/authorization in new endpoints
- ✅ SQL injection protection (iBatis handles this, but verify in new code)

## Common Gotchas

1. **Java 8 → Kotlin nullability**: Legacy models are nullable by default - handle with `?.` and `!!` carefully
2. **Spring XML import**: Use `@ImportResource` to load legacy Spring configs in Spring Boot
3. **iBatis SQL IDs**: Must be unique across all XML files
4. **Transaction boundaries**: `LogicInterface` manages transactions - don't create new ones in service layer
5. **DTO mapping**: Always explicit, never auto-mapping
6. **Legacy dependencies**: Never upgrade Spring 3.2 or iBatis in legacy modules

## Module-Specific Documentation

For detailed guidance on specific modules, see:

### New Modern Modules
- **`bibsonomy-rest-api-v2/CLAUDE.md`** - Kotlin REST API development (DTOs, services, controllers, testing)
- **`bibsonomy-webapp-v2/CLAUDE.md`** - Vue 3 frontend development (components, composables, i18n, testing)

### Legacy Modules
Legacy modules (`bibsonomy-database`, `bibsonomy-webapp`, `bibsonomy-model`) do NOT have specific CLAUDE.md files because:
- **They should not be actively developed** (only maintained)
- **No new features should be added** (use modern modules instead)
- **Only minimal bugfixes allowed** when absolutely necessary

To understand legacy architecture, see:
- `bibsonomy-model/src/main/java/org/bibsonomy/model/logic/LogicInterface.java` - Business logic interface
- `bibsonomy-database/src/main/resources/org/bibsonomy/database/bibsonomy-database-context.xml` - Spring config
- `bibsonomy-database/src/main/resources/sql/` - iBatis SQL mappings

## Key Files & Locations

**Strategy & Planning**:
- `.cursor/plans/bibsonomy_modernization_strategy_50e37204.plan.md` - Full modernization strategy
- `.coderabbit.yaml` - Code review rules and quality standards

**Configuration**:
- `.gitlab-ci.yml` - CI/CD pipeline (database setup, test commands)
- `misc/scripts/settings.xml` - Maven test settings
- `toolchains.xml` - Java toolchain configuration

**Legacy Core**:
- `bibsonomy-model/` - Domain model and `LogicInterface`
- `bibsonomy-database/` - iBatis implementation and database managers
- `bibsonomy-common/` - Shared utilities and exceptions

## Getting Help

- **Modernization questions**: See `.cursor/plans/bibsonomy_modernization_strategy_50e37204.plan.md`
- **REST API v2 development**: See `bibsonomy-rest-api-v2/CLAUDE.md`
- **Frontend development**: See `bibsonomy-webapp-v2/CLAUDE.md`
- **Legacy architecture**: Search existing modules for patterns (don't modify unless critical)
- **Domain model**: Read `bibsonomy-model` classes and JavaDoc
- **Code quality expectations**: Review `.coderabbit.yaml`

---

**Remember**: When in doubt, build new features in the modern modules (`bibsonomy-rest-api-v2`, `bibsonomy-webapp-v2`). Only touch legacy code for critical bugfixes.
