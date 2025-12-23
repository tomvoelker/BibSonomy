# BibSonomy REST API v2: Deferred Features

## Overview

This document catalogs features from the legacy BibSonomy system that are **deferred to post-MVP** for the REST API v2 implementation. These features are intentionally excluded from the initial release to focus on core functionality (posts, bookmarks, publications, users, groups, tags, and basic search). However, they remain important for future iterations and are documented here for planning purposes.

**Status**: All features listed below are planned for future implementation but not included in the MVP release.

---

## Table of Contents

1. [CRIS Features (Current Research Information System)](#1-cris-features-current-research-information-system)
   - [1.1 Person Pages](#11-person-pages)
   - [1.2 Project Pages](#12-project-pages)
   - [1.3 Organization Pages](#13-organization-pages)
   - [1.4 Publication Reporting](#14-publication-reporting)
2. [Synchronization with External Services](#2-synchronization-with-external-services)
3. [Statistics Pages](#3-statistics-pages)
4. [Did You Know Messages](#4-did-you-know-messages)
5. [Hash Examples & Scraper Info Pages](#5-hash-examples--scraper-info-pages)
6. [Concepts/Relations (Tag Hierarchies)](#6-conceptsrelations-tag-hierarchies)
7. [Custom Layouts](#7-custom-layouts)
8. [Implementation Priority Matrix](#8-implementation-priority-matrix)

---

## 1. CRIS Features (Current Research Information System)

### Overview

BibSonomy includes CRIS (Current Research Information System) features for managing researchers, projects, and organizations. These features are separate from the core bookmark/publication sharing functionality and represent a distinct domain requiring specialized modeling and UI.

**Why Deferred**: CRIS features are complex, institutional-focused, and serve a different use case than the core social bookmarking functionality. They require extensive domain modeling, complex relationships between entities (Person-Publication, Person-Project, Project-Organization), and specialized reporting/export features. The MVP focuses on individual researchers and small groups using core features.

---

### 1.1 Person Pages

**Description**: Person entities represent real-world researchers (distinct from User accounts). Person pages display researcher profiles including publications, projects, affiliations, and external identifiers (ORCID, ResearcherID).

**Codebase References**:

**Controllers**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/person/PersonPageController.java` - Main person page view
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/person/PersonDisambiguationPageController.java` - Handles disambiguation of duplicate persons
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/person/MergePersonController.java` - Merges duplicate person records
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/person/EditPersonController.java` - Edit person details
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/person/EditPersonDetailsController.java` - Edit specific person fields
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/person/EditRelationController.java` - Edit person-resource relations
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/person/relation/AddPersonResourceRelationController.java` - Link person to publication
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/person/relation/DeletePersonResourceRelationController.java` - Unlink person from publication
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/cris/PersonsPageController.java` - List all persons
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/ajax/person/PersonPublicationsAjaxController.java` - AJAX: Load person's publications
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/ajax/person/PersonProjectsAjaxController.java` - AJAX: Load person's projects
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/ajax/person/PersonSimilarAjaxController.java` - AJAX: Find similar persons (for disambiguation)

**Domain Models**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/Person.java` - Core Person entity
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/PersonName.java` - Person name representation
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/PersonMatch.java` - Person matching/disambiguation
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/PersonMergeFieldConflict.java` - Conflict resolution during merge
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/ResourcePersonRelation.java` - Link between Person and Publication (referenced in Person.java)

**Database Tables**:
- `person` - Core person data (see: `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/resources/database/migrations/3.3.0/person-entities.sql`)
- `person_name` - Multiple names per person (aliases, former names)
- `person_additional_keys` - External identifiers (ORCID, ResearcherID, etc.) (see: `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/resources/database/migrations/4.0.0/add_person_additional_keys.sql`)
- `person_resource_relation` - Links persons to publications
- `similar_persons` - Pre-computed similarity scores for disambiguation (see: `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/resources/database/migrations/3.8.13/add_table_similar_persons.sql`)

**Query Builders**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/logic/querybuilder/PersonQueryBuilder.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/logic/query/PersonQuery.java`

**Legacy REST API**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-rest-server/src/main/java/org/bibsonomy/rest/strategy/PersonsHandler.java` - REST endpoint for persons
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-rest-server/src/main/java/org/bibsonomy/rest/strategy/persons/GetPersonPostsStrategy.java` - Get publications for person

**Implementation Notes**:

1. **DTOs Required**:
   - `PersonDto` - Person profile data
   - `PersonNameDto` - Name representation
   - `PersonRelationDto` - Person-publication link
   - `PersonMatchDto` - Disambiguation suggestions

2. **Key Endpoints**:
   - `GET /api/v2/persons` - List all persons (with filtering/sorting)
   - `GET /api/v2/persons/{personId}` - Get person details
   - `POST /api/v2/persons` - Create new person
   - `PUT /api/v2/persons/{personId}` - Update person
   - `DELETE /api/v2/persons/{personId}` - Delete person
   - `GET /api/v2/persons/{personId}/publications` - Get person's publications
   - `GET /api/v2/persons/{personId}/projects` - Get person's projects
   - `POST /api/v2/persons/{personId}/publications/{resourceHash}` - Link person to publication
   - `DELETE /api/v2/persons/{personId}/publications/{resourceHash}` - Unlink person from publication
   - `GET /api/v2/persons/{personId}/similar` - Find similar persons (for disambiguation)
   - `POST /api/v2/persons/merge` - Merge duplicate persons

3. **Challenges**:
   - **Disambiguation**: Multiple persons with same name must be disambiguated (requires similarity algorithms)
   - **Merging**: Merging duplicate persons requires conflict resolution for conflicting fields
   - **External IDs**: Integration with ORCID, ResearcherID, DNB (Deutsche Nationalbibliothek)
   - **User-Person linking**: Persons can be linked to User accounts (many-to-one)

4. **Dependencies**:
   - Person pages depend on Publication API (already in MVP)
   - Person pages depend on Project API (deferred, see 1.2)

**Complexity**: **High**

**Post-MVP Priority**: **Medium** (important for institutional/academic users, but not core social bookmarking)

---

### 1.2 Project Pages

**Description**: Project entities represent research projects with funding information, timelines, participants (persons), and related publications. Project pages display project details, team members, and outputs.

**Codebase References**:

**Controllers**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/cris/ProjectPageController.java` - View single project
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/cris/ProjectsPageController.java` - List all projects
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/actions/EditProjectController.java` - Edit project
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/actions/DeleteProjectController.java` - Delete project
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/cris/ajax/ProjectPersonLinkAjaxController.java` - AJAX: Link/unlink persons to projects

**Domain Models**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/cris/Project.java` - Core Project entity
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/cris/ProjectPersonLinkType.java` - Type of person-project relationship (PI, collaborator, etc.)
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/validation/ProjectValidator.java` - Validation logic

**Database Tables**:
- `project` - Core project data (title, description, dates, funding)
- `project_person` - Links projects to persons (with role/link type)
- `project_publication` - Links projects to publications

**Query Builders**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/logic/querybuilder/ProjectQueryBuilder.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/logic/query/ProjectQuery.java`

**Legacy REST API**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-rest-server/src/main/java/org/bibsonomy/rest/strategy/ProjectsHandler.java` - REST endpoint for projects

**Implementation Notes**:

1. **DTOs Required**:
   - `ProjectDto` - Project data (title, description, funding, dates)
   - `ProjectPersonLinkDto` - Person-project relationship
   - `ProjectPublicationLinkDto` - Project-publication relationship

2. **Key Endpoints**:
   - `GET /api/v2/projects` - List all projects
   - `GET /api/v2/projects/{projectId}` - Get project details
   - `POST /api/v2/projects` - Create new project
   - `PUT /api/v2/projects/{projectId}` - Update project
   - `DELETE /api/v2/projects/{projectId}` - Delete project
   - `GET /api/v2/projects/{projectId}/persons` - Get project team members
   - `POST /api/v2/projects/{projectId}/persons/{personId}` - Add person to project
   - `DELETE /api/v2/projects/{projectId}/persons/{personId}` - Remove person from project
   - `GET /api/v2/projects/{projectId}/publications` - Get project publications

3. **Challenges**:
   - Project dates (start/end) require date range queries
   - Funding information requires structured data (amount, source, currency)
   - Person roles within projects require additional metadata

4. **Dependencies**:
   - Requires Person API (see 1.1)
   - Requires Publication API (already in MVP)

**Complexity**: **Moderate**

**Post-MVP Priority**: **Medium**

---

### 1.3 Organization Pages

**Description**: Organization entities represent institutions, departments, or research groups. Organization pages display members, projects, and publications.

**Codebase References**:

**Controllers**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/cris/OrganizationPageController.java` - View organization page

**Domain Models**:
- Likely uses Group entity with specialized CRIS attributes
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/cris/GroupPersonLinkType.java` - Type of person-organization relationship

**Database Tables**:
- Likely extends `group` table with CRIS-specific fields
- `group_person` - Links organizations to persons

**Implementation Notes**:

1. **DTOs Required**:
   - `OrganizationDto` - Organization data
   - `OrganizationPersonLinkDto` - Person-organization relationship

2. **Key Endpoints**:
   - `GET /api/v2/organizations` - List all organizations
   - `GET /api/v2/organizations/{orgId}` - Get organization details
   - `POST /api/v2/organizations` - Create new organization
   - `PUT /api/v2/organizations/{orgId}` - Update organization
   - `DELETE /api/v2/organizations/{orgId}` - Delete organization
   - `GET /api/v2/organizations/{orgId}/persons` - Get organization members
   - `GET /api/v2/organizations/{orgId}/projects` - Get organization projects

3. **Challenges**:
   - Organization hierarchies (departments within universities)
   - External identifiers (ROR, GRID, etc.)
   - Institutional access control

4. **Dependencies**:
   - Requires Person API (see 1.1)
   - Requires Project API (see 1.2)

**Complexity**: **Moderate**

**Post-MVP Priority**: **Low** (least critical CRIS feature)

---

### 1.4 Publication Reporting

**Description**: Export publication lists for reporting purposes (institutional reports, CVs, etc.). Includes filtering by person, project, organization, and date range. Supports multiple output formats (PDF, Word, BibTeX, etc.).

**Codebase References**:

**Controllers**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/reporting/PublicationReportingPageController.java` - Publication reporting
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/reporting/PersonReportingPageController.java` - Person-specific reports
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/reporting/ProjectReportingPageController.java` - Project-specific reports
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/reporting/AbstractReportingPageController.java` - Base reporting controller

**Implementation Notes**:

1. **Key Endpoints**:
   - `GET /api/v2/reporting/publications` - Generate publication report (with filtering)
   - `GET /api/v2/reporting/persons/{personId}/publications` - Person's publication list
   - `GET /api/v2/reporting/projects/{projectId}/publications` - Project's publication list

2. **Query Parameters**:
   - `person` - Filter by person ID
   - `project` - Filter by project ID
   - `organization` - Filter by organization ID
   - `startDate` / `endDate` - Date range
   - `format` - Output format (json, bibtex, pdf, docx)

3. **Challenges**:
   - PDF/Word generation requires layout engine integration
   - Citation style selection (APA, MLA, IEEE, etc.) - see Custom Layouts (section 7)
   - Grouping/sorting options (by year, by type, by project)

4. **Dependencies**:
   - Requires Person API (see 1.1)
   - Requires Project API (see 1.2)
   - Requires Custom Layouts (see section 7)

**Complexity**: **Moderate to High**

**Post-MVP Priority**: **Medium** (important for institutional users)

---

## 2. Synchronization with External Services

**Description**: Bidirectional synchronization with external reference managers and services (Mendeley, Zotero, CiteULike, etc.). Allows users to sync their BibSonomy posts with external accounts.

**Why Deferred**: Synchronization requires OAuth integration with third-party services, complex conflict resolution logic, and ongoing maintenance as external APIs change. It's a power-user feature that doesn't affect core functionality.

**Codebase References**:

**Controllers**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/SyncPageController.java` - Main sync page
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/actions/SyncSettingsController.java` - Sync settings management

**Domain Models**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/sync/SyncService.java` - Sync service configuration
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/sync/SynchronizationData.java` - Sync metadata
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/sync/SynchronizationPost.java` - Sync post data
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/sync/SynchronizationStatus.java` - Sync status enum
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/sync/SynchronizationDirection.java` - Sync direction (push/pull/bidirectional)
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/sync/ConflictResolutionStrategy.java` - How to resolve conflicts

**Synchronization Client**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-synchronization/src/main/java/org/bibsonomy/synchronization/TwoStepSynchronizationClient.java` - Main sync client
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-synchronization/src/main/java/org/bibsonomy/synchronization/AbstractSynchronizationClient.java` - Base sync client
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-synchronization/src/main/java/org/bibsonomy/synchronization/SynchronizationClient.java` - Sync interface
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-synchronization/src/main/java/org/bibsonomy/synchronization/AutoSync.java` - Automatic sync scheduler

**Database Tables**:
- `sync_service` - User's sync service configurations
- `sync_data` - Synchronization history and metadata

**LogicInterface Methods**:
- `LogicInterface.getSyncServiceSettings(String userName, URI service, boolean server)` - Get user's sync settings
- `SyncLogicInterface` - Specialized interface for sync operations (see: `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-model/src/main/java/org/bibsonomy/model/sync/SyncLogicInterface.java`)

**Implementation Notes**:

1. **DTOs Required**:
   - `SyncServiceDto` - Sync service configuration
   - `SyncStatusDto` - Current sync status
   - `SyncConflictDto` - Conflict information

2. **Key Endpoints**:
   - `GET /api/v2/sync/services` - List user's sync services
   - `POST /api/v2/sync/services` - Add new sync service
   - `PUT /api/v2/sync/services/{serviceId}` - Update sync settings
   - `DELETE /api/v2/sync/services/{serviceId}` - Remove sync service
   - `POST /api/v2/sync/services/{serviceId}/sync` - Trigger manual sync
   - `GET /api/v2/sync/services/{serviceId}/status` - Get sync status
   - `GET /api/v2/sync/services/{serviceId}/history` - Get sync history

3. **Supported Services** (from legacy system):
   - Mendeley
   - Zotero
   - CiteULike (deprecated)
   - Other BibSonomy instances

4. **Challenges**:
   - OAuth 2.0 integration with multiple services
   - Conflict resolution (same post modified in both systems)
   - Handling deleted posts (delete in one system, exists in other)
   - Rate limiting from external APIs
   - Schema mapping (BibSonomy fields ↔ external service fields)
   - Two-step sync process: plan → confirm (requires session state)

5. **Dependencies**:
   - Requires Publication API (already in MVP)
   - Requires Bookmark API (already in MVP)
   - External: Mendeley API, Zotero API, etc.

**Complexity**: **High** (external dependencies, OAuth, conflict resolution)

**Post-MVP Priority**: **Low to Medium** (nice-to-have, but maintenance-intensive)

---

## 3. Statistics Pages

**Description**: System-wide statistics and metrics (admin-only). Provides counts of users, posts, tags, documents over time with filtering by date range, resource type, and grouping entity.

**Why Deferred**: Statistics are admin-only features useful for monitoring system health and growth. They don't affect end-user functionality and can be added once the core API is stable and generating real usage data.

**Codebase References**:

**Controllers**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/StatisticsController.java` - Main statistics endpoint

**LogicInterface Methods**:
- `LogicInterface.getUserStatistics(GroupingEntity, Set<Filter>, String, SpamStatus, Date, Date)` - User counts
- `LogicInterface.getTagStatistics(Class, GroupingEntity, String, String, Set<String>, ConceptStatus, Set<Filter>, Date, Date, int, int)` - Tag counts
- `LogicInterface.getPostStatistics(Class, GroupingEntity, String, String, Set<String>, Set<String>, Set<Filter>, String, Date, Date, int, int)` - Post counts
- `LogicInterface.getDocumentStatistics(GroupingEntity, String, Set<Filter>, Date, Date)` - Document counts

**Database Managers**:
- `StatisticsDatabaseManager` - Database manager for statistics queries

**Implementation Notes**:

1. **DTOs Required**:
   - `StatisticsDto` - Statistics data (count, grouping, filters)
   - `TimeSeriesStatsDto` - Statistics over time

2. **Key Endpoints**:
   - `GET /api/v2/admin/statistics/users` - User counts
   - `GET /api/v2/admin/statistics/posts` - Post counts
   - `GET /api/v2/admin/statistics/tags` - Tag counts
   - `GET /api/v2/admin/statistics/documents` - Document counts

3. **Query Parameters**:
   - `grouping` - ALL, USER, GROUP
   - `resourceType` - bibtex, bookmark
   - `startDate` / `endDate` - Date range
   - `interval` - Time interval (day, week, month, year)
   - `unit` - StatisticsUnit enum value
   - `filters` - Filter set (e.g., SPAMMER filter)

4. **Challenges**:
   - Performance on large datasets (requires indexing and caching)
   - Time-series aggregation (by day/week/month/year)
   - Real-time vs. cached statistics

5. **Dependencies**:
   - Requires admin authentication/authorization

**Complexity**: **Moderate**

**Post-MVP Priority**: **Low** (admin-only, not user-facing)

---

## 4. Did You Know Messages

**Description**: Random informational messages displayed to users to highlight features and tips. Injected into various page controllers to provide contextual help.

**Why Deferred**: This is a UI enhancement feature that doesn't affect core API functionality. It's essentially static content management and can be added once the frontend is more mature.

**Codebase References**:

**Controllers**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/DidYouKnowMessageController.java` - Base controller for "Did You Know" messages

**Model**:
- `DidYouKnowMessage` class (referenced in controller)

**Configuration**:
- Messages configured in Spring XML files (`bibsonomy-servlet-*.xml`)

**Implementation Notes**:

1. **DTOs Required**:
   - `DidYouKnowMessageDto` - Message content (text, link, context)

2. **Key Endpoints**:
   - `GET /api/v2/didyouknow/random` - Get random message
   - `GET /api/v2/didyouknow/by-context?context={context}` - Get contextual message

3. **Context Types** (examples):
   - `homepage` - Homepage tips
   - `post_create` - Post creation tips
   - `search` - Search tips

4. **Challenges**:
   - Internationalization (German/English messages)
   - Context-aware message selection
   - Message versioning (don't show outdated tips)

5. **Dependencies**:
   - None (standalone feature)

**Complexity**: **Simple**

**Post-MVP Priority**: **Low** (UI enhancement, not core functionality)

---

## 5. Hash Examples & Scraper Info Pages

**Description**: Informational pages showing how BibSonomy hashes work and which scrapers are available for metadata extraction from external sources.

**Why Deferred**: These are documentation/help pages that don't affect core API functionality. They're useful for advanced users but not essential for MVP.

**Codebase References**:

**Controllers**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/HashExampleController.java` - Hash example page (`/hashexample`)
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/ScraperInfoController.java` - Scraper info page (`/scraperinfo`)

**Scraper Infrastructure**:
- `KDEScraperFactory` - Factory for available scrapers (referenced in ScraperInfoController)
- `Scraper` interface - Base scraper interface

**Implementation Notes**:

**Hash Examples**:
1. Shows how BibTeX publications are hashed (for deduplication)
2. Demonstrates `Resource.recalculateHashes()` behavior
3. Educational/debugging tool

**Scraper Info**:
1. Lists all available scrapers (web scrapers for extracting publication metadata)
2. Shows supported sites (e.g., ACM, IEEE, arXiv, Google Scholar, etc.)
3. Displays scraper capabilities (what metadata can be extracted)

**Key Endpoints** (if implemented):
- `GET /api/v2/info/hash-example` - Hash calculation example
- `GET /api/v2/info/scrapers` - List available scrapers
- `GET /api/v2/info/scrapers/{scraperId}` - Scraper details

**Complexity**: **Simple**

**Post-MVP Priority**: **Low** (informational only)

---

## 6. Concepts/Relations (Tag Hierarchies)

**Description**: Hierarchical tag relationships (concepts). Tags can have broader/narrower relationships, forming taxonomies. The `c:` prefix denotes concept tags (e.g., `c:algorithm` is a concept, `algorithm` is a regular tag).

**Why Deferred**: Concept/relation features require additional modeling (tag hierarchies, concept status), specialized UI for managing relationships, and complex query logic. The MVP focuses on flat tagging, which is simpler and covers most use cases. Hierarchical tags are a power-user feature that can be added later.

**Codebase References**:

**Controllers**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/ConceptPageController.java` - View concept page (`/concept/tag/CONCEPT`)
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/RelationsController.java` - View popular concepts (`/concepts`)
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/controller/ajax/ConceptController.java` - AJAX concept operations

**Domain Models**:
- `Tag` model has concept-related fields (see concept prefix: `Tag.CONCEPT_PREFIX = "c:"`)
- `ConceptStatus` enum - PICKED, UNPICKED, ALL (see: `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-common/src/main/java/org/bibsonomy/common/enums/ConceptStatus.java`)

**LogicInterface Methods**:
- `LogicInterface.getConcepts(Class, GroupingEntity, String, String, Set<String>, ConceptStatus, int, int)` - Get concept list
- `LogicInterface.getConceptDetails(String, GroupingEntity, String)` - Get concept details (returns Tag with relations)

**Database**:
- `tag_relation` table - Stores hierarchical relationships between tags
- Concept chain processing (see: `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-database/src/main/java/org/bibsonomy/database/managers/chain/concept/`)

**Legacy REST API**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-rest-server/src/main/java/org/bibsonomy/rest/strategy/concepts/GetConceptsStrategy.java`

**Implementation Notes**:

1. **DTOs Required**:
   - `ConceptDto` - Concept data (extends TagDto with relations)
   - `ConceptRelationDto` - Broader/narrower relationship

2. **Key Endpoints**:
   - `GET /api/v2/concepts` - List all concepts
   - `GET /api/v2/concepts/{conceptName}` - Get concept details (with broader/narrower concepts)
   - `POST /api/v2/concepts` - Create new concept
   - `PUT /api/v2/concepts/{conceptName}` - Update concept
   - `DELETE /api/v2/concepts/{conceptName}` - Delete concept
   - `POST /api/v2/concepts/{conceptName}/relations` - Add broader/narrower relation
   - `DELETE /api/v2/concepts/{conceptName}/relations/{relatedConcept}` - Remove relation
   - `GET /api/v2/concepts/{conceptName}/posts` - Get posts tagged with concept (including narrower concepts)

3. **Concept Status**:
   - `PICKED` - User has explicitly picked this concept (subscribed)
   - `UNPICKED` - User has explicitly unpicked this concept
   - `ALL` - Show all concepts

4. **Challenges**:
   - Detecting circular relationships (A broader than B, B broader than A)
   - Transitive closure (if A → B → C, then posts with C should appear under A)
   - Conflict resolution (different users may define conflicting hierarchies)
   - UI complexity (visualizing tag hierarchies)

5. **Dependencies**:
   - Requires Tag API (already in MVP)
   - Requires Post API (already in MVP)

**Complexity**: **Moderate to High** (hierarchical data, complex queries)

**Post-MVP Priority**: **Medium** (useful for organizing large tag spaces, but MVP flat tags work for most users)

---

## 7. Custom Layouts

**Description**: User-defined citation styles and export formats. Users can create custom JabRef-style layouts or CSL (Citation Style Language) styles for formatting publication lists.

**Why Deferred**: Custom layouts require a layout engine (JabRef layout renderer or CSL processor), user-generated content management, and extensive testing across different output formats. The MVP will use a few pre-defined citation styles. Custom layouts can be added once the core export functionality is solid.

**Codebase References**:

**Layout Rendering**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-layout/` - Entire layout module
  - JabRef layout renderer (`bibsonomy-layout/src/main/java/org/bibsonomy/layout/jabref/`)
  - CSL (Citation Style Language) renderer (`bibsonomy-layout/src/main/java/org/bibsonomy/layout/citeproc/`)
  - Standard layouts (`bibsonomy-layout/src/main/java/org/bibsonomy/layout/standard/`)

**Key Classes**:
- `JabrefLayoutRenderer` - Renders publications using JabRef layout format
- `CSLProcessor` - Renders publications using CSL styles
- `AdhocRenderer` - Ad-hoc CSL rendering
- `CSLFilesManager` - Manages CSL style files

**Web Views**:
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/view/LayoutView.java`
- `/Users/tomvolker/localProjects/dsc/BibSonomy/bibsonomy-webapp/src/main/java/org/bibsonomy/webapp/view/ExportLayoutView.java`

**Implementation Notes**:

1. **Pre-defined Layouts** (for MVP export):
   - BibTeX
   - JSON
   - RIS
   - CSL-JSON
   - Simple HTML (using a default CSL style like APA)

2. **Custom Layout Features** (post-MVP):
   - User can upload JabRef `.layout` files
   - User can select from 1000+ CSL styles (from Zotero CSL repository)
   - User can create/edit custom CSL styles in UI
   - Layouts can be shared with groups

3. **Key Endpoints**:
   - `GET /api/v2/layouts` - List available layouts (system + user-created)
   - `GET /api/v2/layouts/{layoutId}` - Get layout definition
   - `POST /api/v2/layouts` - Create new custom layout
   - `PUT /api/v2/layouts/{layoutId}` - Update layout
   - `DELETE /api/v2/layouts/{layoutId}` - Delete layout
   - `POST /api/v2/posts/export?layout={layoutId}` - Export posts using layout

4. **Layout Types**:
   - JabRef layout (legacy, text-based template)
   - CSL (modern, XML-based citation style)
   - Custom HTML/PDF templates

5. **Challenges**:
   - Security: User-uploaded layouts could contain malicious code (need sandboxing)
   - CSL complexity: 1000+ styles to test/support
   - Performance: Layout rendering can be slow for large publication lists
   - Internationalization: Different citation styles for different languages

6. **Dependencies**:
   - Requires Publication API (already in MVP)
   - Requires export functionality (partially in MVP)

**Complexity**: **High** (layout engines, security, performance)

**Post-MVP Priority**: **Medium** (important for academic users who need specific citation styles)

---

## 8. Implementation Priority Matrix

Summary of deferred features by priority and complexity:

| Feature | Complexity | Priority | Estimated Effort | Dependencies |
|---------|-----------|----------|------------------|--------------|
| **CRIS: Person Pages** | High | Medium | 3-4 weeks | Publication API (MVP) |
| **CRIS: Project Pages** | Moderate | Medium | 2-3 weeks | Person API, Publication API |
| **CRIS: Organization Pages** | Moderate | Low | 2-3 weeks | Person API, Project API |
| **CRIS: Publication Reporting** | Moderate-High | Medium | 2-3 weeks | Person/Project/Org APIs, Custom Layouts |
| **Synchronization** | High | Low-Medium | 4-6 weeks | Publication/Bookmark APIs (MVP), External APIs |
| **Statistics Pages** | Moderate | Low | 1-2 weeks | None |
| **Did You Know Messages** | Simple | Low | 1 week | None |
| **Hash Examples & Scraper Info** | Simple | Low | 1 week | None |
| **Concepts/Relations** | Moderate-High | Medium | 3-4 weeks | Tag/Post APIs (MVP) |
| **Custom Layouts** | High | Medium | 4-5 weeks | Export functionality (MVP) |

---

## Recommended Implementation Order (Post-MVP)

Based on user value, dependencies, and complexity:

### Phase 1: Core Extensions (Months 1-3)

1. **Person Pages** - Foundational CRIS feature, high user value for academic users
2. **Concepts/Relations** - Extends existing tag functionality, moderate complexity
3. **Statistics Pages** - Admin monitoring, useful early to track API adoption

### Phase 2: Advanced CRIS (Months 4-6)

1. **Project Pages** - Builds on Person API
2. **Publication Reporting** - Combines Person/Project APIs
3. **Organization Pages** - Completes CRIS feature set

### Phase 3: Power Features (Months 7-9)

1. **Custom Layouts** - Important for academic citation workflows
2. **Synchronization** - Power-user feature, high maintenance overhead

### Phase 4: Polish (Months 10-12)

1. **Did You Know Messages** - UI enhancement
2. **Hash Examples & Scraper Info** - Educational content

---

## Notes for Future Implementation

### General Principles

1. **Always create DTOs**: Never expose domain models (`Person`, `Project`, etc.) directly in REST API
2. **Explicit mapping**: Write explicit `toDto()` extension functions, avoid auto-mapping libraries
3. **Integration tests**: Write REST-level integration tests for all endpoints
4. **Null safety**: Handle Java → Kotlin nullability conversions carefully
5. **Consistent HTTP semantics**: Use proper status codes (200, 201, 204, 400, 404, etc.)

### Database Access

All features depend on `LogicInterface` from the legacy database layer:
- Inject `LogicInterface` via Spring Bridge (see `DatabaseBridgeConfig`)
- Call existing `LogicInterface` methods where available
- For new queries, add to `bibsonomy-database` iBatis XML files first, then expose via `LogicInterface`

### External Dependencies

Features requiring external APIs (Synchronization, Scraper Info):
- Document external API rate limits
- Implement retry logic with exponential backoff
- Cache external API responses where appropriate
- Handle API deprecation/changes gracefully

### User Experience

Features with complex UI (Concepts, Custom Layouts):
- Collaborate closely with frontend team
- Prototype UI mockups before backend implementation
- Consider mobile/responsive design constraints

---

## Conclusion

This document provides a comprehensive roadmap for post-MVP features. Each feature is documented with:
- Clear description and rationale for deferral
- Exact file paths to legacy implementation
- Implementation notes for future developers
- Complexity and priority assessment

All deferred features are **planned for future implementation** and should be revisited after the MVP is stable and in production use.

**Next Steps**:
1. Complete MVP (posts, bookmarks, publications, users, groups, tags, search)
2. Deploy and gather user feedback
3. Prioritize post-MVP features based on real usage patterns
4. Implement in phases as outlined above

---

**Document Version**: 1.0
**Last Updated**: 2025-12-14
**Author**: Generated from legacy codebase analysis
