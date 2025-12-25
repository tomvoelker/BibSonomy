# BibSonomy Frontend - Page Specifications

This directory contains comprehensive specifications for all major pages in the BibSonomy Vue 3 frontend application.

## Overview

Each page specification includes:

- Route and access control
- Page purpose and layout structure
- Component breakdown
- API endpoint calls
- State management (vue-query, Pinia, component state)
- User interactions
- URL parameters and query strings
- Page states (loading, empty, error, success)
- Responsive behavior
- Accessibility requirements
- i18n translation keys
- Mockup notes and visual design guidelines

## Page Categories

### Public Discovery Pages

1. **[HomePage.md](./HomePage.md)** - `/`
   - Landing page with popular posts, recent activity, and tag cloud
   - Public access

2. **[PostListPage.md](./PostListPage.md)** - `/posts`
   - Browse and filter all posts with advanced filtering
   - Public access

3. **[TagCloudPage.md](./TagCloudPage.md)** - `/tags`
   - Interactive tag cloud for tag discovery
   - Public access

4. **[SearchPage.md](./SearchPage.md)** - `/search`
   - Full-text search with advanced filtering
   - Public access

5. **[CommunityPage.md](./CommunityPage.md)** - `/community`
   - Gold Standard/Community posts with citation relationships
   - Public access

### Post Management Pages

6. **[PostDetailPage.md](./PostDetailPage.md)** - `/posts/:postId`
   - View single post with full details and documents
   - Public for public posts, requires authentication for private/group posts

7. **[PostEditPage.md](./PostEditPage.md)** - `/posts/new` and `/posts/:postId/edit`
   - Create or edit posts (bookmarks and publications)
   - Requires authentication

8. **[ImportPage.md](./ImportPage.md)** - `/import`
   - Import posts from various sources (BibTeX, URLs, DOIs, PDFs, etc.)
   - Requires authentication

### User Pages

9. **[UserProfilePage.md](./UserProfilePage.md)** - `/users/:username`
   - View user profile with posts, tags, and groups
   - Public access

10. **[UserSettingsPage.md](./UserSettingsPage.md)** - `/settings`
    - Manage user settings, preferences, and API keys
    - Requires authentication

### Group Pages

11. **[GroupListPage.md](./GroupListPage.md)** - `/groups`
    - Browse and search groups
    - Public access

12. **[GroupDetailPage.md](./GroupDetailPage.md)** - `/groups/:groupname`
    - View group details, members, and posts
    - Public for public/viewable groups, requires authentication for private groups

13. **[GroupSettingsPage.md](./GroupSettingsPage.md)** - `/groups/:groupname/settings`
    - Manage group settings, members, invitations, and join requests
    - Requires group admin or moderator role

### Authentication Pages

14. **[LoginPage.md](./LoginPage.md)** - `/login`
    - User authentication via username/password or OAuth
    - Public access (redirects if already authenticated)

15. **[RegisterPage.md](./RegisterPage.md)** - `/register`
    - New user registration
    - Public access (redirects if already authenticated)

## Common Patterns

### State Management Strategy

- **vue-query**: Server state (API calls, caching, invalidation)
- **Pinia**: Client state (auth, UI preferences, global filters)
- **Component State**: Local UI state (modals, forms, temporary data)

### URL Parameter Patterns

- All filters reflected in URL for bookmarking and sharing
- Pagination: `offset` and `limit`
- Sorting: `sortBy` and `order`
- Filtering: `resourceType`, `tags`, `user`, `group`, `search`

### Responsive Breakpoints

- Desktop: `>1024px` - Full layout with sidebars
- Tablet: `768-1024px` - Adaptive layout, collapsible sidebars
- Mobile: `<768px` - Single column, bottom sheets, floating buttons

### Accessibility Standards

- Semantic HTML
- ARIA labels and roles
- Keyboard navigation
- Focus management
- Screen reader announcements
- Clear error messages

### i18n Support

- German (de) and English (en)
- Translation keys namespaced by page
- Consistent naming conventions

## Design Principles

### Modern UX Patterns

- **Optimistic updates**: Immediate UI feedback before API confirmation
- **Skeleton loading**: Content-aware placeholders during loading
- **Infinite scroll**: Progressive content loading with pagination fallback
- **Real-time search/filter**: Debounced input with instant results
- **Responsive design**: Mobile-first with progressive enhancement
- **Accessible interactions**: Keyboard, screen reader, and assistive technology support

### Performance Optimization

- **Code splitting**: Route-based lazy loading
- **Image optimization**: Lazy loading and responsive images
- **API caching**: vue-query automatic caching and deduplication
- **Virtual scrolling**: For long lists (tags, members)

### Error Handling

- **Toast notifications**: Non-intrusive error messages
- **Inline validation**: Real-time form validation
- **Retry mechanisms**: Automatic retry for failed requests
- **Fallback UI**: Graceful degradation when features fail

## Development Workflow

1. **Read the spec**: Understand the page requirements
2. **Create route**: Add route to `src/router/index.ts`
3. **Build components**: Create reusable components
4. **Implement API calls**: Use vue-query composables
5. **Add state management**: Pinia stores if needed
6. **Implement i18n**: Add translation keys
7. **Test accessibility**: Keyboard navigation, screen reader
8. **Test responsive**: Mobile, tablet, desktop
9. **Document**: Update this README if patterns change

## Related Documentation

- **[API Specification](../../bibsonomy-rest-api-v2/docs/openapi.yaml)**: REST API v2 endpoints
- **[Component Library](../components/README.md)**: Reusable Vue components (to be created)
- **[Composables](../composables/README.md)**: Vue composables for API and state (to be created)
- **[i18n Keys](../locales/README.md)**: Translation key reference (to be created)

## Questions?

For questions or clarifications about page specifications, refer to:

- **CLAUDE.md**: Project overview and development guidelines
- **OpenAPI spec**: API endpoint details and schemas
- **Modernization plan**: Architecture strategy and principles
