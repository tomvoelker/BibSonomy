# BibSonomy REST API v2 - Feature Scope

This document defines the feature scope for the MVP (Minimum Viable Product) of the new REST API v2.

## Overview

The new API focuses on **core content management and collaboration** features, removing social networking overhead and deferring complex research-specific functionality to post-MVP.

---

## ✅ MVP Features (In Scope)

### 1. Content Management

#### Posts (Bookmarks + Publications)
- **CRUD operations**: Create, read, update, delete posts
- **Dual resource types**: Bookmarks (web URLs) and Publications (BibTeX entries)
- **Batch operations**: Bulk import, batch editing
- **Duplicate detection**: Hash-based for publications, URL hash for bookmarks
- **Post history**: Track changes over time

#### Import Capabilities
- **Publications**:
  - BibTeX file upload
  - BibTeX snippet (paste)
  - DOI/ISBN lookup
  - URL scraping for metadata
  - PDF upload with metadata extraction
  - EndNote format
  - Batch import from BibTeX files
- **Bookmarks**:
  - Browser export files (Firefox, Chrome, Safari, Opera)
  - Delicious import (if still relevant)
  - Manual entry with URL scraping

#### Export Capabilities
**All formats supported** (JSON is default):
- **Bibliographic**: BibTeX, EndNote, CSL (Citation Style Language), SWRC (Semantic Web)
- **Structured data**: JSON, XML, CSV
- **Feeds**: RSS (bookmarks), RSS (publications), APARSS
- **Formatted**: HTML (formatted publications/bookmarks)
- **Semantic**: FOAF (Friend of a Friend RDF)
- **Browser**: Netscape Bookmark HTML
- **Custom**: JabRef layouts, MS Office XML

**Note:** Custom layouts are supported but not high priority for MVP.

#### Documents (Publications only)
- PDF upload and storage
- Document download
- Metadata extraction from PDFs
- Full-text indexing (if Elasticsearch integration maintained)

---

### 2. Tagging System

- **Free-form tagging**: User-defined tags (case-sensitive)
- **Tag autocomplete**: Suggest tags while typing
- **Tag recommendations**: Context-aware (different for bookmarks vs publications)
- **Related tags**: Discover co-occurring tags
- **Tag cloud**: Visual frequency representation
- **System tags**: Special `sys:*` prefix tags for metadata
  - **"Relevant For" group tagging**: `sys:relevantFor:GROUP` - mark posts as relevant for a group without full sharing

---

### 3. User Management

#### Authentication
- **OAuth2/JWT**: Modern token-based authentication (replacing HTTP Basic Auth)
- **OpenID Connect**: External authentication providers
- **Registration**: Standard user registration with email activation
- **Login**: Username/password authentication
- **Password reset**: Email-based password recovery
- **API token management**: Users can generate/revoke API keys

**Note:** LDAP and SAML authentication were supported in legacy system but are **removed** in v2. They can be re-added as custom authentication providers if needed.

#### User Profile
- Real name, email, homepage
- Profile picture (upload or Gravatar)
- Biography, interests, profession, institution
- Language preference (German/English)

#### User Settings
- Display preferences (items per page, layout style)
- Tag cloud settings (min frequency, max count)
- Default groups for new posts
- Email notifications
- OAuth application management (view/revoke connected apps)

---

### 4. Groups & Collaboration

#### Group Features
- **Create/join groups**: Public, private, or viewable groups
- **Group types**:
  - **Public**: Anyone can view and join
  - **Private**: Members only
  - **Viewable**: Non-members can view but not edit
- **Member management**:
  - Roles: Admin, Moderator, Member
  - Membership approval workflow
  - Invite users
  - Remove members
- **Group settings**:
  - Description
  - Shared documents (allow members to access PDFs)
  - Preset tags (suggested tags for group posts)
  - Allow/disallow join requests
- **Group hierarchy**: Organizations with subgroups (parent-child relationships)
- **Group posts**: View all posts shared with a group

#### Post Visibility
- Posts can be shared with multiple groups simultaneously
- Privacy levels:
  - `PUBLIC` (group ID 0)
  - `PRIVATE` (group ID 1)
  - Specific groups (group ID ≥ 3)

**Note:** "FRIENDS" privacy level (group ID 2) is removed since Friends feature is excluded.

---

### 5. Search & Discovery

#### Search
- **Full-text search**: Search across post titles, descriptions, tags, authors
- **Query operators**: AND, OR (basic boolean search)
- **Scope filters**:
  - User-specific: `user:USERNAME`
  - Group-specific: `group:GROUPNAME`
- **Sort options**: Relevance, date, popularity (FolkRank)
- **Tag filtering**: Combine search with tag filters
- **Resource type filter**: Filter by bookmarks, publications, or both

#### Discovery Pages
- **Popular posts**: Trending posts (recent, configurable time window)
- **Homepage feed**: Personalized feed (logged in) or global feed (logged out)
- **Tag pages**: All posts with a specific tag (supports multiple tags)
- **Author pages**: Publications by specific author (with disambiguation)
- **Related content**: Similar posts, related tags, related users (by tag overlap)

---

### 6. Special Features

#### BibTeX Key Navigation
- Direct URL lookup by BibTeX citation key
- Pattern: `/api/v2/bibtexkey/{key}`
- User-specific keys: `/api/v2/bibtexkey/{key}?user={username}`
- Conflict resolution (multiple posts with same key)

#### Gold Standard / Community Posts
- Special post type for research validation
- Reference relations: Post A references Post B
- Part-of relations: Post A is part of Post B
- Used for annotation and academic gold standard creation
- CRUD operations: Create, update, delete community posts and relations

---

## ❌ Removed Features (Not in v2)

The following features existed in the legacy system but are **excluded** from API v2:

### Social Networking Features
- **Friends / "Spheres"**: User-to-user friend relationships and tagged friend groups
- **Followers**: Asymmetric follow relationships
- **Post Sharing / Inbox**: Private sharing system where users send posts to other users

**Rationale**: Reduces social networking complexity; focus on content management and group collaboration instead.

### User Convenience Features
- **Clipboard**: Temporary collection for batch operations
- **MySearch / Saved Searches**: Persistent saved search queries

**Rationale**: Users can bookmark URLs or use browser features instead.

### Content Features
- **Recommendations**: Personalized post and tag recommendations
- **Comments/Discussion**: Post comments and reviews

**Rationale**: Simplifies MVP; recommendations require separate ML infrastructure.

### User Profile Features
- **CV/Wiki**: User curriculum vitae pages with wiki markup

**Rationale**: Niche feature; users can link to external CV pages.

### Meta Features
- **Genealogy**: (No evidence found; may have been removed already)

See [REMOVED_FEATURES.md](REMOVED_FEATURES.md) for detailed codebase references.

---

## ⏸️ Deferred Features (Post-MVP)

These features will be implemented **after MVP** is stable:

### Research Features
- **CRIS (Current Research Information System)**:
  - Person Pages (researcher profiles, ORCID integration)
  - Project Pages (research projects)
  - Organization Pages (university departments)
  - Publication Reporting (email templates, reports)

**Rationale**: Large subsystem (20-30% of legacy codebase); defer to focus on core MVP.

### External Integration
- **Synchronization with External Services**: Mendeley, Zotero, CiteULike (legacy)

**Rationale**: External APIs may have changed; requires validation and updates.

### Analytics & Metadata
- **Statistics Pages**: Detailed statistics on users, groups, tags, publications

**Rationale**: Can be added incrementally; not blocking for core functionality.

### User Experience
- **"Did You Know" Messages**: Educational tips and prompts

**Rationale**: Needs redesign for new UI; low priority.

### Documentation & Tools
- **Hash Examples**: BibTeX hashing demonstration
- **Scraper Info Pages**: Supported scraper services documentation

**Rationale**: Scrapers will likely be replaced with external dependencies; documentation can move elsewhere.

### Advanced Tagging
- **Concepts/Relations**: User-defined tag hierarchies and relationships

**Rationale**: Needs more design thought; potentially complex UX; not blocking for MVP.

### Advanced Export
- **Custom Layouts**: User-defined export templates (JabRef, MS Office XML, custom CSL)

**Rationale**: Will need to work eventually but not high priority; standard formats sufficient for MVP.

See [DEFERRED_FEATURES.md](DEFERRED_FEATURES.md) for detailed codebase references and implementation notes.

---

## API Design Principles

### 1. JSON-First
- **Default format**: JSON
- **Optional formats**: XML (for legacy compatibility), BibTeX, EndNote, CSV, CSL, etc.
- **Content negotiation**: Via `Accept` header or `?format=json` query parameter

### 2. Versioned URLs
- **Base path**: `/api/v2/`
- **Example**: `/api/v2/posts`, `/api/v2/users/{username}`

### 3. RESTful Conventions
- **Resources, not actions**: `/api/v2/posts` (not `/api/v2/getPost`)
- **HTTP methods**: GET (read), POST (create), PUT (update), DELETE (delete)
- **HTTP status codes**:
  - `200 OK` - Success (GET, PUT, DELETE)
  - `201 Created` - Success (POST)
  - `400 Bad Request` - Validation error
  - `401 Unauthorized` - Authentication required
  - `403 Forbidden` - Access denied
  - `404 Not Found` - Resource not found
  - `500 Internal Server Error` - Server error

### 4. Consistent Patterns
- **Pagination**: `offset` and `limit` query parameters
- **Sorting**: `sortBy` and `order` (asc/desc) query parameters
- **Filtering**: Resource-specific query parameters
- **Dates**: ISO 8601 format (`YYYY-MM-DDTHH:MM:SSZ`)

### 5. DTO Decoupling
- **Never expose domain models**: All responses use DTOs
- **Explicit mapping**: Domain → DTO mapping is explicit, no auto-mapping
- **Null-safety**: Kotlin null-safety enforced

### 6. Security
- **OAuth2/JWT**: Token-based authentication
- **API keys**: Users can generate API keys for programmatic access
- **HTTPS only**: Production API requires HTTPS

### 7. Documentation
- **OpenAPI 3.0**: Complete specification in `docs/openapi.yaml`
- **Swagger UI**: Interactive documentation at `/swagger-ui.html`
- **Examples**: All endpoints include request/response examples

---

## Feature Priority Matrix

| Feature Category | Priority | Complexity | MVP Status |
|------------------|----------|------------|------------|
| Posts CRUD | **High** | Medium | ✅ Included |
| Import/Export | **High** | Medium | ✅ Included |
| Tagging | **High** | Low | ✅ Included |
| Users & Auth | **High** | Medium | ✅ Included |
| Groups | **High** | Medium | ✅ Included |
| Search | **High** | Medium | ✅ Included |
| Documents | Medium | Low | ✅ Included |
| BibTeX Key Nav | Medium | Low | ✅ Included |
| Gold Standard | Medium | Low | ✅ Included |
| Custom Layouts | Low | High | ✅ Included (deferred priority) |
| Friends/Followers | Low | Medium | ❌ Removed |
| Recommendations | Low | High | ❌ Removed |
| Clipboard | Low | Low | ❌ Removed |
| CRIS | Medium | **Very High** | ⏸️ Deferred |
| Sync Services | Low | High | ⏸️ Deferred |
| Statistics | Low | Medium | ⏸️ Deferred |
| Concepts | Medium | Medium | ⏸️ Deferred |

---

## Migration from API v1

Users of the legacy REST API v1 will need to migrate to v2:

### Breaking Changes
1. **Authentication**: OAuth2/JWT instead of HTTP Basic Auth
2. **URL structure**: `/api/v2/` instead of `/api/`
3. **Default format**: JSON instead of XML
4. **Response structure**: DTOs instead of domain models
5. **Removed endpoints**: Friends, followers, clipboard, recommendations, inbox

### Compatibility
- Legacy API v1 will remain operational alongside v2 during transition period
- Export formats (BibTeX, EndNote, RSS) maintain backward compatibility
- Database is shared between v1 and v2

### Migration Timeline
1. **Phase 1**: Deploy v2 MVP alongside v1 (both operational)
2. **Phase 2**: Migrate relevant v1 endpoints to v2 backend (compatibility layer)
3. **Phase 3**: Deprecate v1 (announce sunset date)
4. **Phase 4**: Remove v1 (redirect to v2)

---

## Success Criteria

The MVP is considered successful when:

1. ✅ **Feature parity**: All core features work as well as legacy system
2. ✅ **Performance**: API response time < 200ms for 95% of requests
3. ✅ **Reliability**: 99.9% uptime
4. ✅ **Documentation**: Complete OpenAPI spec with examples
5. ✅ **Testing**: >80% code coverage with integration tests
6. ✅ **User migration**: >50% of active users migrate to new frontend
7. ✅ **Developer adoption**: External developers use API v2 with positive feedback

---

## References

- [OpenAPI Specification](openapi.yaml) - Complete API specification
- [Removed Features](REMOVED_FEATURES.md) - Excluded features with codebase references
- [Deferred Features](DEFERRED_FEATURES.md) - Post-MVP features with implementation notes
- [CLAUDE.md](../../CLAUDE.md) - Development guidelines for the entire project
- [Modernization Strategy](../../.cursor/plans/bibsonomy_modernization_strategy_50e37204.plan.md) - Overall modernization plan
