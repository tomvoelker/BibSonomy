# Information Architecture - BibSonomy Frontend

**Version:** 1.0
**Last Updated:** 2025-12-15
**Status:** Design Specification

## Overview

This document defines the information architecture for the BibSonomy Vue 3 frontend application. It maps all pages, routes, navigation patterns, and user flows based on the REST API v2 endpoints.

---

## Table of Contents

1. [Sitemap / Page Hierarchy](#1-sitemap--page-hierarchy)
2. [Routes & URLs](#2-routes--urls)
3. [Navigation Structure](#3-navigation-structure)
4. [User Flows](#4-user-flows)
5. [Page-to-API Mapping](#5-page-to-api-mapping)
6. [State Management Strategy](#6-state-management-strategy)
7. [Internationalization (i18n)](#7-internationalization-i18n)

---

## 1. Sitemap / Page Hierarchy

### 1.1 Public Pages (No Authentication Required)

```
/ (Home)
├── /about
├── /login
├── /register
├── /search
│   └── /search?q={query}
├── /posts
│   ├── /posts (Browse all public posts)
│   ├── /posts/{postId} (Post detail)
│   └── /posts/bibtexkey/{key} (Lookup by BibTeX key)
├── /users
│   ├── /users (Browse users)
│   └── /users/{username}
│       ├── /users/{username}/posts
│       └── /users/{username}/profile
├── /groups
│   ├── /groups (Browse public groups)
│   └── /groups/{groupname}
│       └── /groups/{groupname}/posts
├── /tags
│   ├── /tags (Tag cloud)
│   ├── /tags/{tagname}
│   └── /tags/{tagname}/posts
├── /authors/{authorName} (Publications by author)
├── /popular (Popular/trending posts)
└── /community (Gold standard posts)
```

### 1.2 Authenticated Pages (Login Required)

```
/dashboard (User's home after login)
├── /my/posts (My posts)
│   ├── /my/posts/new (Create new post)
│   ├── /my/posts/{postId}/edit
│   └── /my/posts/{postId}/documents (Manage PDFs)
├── /my/groups (My groups)
│   ├── /my/groups/new (Create group)
│   └── /my/groups/{groupname}/manage
│       ├── /my/groups/{groupname}/members
│       ├── /my/groups/{groupname}/invitations
│       └── /my/groups/{groupname}/join-requests
├── /my/settings
│   ├── /my/settings/profile
│   ├── /my/settings/preferences
│   ├── /my/settings/api-keys
│   └── /my/settings/account
├── /import
│   ├── /import/bibtex
│   ├── /import/doi
│   ├── /import/url
│   ├── /import/pdf
│   ├── /import/bookmarks
│   └── /import/endnote
└── /notifications (Invitations, join requests, etc.)
```

### 1.3 Admin Pages (Admin Role Required)

```
/admin
├── /admin/users
├── /admin/groups
├── /admin/posts (Moderation)
└── /admin/system
```

---

## 2. Routes & URLs

### 2.1 Route Definitions (Vue Router)

#### Public Routes

| Path                       | Component                 | Description         | Query Parameters                                                                        | Route Guards |
| -------------------------- | ------------------------- | ------------------- | --------------------------------------------------------------------------------------- | ------------ |
| `/`                        | `HomePage.vue`            | Landing page        | -                                                                                       | -            |
| `/about`                   | `AboutPage.vue`           | About BibSonomy     | -                                                                                       | -            |
| `/login`                   | `LoginPage.vue`           | Login form          | `redirect`                                                                              | Guest only   |
| `/register`                | `RegisterPage.vue`        | Registration form   | -                                                                                       | Guest only   |
| `/search`                  | `SearchPage.vue`          | Search results      | `q`, `resourceType`, `user`, `group`, `tags`, `offset`, `limit`                         | -            |
| `/posts`                   | `PostListPage.vue`        | Browse posts        | `resourceType`, `tags`, `user`, `group`, `search`, `offset`, `limit`, `sortBy`, `order` | -            |
| `/posts/:postId`           | `PostDetailPage.vue`      | Post detail         | -                                                                                       | -            |
| `/bibtexkey/:key`          | `BibtexKeyLookupPage.vue` | BibTeX key lookup   | `user`                                                                                  | -            |
| `/users`                   | `UserListPage.vue`        | Browse users        | `search`, `offset`, `limit`                                                             | -            |
| `/users/:username`         | `UserProfilePage.vue`     | User profile        | -                                                                                       | -            |
| `/users/:username/posts`   | `UserPostsPage.vue`       | User's posts        | `resourceType`, `offset`, `limit`, `format`                                             | -            |
| `/groups`                  | `GroupListPage.vue`       | Browse groups       | `search`, `visibility`, `offset`, `limit`                                               | -            |
| `/groups/:groupname`       | `GroupDetailPage.vue`     | Group details       | -                                                                                       | -            |
| `/groups/:groupname/posts` | `GroupPostsPage.vue`      | Group posts         | `offset`, `limit`, `format`                                                             | -            |
| `/tags`                    | `TagCloudPage.vue`        | Tag cloud           | `minFreq`, `maxCount`                                                                   | -            |
| `/tags/:tagname`           | `TagDetailPage.vue`       | Tag details         | -                                                                                       | -            |
| `/tags/:tagname/posts`     | `TagPostsPage.vue`        | Posts with tag      | `offset`, `limit`, `format`                                                             | -            |
| `/authors/:authorName`     | `AuthorPage.vue`          | Author publications | `offset`, `limit`                                                                       | -            |
| `/popular`                 | `PopularPostsPage.vue`    | Popular posts       | `days`, `offset`, `limit`                                                               | -            |
| `/community`               | `CommunityPostsPage.vue`  | Gold standard posts | `offset`, `limit`, `format`                                                             | -            |

#### Authenticated Routes

| Path                                  | Component                   | Description                         | Query Parameters                  | Route Guards     |
| ------------------------------------- | --------------------------- | ----------------------------------- | --------------------------------- | ---------------- |
| `/dashboard`                          | `DashboardPage.vue`         | User dashboard                      | -                                 | Auth required    |
| `/my/posts`                           | `MyPostsPage.vue`           | My posts                            | `resourceType`, `offset`, `limit` | Auth required    |
| `/my/posts/new`                       | `CreatePostPage.vue`        | Create post                         | `type` (bookmark/bibtex)          | Auth required    |
| `/my/posts/:postId/edit`              | `EditPostPage.vue`          | Edit post                           | -                                 | Auth + owner     |
| `/my/posts/:postId/documents`         | `ManageDocumentsPage.vue`   | Manage PDFs                         | -                                 | Auth + owner     |
| `/my/groups`                          | `MyGroupsPage.vue`          | My groups                           | -                                 | Auth required    |
| `/my/groups/new`                      | `CreateGroupPage.vue`       | Create group                        | -                                 | Auth required    |
| `/my/groups/:groupname/manage`        | `ManageGroupPage.vue`       | Manage group                        | -                                 | Auth + admin     |
| `/my/groups/:groupname/members`       | `GroupMembersPage.vue`      | Manage members                      | -                                 | Auth + admin/mod |
| `/my/groups/:groupname/invitations`   | `GroupInvitationsPage.vue`  | Manage invitations                  | -                                 | Auth + admin/mod |
| `/my/groups/:groupname/join-requests` | `GroupJoinRequestsPage.vue` | Handle join requests                | -                                 | Auth + admin/mod |
| `/my/settings`                        | `SettingsPage.vue`          | User settings (redirect to profile) | -                                 | Auth required    |
| `/my/settings/profile`                | `ProfileSettingsPage.vue`   | Profile settings                    | -                                 | Auth required    |
| `/my/settings/preferences`            | `PreferencesPage.vue`       | User preferences                    | -                                 | Auth required    |
| `/my/settings/api-keys`               | `ApiKeysPage.vue`           | API key management                  | -                                 | Auth required    |
| `/my/settings/account`                | `AccountSettingsPage.vue`   | Account settings                    | -                                 | Auth required    |
| `/import`                             | `ImportPage.vue`            | Import hub                          | -                                 | Auth required    |
| `/import/bibtex`                      | `ImportBibtexPage.vue`      | Import BibTeX                       | -                                 | Auth required    |
| `/import/doi`                         | `ImportDoiPage.vue`         | Import by DOI                       | -                                 | Auth required    |
| `/import/url`                         | `ImportUrlPage.vue`         | Import from URL                     | -                                 | Auth required    |
| `/import/pdf`                         | `ImportPdfPage.vue`         | Import PDF                          | -                                 | Auth required    |
| `/import/bookmarks`                   | `ImportBookmarksPage.vue`   | Import bookmarks                    | -                                 | Auth required    |
| `/import/endnote`                     | `ImportEndnotePage.vue`     | Import EndNote                      | -                                 | Auth required    |
| `/notifications`                      | `NotificationsPage.vue`     | Notifications                       | -                                 | Auth required    |

#### Admin Routes

| Path            | Component             | Description      | Query Parameters | Route Guards |
| --------------- | --------------------- | ---------------- | ---------------- | ------------ |
| `/admin`        | `AdminDashboard.vue`  | Admin dashboard  | -                | Admin only   |
| `/admin/users`  | `AdminUsersPage.vue`  | User management  | -                | Admin only   |
| `/admin/groups` | `AdminGroupsPage.vue` | Group management | -                | Admin only   |
| `/admin/posts`  | `AdminPostsPage.vue`  | Post moderation  | -                | Admin only   |
| `/admin/system` | `AdminSystemPage.vue` | System settings  | -                | Admin only   |

### 2.2 Route Guards

**Route Guard Types:**

1. **Guest Only** (`/login`, `/register`) - Redirect to `/dashboard` if authenticated
2. **Auth Required** - Redirect to `/login?redirect={currentPath}` if not authenticated
3. **Owner Required** - Check if current user owns the resource (for edit/delete)
4. **Admin/Moderator** - Check if user has admin or moderator role in group
5. **Admin Only** - Check if user has global admin role

**Implementation Pattern:**

```typescript
// router/guards.ts
export const requireAuth = (to, from, next) => {
  const authStore = useAuthStore()
  if (!authStore.isAuthenticated) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
}

export const requireGuest = (to, from, next) => {
  const authStore = useAuthStore()
  if (authStore.isAuthenticated) {
    next('/dashboard')
  } else {
    next()
  }
}

export const requireAdmin = (to, from, next) => {
  const authStore = useAuthStore()
  if (!authStore.isAdmin) {
    next('/') // or show 403
  } else {
    next()
  }
}
```

---

## 3. Navigation Structure

### 3.1 Top Navigation (Header)

**Unauthenticated:**

```
[Logo: BibSonomy] | Posts | Users | Groups | Tags | Search [🔍] | [Login] [Register]
```

**Authenticated:**

```
[Logo: BibSonomy] | Posts | Users | Groups | Tags | Search [🔍] | [+ Create] [👤 User Menu ▾] [🔔 Notifications]
```

### 3.2 User Menu (Authenticated)

**Dropdown from user avatar/name:**

```
👤 {username}
├── Dashboard
├── My Posts
├── My Groups
├── Settings
├── Import
├── API Keys
├── ───────────
└── Logout
```

**Admin users see additional:**

```
├── ───────────
└── Admin Panel
```

### 3.3 Main Navigation Links

| Link                 | Target           | Visibility          |
| -------------------- | ---------------- | ------------------- |
| **BibSonomy** (Logo) | `/`              | Always              |
| **Posts**            | `/posts`         | Always              |
| **Users**            | `/users`         | Always              |
| **Groups**           | `/groups`        | Always              |
| **Tags**             | `/tags`          | Always              |
| **Search**           | `/search`        | Always (icon/input) |
| **Create**           | `/my/posts/new`  | Authenticated only  |
| **Login**            | `/login`         | Guests only         |
| **Register**         | `/register`      | Guests only         |
| **User Menu**        | (dropdown)       | Authenticated only  |
| **Notifications**    | `/notifications` | Authenticated only  |

### 3.4 Sidebar Navigation (Context-Dependent)

**Dashboard Page:**

```
📊 Overview
📚 My Posts
👥 My Groups
⚙️ Settings
```

**My Groups Page:**

```
+ Create Group
───────────
[List of user's groups]
```

**Group Detail Page (if member):**

```
📖 Posts
👥 Members
📊 Statistics
⚙️ Manage (admin only)
```

**Settings Page:**

```
👤 Profile
⚙️ Preferences
🔑 API Keys
🔒 Account & Security
```

### 3.5 Footer Navigation

```
About | Help | API Documentation | Terms of Service | Privacy Policy | Contact
─────────────────────────────────────────────────────────────────────────────
© 2025 BibSonomy | LGPL 3.0 License
```

### 3.6 Mobile Navigation Pattern

**Hamburger Menu (☰) containing:**

```
🏠 Home
📚 Posts
👥 Users
🏢 Groups
🏷️ Tags
🔍 Search
───────────
(If authenticated:)
+ Create Post
📊 Dashboard
⚙️ Settings
📥 Import
🔔 Notifications
───────────
👤 Login / Logout
```

**Bottom Tab Bar (Mobile):**

```
[🏠 Home] [🔍 Search] [➕ Create] [🔔] [👤 Profile]
```

---

## 4. User Flows

### 4.1 New User Registration → First Post

```
1. User visits / (Home)
2. Clicks "Register"
3. Fills registration form:
   - Username
   - Email
   - Password
   - Real name (optional)
4. Submits → POST /api/v2/users
5. Receives confirmation (email verification may be required)
6. Redirected to /login
7. Logs in → POST /api/v2/auth/token
8. Redirected to /dashboard
9. Dashboard shows "Get Started" guide
10. Clicks "Create Your First Post"
11. Redirected to /my/posts/new
12. Chooses post type (Bookmark or Publication)
13. Fills form:
    - URL or BibTeX fields
    - Tags
    - Groups (default: public)
14. Submits → POST /api/v2/posts
15. Redirected to /posts/{postId} (success message)
```

### 4.2 Bookmarking a Webpage

```
1. User is on /my/posts/new (or uses browser extension)
2. Selects "Bookmark" type
3. Enters URL
4. Clicks "Fetch Metadata" (optional)
   → POST /api/v2/import/url (preview)
5. Reviews auto-filled title/description
6. Adds tags (with autocomplete)
   → GET /api/v2/tags?search={input}
7. Selects groups to share with
   → GET /api/v2/groups (user's groups)
8. Adds personal description
9. Submits → POST /api/v2/posts
10. Success: Redirected to /posts/{postId}
```

### 4.3 Importing BibTeX Publications

```
1. User navigates to /import/bibtex
2. Chooses input method:
   a) Upload .bib file, OR
   b) Paste BibTeX text
3. Optionally adds default tags for all entries
4. Optionally selects groups for all entries
5. Submits → POST /api/v2/import/bibtex
6. System parses BibTeX:
   - Shows preview of entries to be imported
   - Highlights errors/warnings
7. User confirms import
8. Posts created → Response with imported posts
9. Success page shows:
   - Count of successful imports
   - List of imported publications (with links)
   - Errors (if any)
10. User can navigate to /my/posts to view
```

### 4.4 Creating and Managing Groups

**Creating a Group:**

```
1. User navigates to /my/groups
2. Clicks "Create Group"
3. Redirected to /my/groups/new
4. Fills form:
   - Group name (unique, lowercase-hyphenated)
   - Display name
   - Description
   - Visibility (public/private/viewable)
   - Parent group (optional, for hierarchy)
5. Submits → POST /api/v2/groups
6. Group created
7. Redirected to /groups/{groupname}
```

**Inviting Members:**

```
1. User navigates to /my/groups/{groupname}/manage
2. Clicks "Invite Members" tab
3. Searches for users → GET /api/v2/users?search={query}
4. Selects user, assigns role (admin/moderator/member)
5. Adds optional invitation message
6. Submits → POST /api/v2/groups/{groupname}/invitations
7. Invited user receives notification
8. Invited user can accept/reject from /notifications
```

**Handling Join Requests:**

```
1. User requests to join group (public/viewable groups)
   → POST /api/v2/groups/{groupname}/join-requests
2. Group admin/moderator receives notification
3. Admin navigates to /my/groups/{groupname}/join-requests
4. Reviews pending requests:
   - Username
   - Optional message from user
5. Approves or rejects:
   - Approve → PUT /api/v2/groups/{groupname}/join-requests/{username}
     (action: approve, role: member)
   - Reject → Same endpoint (action: reject)
6. User receives notification of approval/rejection
```

### 4.5 Searching and Filtering Posts

**Simple Search:**

```
1. User types query in header search box
2. Presses Enter
3. Redirected to /search?q={query}
4. Results displayed (paginated)
   → GET /api/v2/search?q={query}
5. User can refine with filters:
   - Resource type (bookmarks/publications)
   - Tags
   - User
   - Group
6. Filters update URL query params
7. Results update (client-side or new request)
```

**Tag-Based Browsing:**

```
1. User navigates to /tags (tag cloud)
   → GET /api/v2/tags?maxCount=100
2. Clicks on tag (e.g., "machine-learning")
3. Redirected to /tags/machine-learning/posts
   → GET /api/v2/tags/machine-learning/posts
4. Posts with that tag displayed
5. Sidebar shows:
   - Related tags → GET /api/v2/tags/machine-learning/related
   - Tag statistics
6. User can click related tags to refine search
```

**Advanced Filtering:**

```
1. User on /posts page
2. Uses filter sidebar:
   - Resource type: Bookmark/Publication/All
   - Tags: Multi-select with autocomplete
   - User: Search username
   - Group: Select from user's groups
   - Date range (if implemented)
3. Each filter updates URL query params
4. Posts list re-fetches:
   → GET /api/v2/posts?resourceType={}&tags={}&user={}&group={}
5. Results update with pagination
```

### 4.6 Managing User Settings

```
1. User navigates to /my/settings
2. Redirected to /my/settings/profile (default tab)
3. Tabs available:
   a) Profile
      - Real name, biography, institution, homepage
      → PUT /api/v2/users/{username}
   b) Preferences
      - Language (en/de)
      - Items per page
      - Tag cloud settings
      - Default groups for new posts
      → PUT /api/v2/users/{username}/settings
   c) API Keys
      - List existing keys → GET /api/v2/users/{username}/api-keys
      - Create new key → POST /api/v2/users/{username}/api-keys
      - Revoke key (delete)
   d) Account & Security
      - Change password
      - Delete account → DELETE /api/v2/users/{username}
4. Changes saved per-section (not global save button)
5. Success/error messages displayed per section
```

---

## 5. Page-to-API Mapping

### 5.1 Core Post Pages

**Home Page (`/`)**

```
HomePage.vue
├── GET /api/v2/posts?limit=10 (Recent posts)
├── GET /api/v2/search/popular?days=7&limit=10 (Popular posts)
├── GET /api/v2/tags?maxCount=30 (Tag cloud)
└── GET /api/v2/community?limit=5 (Featured community posts)
```

**Post List Page (`/posts`)**

```
PostListPage.vue
├── GET /api/v2/posts?offset={offset}&limit={limit}&resourceType={type}&tags={tags}&user={user}&group={group}&search={q}&sortBy={sortBy}&order={order}
├── GET /api/v2/tags?maxCount=50 (Sidebar tag cloud)
└── DELETE /api/v2/posts/{postId} (Delete action, if owner)
```

**Post Detail Page (`/posts/:postId`)**

```
PostDetailPage.vue
├── GET /api/v2/posts/{postId}
├── GET /api/v2/posts/{postId}/documents (If publication)
├── GET /api/v2/tags/{tagname}/related (Related tags, for each tag)
├── PUT /api/v2/posts/{postId} (Edit action, if owner)
└── DELETE /api/v2/posts/{postId} (Delete action, if owner)
```

**Create Post Page (`/my/posts/new`)**

```
CreatePostPage.vue
├── POST /api/v2/posts (Create bookmark or publication)
├── POST /api/v2/import/url (Fetch metadata for bookmark)
├── GET /api/v2/tags?search={input} (Tag autocomplete)
├── GET /api/v2/groups (User's groups for selection)
└── POST /api/v2/posts/{postId}/documents (Upload PDF after creation)
```

**Edit Post Page (`/my/posts/:postId/edit`)**

```
EditPostPage.vue
├── GET /api/v2/posts/{postId} (Load current data)
├── PUT /api/v2/posts/{postId} (Update)
├── GET /api/v2/tags?search={input} (Tag autocomplete)
└── GET /api/v2/groups (User's groups)
```

### 5.2 User Pages

**User List Page (`/users`)**

```
UserListPage.vue
└── GET /api/v2/users?search={query}&offset={offset}&limit={limit}
```

**User Profile Page (`/users/:username`)**

```
UserProfilePage.vue
├── GET /api/v2/users/{username}
├── GET /api/v2/users/{username}/posts?limit=10 (Recent posts preview)
└── GET /api/v2/tags?user={username}&maxCount=20 (User's top tags, if available)
```

**User Posts Page (`/users/:username/posts`)**

```
UserPostsPage.vue
└── GET /api/v2/users/{username}/posts?resourceType={type}&offset={offset}&limit={limit}&format={format}
```

**My Posts Page (`/my/posts`)**

```
MyPostsPage.vue
├── GET /api/v2/users/{currentUsername}/posts?offset={offset}&limit={limit}&resourceType={type}
└── DELETE /api/v2/posts/{postId} (Bulk delete or individual)
```

### 5.3 Group Pages

**Group List Page (`/groups`)**

```
GroupListPage.vue
└── GET /api/v2/groups?search={query}&visibility={visibility}&offset={offset}&limit={limit}
```

**Group Detail Page (`/groups/:groupname`)**

```
GroupDetailPage.vue
├── GET /api/v2/groups/{groupname}
├── GET /api/v2/groups/{groupname}/members (Member list)
├── GET /api/v2/groups/{groupname}/posts?limit=10 (Preview)
└── POST /api/v2/groups/{groupname}/join-requests (If public/viewable)
```

**Group Posts Page (`/groups/:groupname/posts`)**

```
GroupPostsPage.vue
└── GET /api/v2/groups/{groupname}/posts?offset={offset}&limit={limit}&format={format}
```

**My Groups Page (`/my/groups`)**

```
MyGroupsPage.vue
└── GET /api/v2/groups?user={currentUsername} (Not in API spec - may need to fetch all groups and filter client-side, or use /api/v2/users/{username}/groups if available)
```

**Create Group Page (`/my/groups/new`)**

```
CreateGroupPage.vue
├── POST /api/v2/groups
└── GET /api/v2/groups (For parent group selection)
```

**Manage Group Page (`/my/groups/:groupname/manage`)**

```
ManageGroupPage.vue
├── GET /api/v2/groups/{groupname}
├── PUT /api/v2/groups/{groupname} (Update settings)
└── DELETE /api/v2/groups/{groupname} (Delete group)
```

**Group Members Page (`/my/groups/:groupname/members`)**

```
GroupMembersPage.vue
├── GET /api/v2/groups/{groupname}/members
├── POST /api/v2/groups/{groupname}/members (Add member)
├── PUT /api/v2/groups/{groupname}/members/{username} (Update role)
└── DELETE /api/v2/groups/{groupname}/members/{username} (Remove member)
```

**Group Invitations Page (`/my/groups/:groupname/invitations`)**

```
GroupInvitationsPage.vue
├── GET /api/v2/groups/{groupname}/invitations
├── POST /api/v2/groups/{groupname}/invitations (Send invitation)
└── DELETE /api/v2/groups/{groupname}/invitations/{username} (Cancel invitation)
```

**Group Join Requests Page (`/my/groups/:groupname/join-requests`)**

```
GroupJoinRequestsPage.vue
├── GET /api/v2/groups/{groupname}/join-requests
├── PUT /api/v2/groups/{groupname}/join-requests/{username} (Approve/reject)
└── DELETE /api/v2/groups/{groupname}/join-requests/{username} (User withdraws own request)
```

### 5.4 Tag Pages

**Tag Cloud Page (`/tags`)**

```
TagCloudPage.vue
└── GET /api/v2/tags?minFreq={minFreq}&maxCount={maxCount}
```

**Tag Detail Page (`/tags/:tagname`)**

```
TagDetailPage.vue
├── GET /api/v2/tags/{tagname}
└── GET /api/v2/tags/{tagname}/related?limit=20
```

**Tag Posts Page (`/tags/:tagname/posts`)**

```
TagPostsPage.vue
└── GET /api/v2/tags/{tagname}/posts?offset={offset}&limit={limit}&format={format}
```

### 5.5 Search & Discovery Pages

**Search Page (`/search`)**

```
SearchPage.vue
├── GET /api/v2/search?q={query}&resourceType={type}&user={user}&group={group}&tags={tags}&offset={offset}&limit={limit}&format={format}
└── GET /api/v2/tags?search={input} (Tag filter autocomplete)
```

**Popular Posts Page (`/popular`)**

```
PopularPostsPage.vue
└── GET /api/v2/search/popular?days={days}&offset={offset}&limit={limit}
```

**Author Page (`/authors/:authorName`)**

```
AuthorPage.vue
└── GET /api/v2/authors/{authorName}?offset={offset}&limit={limit}
```

**BibTeX Key Lookup Page (`/bibtexkey/:key`)**

```
BibtexKeyLookupPage.vue
└── GET /api/v2/bibtexkey/{key}?user={username}
```

### 5.6 Community Pages

**Community Posts Page (`/community`)**

```
CommunityPostsPage.vue
├── GET /api/v2/community?offset={offset}&limit={limit}&format={format}
└── POST /api/v2/community (Create community post, if authorized)
```

**Community Post Detail (uses PostDetailPage with additions)**

```
PostDetailPage.vue (for community posts)
├── GET /api/v2/community/{postId}
├── GET /api/v2/community/{postId}/references
├── GET /api/v2/community/{postId}/part-of
├── POST /api/v2/community/{postId}/references (Add reference relation)
├── POST /api/v2/community/{postId}/part-of (Add part-of relation)
├── DELETE /api/v2/community/{postId}/references (Remove reference)
└── DELETE /api/v2/community/{postId}/part-of (Remove part-of)
```

### 5.7 Import Pages

**Import Hub Page (`/import`)**

```
ImportPage.vue
(No API calls - just navigation hub to specific import pages)
```

**Import BibTeX Page (`/import/bibtex`)**

```
ImportBibtexPage.vue
├── POST /api/v2/import/bibtex (Upload file or text)
├── GET /api/v2/tags?search={input} (Default tags autocomplete)
└── GET /api/v2/groups (Default groups selection)
```

**Import by DOI Page (`/import/doi`)**

```
ImportDoiPage.vue
├── POST /api/v2/import/doi
├── GET /api/v2/tags?search={input}
└── GET /api/v2/groups
```

**Import from URL Page (`/import/url`)**

```
ImportUrlPage.vue
├── POST /api/v2/import/url
├── GET /api/v2/tags?search={input}
└── GET /api/v2/groups
```

**Import PDF Page (`/import/pdf`)**

```
ImportPdfPage.vue
├── POST /api/v2/import/pdf (Upload and extract metadata)
├── GET /api/v2/tags?search={input}
└── GET /api/v2/groups
```

**Import Bookmarks Page (`/import/bookmarks`)**

```
ImportBookmarksPage.vue
├── POST /api/v2/import/bookmarks (Browser bookmark file)
├── GET /api/v2/tags?search={input}
└── GET /api/v2/groups
```

**Import EndNote Page (`/import/endnote`)**

```
ImportEndnotePage.vue
├── POST /api/v2/import/endnote
├── GET /api/v2/tags?search={input}
└── GET /api/v2/groups
```

### 5.8 Settings Pages

**Profile Settings Page (`/my/settings/profile`)**

```
ProfileSettingsPage.vue
├── GET /api/v2/users/{username}
└── PUT /api/v2/users/{username}
```

**Preferences Page (`/my/settings/preferences`)**

```
PreferencesPage.vue
├── GET /api/v2/users/{username}/settings
└── PUT /api/v2/users/{username}/settings
```

**API Keys Page (`/my/settings/api-keys`)**

```
ApiKeysPage.vue
├── GET /api/v2/users/{username}/api-keys
├── POST /api/v2/users/{username}/api-keys (Create new key)
└── DELETE /api/v2/users/{username}/api-keys/{keyId} (Revoke key - endpoint not in spec, may need to use different pattern)
```

**Account Settings Page (`/my/settings/account`)**

```
AccountSettingsPage.vue
├── PUT /api/v2/users/{username} (Change password - may need dedicated endpoint)
└── DELETE /api/v2/users/{username} (Delete account)
```

### 5.9 Authentication Pages

**Login Page (`/login`)**

```
LoginPage.vue
├── POST /api/v2/auth/token (grant_type: password)
└── Redirect to ?redirect param or /dashboard on success
```

**Register Page (`/register`)**

```
RegisterPage.vue
└── POST /api/v2/users (security: [] - public endpoint)
```

**Notifications Page (`/notifications`)**

```
NotificationsPage.vue
├── GET /api/v2/groups/{groupname}/invitations?user={currentUsername} (Invitations received)
├── PUT /api/v2/groups/{groupname}/invitations/{username} (Accept/reject)
└── (May need additional endpoints for notification aggregation not in current API spec)
```

### 5.10 Document Management

**Manage Documents Page (`/my/posts/:postId/documents`)**

```
ManageDocumentsPage.vue
├── GET /api/v2/posts/{postId}/documents
├── POST /api/v2/posts/{postId}/documents (Upload PDF)
├── GET /api/v2/posts/{postId}/documents/{filename} (Download)
└── DELETE /api/v2/posts/{postId}/documents/{filename}
```

### 5.11 Admin Pages

**Admin Dashboard (`/admin`)**

```
AdminDashboard.vue
├── GET /api/v2/users?limit=10&sortBy=registeredAt&order=desc (Recent users)
├── GET /api/v2/posts?limit=10&sortBy=date&order=desc (Recent posts)
├── GET /api/v2/groups?limit=10 (Recent groups)
└── (System statistics - may need dedicated admin endpoints)
```

**Admin Users Page (`/admin/users`)**

```
AdminUsersPage.vue
├── GET /api/v2/users?offset={offset}&limit={limit}&search={query}
├── DELETE /api/v2/users/{username} (Delete user account)
└── PUT /api/v2/users/{username} (Modify user - may need admin-specific fields)
```

**Admin Groups Page (`/admin/groups`)**

```
AdminGroupsPage.vue
├── GET /api/v2/groups?offset={offset}&limit={limit}&search={query}
└── DELETE /api/v2/groups/{groupname}
```

**Admin Posts Page (`/admin/posts`)**

```
AdminPostsPage.vue
├── GET /api/v2/posts?offset={offset}&limit={limit}
└── DELETE /api/v2/posts/{postId} (Moderate/remove posts)
```

---

## 6. State Management Strategy

### 6.1 State Management Architecture

**Principle:** Use the right tool for the right state type.

```
┌─────────────────────────────────────────────────────────────┐
│                    Application State                         │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────┐  ┌──────────────────┐  ┌────────────┐│
│  │   vue-query      │  │   Pinia Stores   │  │ Component  ││
│  │ (Server State)   │  │ (Client State)   │  │ Local State││
│  └──────────────────┘  └──────────────────┘  └────────────┘│
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

### 6.2 @tanstack/vue-query (Server State)

**Use for:**

- All API data fetching
- Caching, deduplication, background refetching
- Pagination and infinite scroll
- Optimistic updates

**Examples:**

```typescript
// composables/usePosts.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'

export function usePosts(params: PostQueryParams) {
  return useQuery({
    queryKey: ['posts', params],
    queryFn: async () => {
      const response = await api.get('/api/v2/posts', { params })
      return response.data
    },
    staleTime: 5 * 60 * 1000, // 5 minutes
  })
}

export function useCreatePost() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (postData: PostCreateRequest) => api.post('/api/v2/posts', postData),
    onSuccess: () => {
      // Invalidate posts queries to refetch
      queryClient.invalidateQueries({ queryKey: ['posts'] })
    },
  })
}
```

**Query Keys Convention:**

```typescript
// Query key structure: [resource, filters?, id?]
;['posts'][('posts', { user: 'jsmith' })][('posts', 'detail', 123)]['users'][ // All posts // User's posts // Single post by ID // All users
  ('users', 'detail', 'jsmith')
][('groups', { visibility: 'public' })][('tags', { search: 'machine' })] // Single user // Filtered groups // Tag autocomplete
```

### 6.3 Pinia Stores (Client State)

**Use for:**

- Authentication state (token, current user)
- User preferences (language, theme, items per page)
- UI state (sidebar open/closed, active filters)
- Global client-side state that multiple components need

**Store Structure:**

```typescript
// stores/auth.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('auth_token'))
  const user = ref<UserDto | null>(null)

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'admin')

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('auth_token', newToken)
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('auth_token')
  }

  return { token, user, isAuthenticated, isAdmin, setToken, logout }
})
```

```typescript
// stores/preferences.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const usePreferencesStore = defineStore(
  'preferences',
  () => {
    const language = ref<'en' | 'de'>('de')
    const itemsPerPage = ref(20)
    const theme = ref<'light' | 'dark'>('light')

    function setLanguage(lang: 'en' | 'de') {
      language.value = lang
      localStorage.setItem('user_language', lang)
    }

    function loadFromSettings(settings: UserSettingsDto) {
      language.value = settings.language
      itemsPerPage.value = settings.itemsPerPage
    }

    return { language, itemsPerPage, theme, setLanguage, loadFromSettings }
  },
  {
    persist: true, // Use pinia-plugin-persistedstate for localStorage persistence
  }
)
```

```typescript
// stores/ui.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUiStore = defineStore('ui', () => {
  const sidebarOpen = ref(true)
  const activeFilters = ref<PostFilters>({})
  const mobileMenuOpen = ref(false)

  function toggleSidebar() {
    sidebarOpen.value = !sidebarOpen.value
  }

  function setFilters(filters: PostFilters) {
    activeFilters.value = filters
  }

  return { sidebarOpen, activeFilters, mobileMenuOpen, toggleSidebar, setFilters }
})
```

### 6.4 Component Local State

**Use for:**

- Form input values (before submission)
- Modal open/closed state
- Accordion expanded/collapsed
- Tooltip visibility
- Local UI interactions that don't affect other components

**Examples:**

```vue
<script setup lang="ts">
import { ref } from 'vue'

// Local form state
const searchQuery = ref('')
const filterOpen = ref(false)
const selectedTags = ref<string[]>([])

// Local UI state
const tooltipVisible = ref(false)
const dropdownOpen = ref(false)
</script>
```

### 6.5 State Management Decision Tree

```
Is this data from the API?
├── YES → Use @tanstack/vue-query
│   └── Define composable in composables/useApi.ts or specific file
│
└── NO → Is this state needed across multiple unrelated components?
    ├── YES → Use Pinia store
    │   ├── Authentication/user data → auth.ts
    │   ├── User preferences → preferences.ts
    │   └── Global UI state → ui.ts
    │
    └── NO → Use component local state (ref/reactive)
```

### 6.6 Data Flow Patterns

**Reading Server Data:**

```vue
<script setup lang="ts">
import { usePosts } from '@/composables/usePosts'
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const params = computed(() => ({
  offset: parseInt(route.query.offset as string) || 0,
  limit: 20,
  resourceType: (route.query.resourceType as string) || 'all',
}))

const { data: posts, isLoading, error } = usePosts(params)
</script>

<template>
  <div>
    <LoadingSpinner v-if="isLoading" />
    <ErrorAlert v-else-if="error" :error="error" />
    <PostList v-else :posts="posts?.items" />
  </div>
</template>
```

**Mutating Server Data:**

```vue
<script setup lang="ts">
import { useCreatePost } from '@/composables/usePosts'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const { mutate: createPost, isPending, error } = useCreatePost()

const formData = ref<PostCreateRequest>({
  resource: { resourceType: 'bookmark', url: '' },
  tags: [],
  groups: ['public'],
  visibility: 'public',
})

async function handleSubmit() {
  createPost(formData.value, {
    onSuccess: (newPost) => {
      router.push(`/posts/${newPost.id}`)
    },
  })
}
</script>
```

**Accessing Client State:**

```vue
<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { usePreferencesStore } from '@/stores/preferences'

const authStore = useAuthStore()
const prefsStore = usePreferencesStore()
</script>

<template>
  <header>
    <UserMenu v-if="authStore.isAuthenticated" :user="authStore.user" />
    <LoginButton v-else />
    <LanguageSwitcher :current="prefsStore.language" @change="prefsStore.setLanguage" />
  </header>
</template>
```

### 6.7 Cache Invalidation Strategy

**@tanstack/vue-query Patterns:**

```typescript
// After creating a post
onSuccess: () => {
  queryClient.invalidateQueries({ queryKey: ['posts'] }) // Invalidate all post queries
  queryClient.invalidateQueries({ queryKey: ['users', 'detail', currentUsername] }) // Invalidate current user
}

// After updating a post
onSuccess: (updatedPost) => {
  queryClient.setQueryData(['posts', 'detail', updatedPost.id], updatedPost) // Optimistic update
  queryClient.invalidateQueries({ queryKey: ['posts'] }) // Refetch lists
}

// After deleting a post
onSuccess: (_, deletedPostId) => {
  queryClient.removeQueries({ queryKey: ['posts', 'detail', deletedPostId] })
  queryClient.invalidateQueries({ queryKey: ['posts'] })
}

// Optimistic updates for better UX
const { mutate: updatePost } = useMutation({
  mutationFn: (data) => api.put(`/api/v2/posts/${data.id}`, data),
  onMutate: async (newData) => {
    await queryClient.cancelQueries({ queryKey: ['posts', 'detail', newData.id] })
    const previous = queryClient.getQueryData(['posts', 'detail', newData.id])
    queryClient.setQueryData(['posts', 'detail', newData.id], newData)
    return { previous }
  },
  onError: (err, newData, context) => {
    queryClient.setQueryData(['posts', 'detail', newData.id], context.previous)
  },
})
```

---

## 7. Internationalization (i18n)

### 7.1 Supported Languages

- **German (de)** - Default language
- **English (en)** - Secondary language

### 7.2 Implementation

**Library:** `vue-i18n`

**Configuration:**

```typescript
// src/plugins/i18n.ts
import { createI18n } from 'vue-i18n'
import de from '@/locales/de.json'
import en from '@/locales/en.json'

export const i18n = createI18n({
  legacy: false, // Use Composition API mode
  locale: 'de', // Default German
  fallbackLocale: 'en',
  messages: { de, en },
})
```

### 7.3 Translation File Structure

```
src/locales/
├── de.json
└── en.json
```

**Example: `src/locales/en.json`**

```json
{
  "nav": {
    "home": "Home",
    "posts": "Posts",
    "users": "Users",
    "groups": "Groups",
    "tags": "Tags",
    "search": "Search",
    "login": "Login",
    "register": "Register",
    "logout": "Logout",
    "dashboard": "Dashboard",
    "settings": "Settings",
    "create": "Create Post"
  },
  "post": {
    "title": "Title",
    "description": "Description",
    "tags": "Tags",
    "groups": "Groups",
    "created": "Created",
    "updated": "Updated",
    "visibility": "Visibility",
    "public": "Public",
    "private": "Private",
    "groupsOnly": "Groups Only",
    "bookmark": "Bookmark",
    "publication": "Publication",
    "createBookmark": "Create Bookmark",
    "createPublication": "Create Publication",
    "editPost": "Edit Post",
    "deletePost": "Delete Post",
    "confirmDelete": "Are you sure you want to delete this post?"
  },
  "user": {
    "username": "Username",
    "email": "Email",
    "password": "Password",
    "realName": "Real Name",
    "profile": "Profile",
    "biography": "Biography",
    "institution": "Institution",
    "homepage": "Homepage",
    "registeredAt": "Registered"
  },
  "group": {
    "name": "Group Name",
    "displayName": "Display Name",
    "description": "Description",
    "visibility": "Visibility",
    "members": "Members",
    "posts": "Posts",
    "createGroup": "Create Group",
    "joinGroup": "Join Group",
    "leaveGroup": "Leave Group",
    "inviteMembers": "Invite Members",
    "manageGroup": "Manage Group"
  },
  "tag": {
    "name": "Tag",
    "tags": "Tags",
    "tagCloud": "Tag Cloud",
    "relatedTags": "Related Tags",
    "addTag": "Add Tag"
  },
  "search": {
    "search": "Search",
    "searchPlaceholder": "Search posts, users, groups...",
    "results": "Results",
    "noResults": "No results found",
    "filters": "Filters",
    "resourceType": "Type",
    "allTypes": "All Types",
    "bookmarks": "Bookmarks",
    "publications": "Publications"
  },
  "import": {
    "import": "Import",
    "importBibtex": "Import BibTeX",
    "importDoi": "Import by DOI",
    "importUrl": "Import from URL",
    "importPdf": "Import PDF",
    "importBookmarks": "Import Bookmarks",
    "importEndnote": "Import EndNote",
    "uploadFile": "Upload File",
    "pasteText": "Paste Text",
    "defaultTags": "Default Tags",
    "defaultGroups": "Default Groups"
  },
  "settings": {
    "settings": "Settings",
    "profile": "Profile",
    "preferences": "Preferences",
    "apiKeys": "API Keys",
    "account": "Account & Security",
    "language": "Language",
    "itemsPerPage": "Items per Page",
    "changePassword": "Change Password",
    "deleteAccount": "Delete Account"
  },
  "common": {
    "save": "Save",
    "cancel": "Cancel",
    "delete": "Delete",
    "edit": "Edit",
    "create": "Create",
    "update": "Update",
    "submit": "Submit",
    "loading": "Loading...",
    "error": "Error",
    "success": "Success",
    "confirm": "Confirm",
    "back": "Back",
    "next": "Next",
    "previous": "Previous",
    "page": "Page",
    "of": "of",
    "showing": "Showing",
    "results": "results"
  },
  "errors": {
    "required": "This field is required",
    "invalidEmail": "Invalid email address",
    "invalidUrl": "Invalid URL",
    "passwordTooShort": "Password must be at least 8 characters",
    "networkError": "Network error. Please try again.",
    "unauthorized": "You are not authorized to perform this action",
    "notFound": "Resource not found",
    "serverError": "Server error. Please try again later."
  }
}
```

**Example: `src/locales/de.json`**

```json
{
  "nav": {
    "home": "Startseite",
    "posts": "Beiträge",
    "users": "Benutzer",
    "groups": "Gruppen",
    "tags": "Tags",
    "search": "Suche",
    "login": "Anmelden",
    "register": "Registrieren",
    "logout": "Abmelden",
    "dashboard": "Dashboard",
    "settings": "Einstellungen",
    "create": "Beitrag erstellen"
  },
  "post": {
    "title": "Titel",
    "description": "Beschreibung",
    "tags": "Tags",
    "groups": "Gruppen",
    "created": "Erstellt",
    "updated": "Aktualisiert",
    "visibility": "Sichtbarkeit",
    "public": "Öffentlich",
    "private": "Privat",
    "groupsOnly": "Nur Gruppen",
    "bookmark": "Lesezeichen",
    "publication": "Publikation",
    "createBookmark": "Lesezeichen erstellen",
    "createPublication": "Publikation erstellen",
    "editPost": "Beitrag bearbeiten",
    "deletePost": "Beitrag löschen",
    "confirmDelete": "Möchten Sie diesen Beitrag wirklich löschen?"
  },
  "user": {
    "username": "Benutzername",
    "email": "E-Mail",
    "password": "Passwort",
    "realName": "Echter Name",
    "profile": "Profil",
    "biography": "Biografie",
    "institution": "Institution",
    "homepage": "Homepage",
    "registeredAt": "Registriert"
  },
  "group": {
    "name": "Gruppenname",
    "displayName": "Anzeigename",
    "description": "Beschreibung",
    "visibility": "Sichtbarkeit",
    "members": "Mitglieder",
    "posts": "Beiträge",
    "createGroup": "Gruppe erstellen",
    "joinGroup": "Gruppe beitreten",
    "leaveGroup": "Gruppe verlassen",
    "inviteMembers": "Mitglieder einladen",
    "manageGroup": "Gruppe verwalten"
  },
  "tag": {
    "name": "Tag",
    "tags": "Tags",
    "tagCloud": "Tag-Wolke",
    "relatedTags": "Verwandte Tags",
    "addTag": "Tag hinzufügen"
  },
  "search": {
    "search": "Suche",
    "searchPlaceholder": "Beiträge, Benutzer, Gruppen suchen...",
    "results": "Ergebnisse",
    "noResults": "Keine Ergebnisse gefunden",
    "filters": "Filter",
    "resourceType": "Typ",
    "allTypes": "Alle Typen",
    "bookmarks": "Lesezeichen",
    "publications": "Publikationen"
  },
  "import": {
    "import": "Import",
    "importBibtex": "BibTeX importieren",
    "importDoi": "Nach DOI importieren",
    "importUrl": "Von URL importieren",
    "importPdf": "PDF importieren",
    "importBookmarks": "Lesezeichen importieren",
    "importEndnote": "EndNote importieren",
    "uploadFile": "Datei hochladen",
    "pasteText": "Text einfügen",
    "defaultTags": "Standard-Tags",
    "defaultGroups": "Standard-Gruppen"
  },
  "settings": {
    "settings": "Einstellungen",
    "profile": "Profil",
    "preferences": "Präferenzen",
    "apiKeys": "API-Schlüssel",
    "account": "Konto & Sicherheit",
    "language": "Sprache",
    "itemsPerPage": "Einträge pro Seite",
    "changePassword": "Passwort ändern",
    "deleteAccount": "Konto löschen"
  },
  "common": {
    "save": "Speichern",
    "cancel": "Abbrechen",
    "delete": "Löschen",
    "edit": "Bearbeiten",
    "create": "Erstellen",
    "update": "Aktualisieren",
    "submit": "Absenden",
    "loading": "Laden...",
    "error": "Fehler",
    "success": "Erfolg",
    "confirm": "Bestätigen",
    "back": "Zurück",
    "next": "Weiter",
    "previous": "Zurück",
    "page": "Seite",
    "of": "von",
    "showing": "Zeige",
    "results": "Ergebnisse"
  },
  "errors": {
    "required": "Dieses Feld ist erforderlich",
    "invalidEmail": "Ungültige E-Mail-Adresse",
    "invalidUrl": "Ungültige URL",
    "passwordTooShort": "Passwort muss mindestens 8 Zeichen lang sein",
    "networkError": "Netzwerkfehler. Bitte versuchen Sie es erneut.",
    "unauthorized": "Sie sind nicht berechtigt, diese Aktion auszuführen",
    "notFound": "Ressource nicht gefunden",
    "serverError": "Serverfehler. Bitte versuchen Sie es später erneut."
  }
}
```

### 7.4 Usage in Components

**Template Usage:**

```vue
<template>
  <div>
    <h1>{{ t('nav.home') }}</h1>
    <button>{{ t('post.createBookmark') }}</button>
    <p>{{ t('common.showing') }} {{ count }} {{ t('common.results') }}</p>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()
</script>
```

**Pluralization:**

```json
{
  "post": {
    "count": "no posts | 1 post | {count} posts"
  }
}
```

```vue
<template>
  <p>{{ t('post.count', count) }}</p>
</template>
```

**Interpolation:**

```json
{
  "user": {
    "welcome": "Welcome back, {username}!"
  }
}
```

```vue
<template>
  <h1>{{ t('user.welcome', { username: currentUser.username }) }}</h1>
</template>
```

### 7.5 Language Switcher Component

**Location:** Header navigation (always visible)

**Implementation:**

```vue
<!-- components/LanguageSwitcher.vue -->
<template>
  <div class="language-switcher">
    <button
      v-for="lang in availableLocales"
      :key="lang"
      :class="{ active: locale === lang }"
      @click="switchLanguage(lang)"
    >
      {{ lang.toUpperCase() }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { usePreferencesStore } from '@/stores/preferences'

const { locale, availableLocales } = useI18n()
const prefsStore = usePreferencesStore()

function switchLanguage(newLocale: string) {
  locale.value = newLocale
  prefsStore.setLanguage(newLocale as 'en' | 'de')
  // Optionally persist to backend if user is authenticated
  // await api.put('/api/v2/users/{username}/settings', { language: newLocale })
}
</script>
```

### 7.6 Translation Key Naming Convention

**Pattern:** `{category}.{key}`

**Categories:**

- `nav` - Navigation labels
- `post` - Post-related terms
- `user` - User-related terms
- `group` - Group-related terms
- `tag` - Tag-related terms
- `search` - Search UI
- `import` - Import workflows
- `settings` - Settings pages
- `common` - Common UI elements (buttons, labels)
- `errors` - Error messages
- `validation` - Form validation messages

### 7.7 Loading Translations from API

**If user settings stored in backend:**

```typescript
// On login or app initialization
const { data: userSettings } = await useQuery({
  queryKey: ['settings', username],
  queryFn: () => api.get(`/api/v2/users/${username}/settings`),
})

if (userSettings?.language) {
  locale.value = userSettings.language
  prefsStore.setLanguage(userSettings.language)
}
```

---

## Appendix: Missing API Endpoints

### Identified Gaps (May Need Backend Implementation)

1. **User's Groups Endpoint:**
   - Current: Must fetch all groups and filter client-side
   - Needed: `GET /api/v2/users/{username}/groups`

2. **Notifications Aggregation:**
   - Current: Must query multiple endpoints (invitations, join requests, etc.)
   - Needed: `GET /api/v2/users/{username}/notifications` (aggregated)

3. **API Key Deletion:**
   - Current: `DELETE /api/v2/users/{username}/api-keys/{keyId}` not in spec
   - Needed: Endpoint to revoke API keys

4. **Change Password:**
   - Current: Generic `PUT /api/v2/users/{username}` may handle this
   - Recommended: Dedicated `PUT /api/v2/users/{username}/password` for security

5. **Tag Search/Autocomplete:**
   - Current: `GET /api/v2/tags` with `search` parameter (inferred, not explicit)
   - Confirm: Parameter exists for tag autocomplete

6. **User's Tags:**
   - Current: No endpoint for user-specific tag cloud
   - Needed: `GET /api/v2/users/{username}/tags` (optional)

7. **Batch Operations:**
   - Current: Delete one post at a time
   - Potential: `DELETE /api/v2/posts` with `ids` parameter for bulk delete

---

## Document Revision History

| Version | Date       | Author         | Changes                                       |
| ------- | ---------- | -------------- | --------------------------------------------- |
| 1.0     | 2025-12-15 | BibSonomy Team | Initial IA document based on REST API v2 spec |

---

**End of Information Architecture Document**
