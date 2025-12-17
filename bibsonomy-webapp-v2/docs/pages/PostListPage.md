# PostListPage

## Route

`/posts`

## Access Control

- [x] Public
- [ ] Requires Authentication
- [ ] Requires Admin

## Page Purpose

Browse and filter all posts (bookmarks and publications). Primary discovery interface for exploring BibSonomy content with advanced filtering, sorting, and search capabilities.

**Context**: Core discovery page for BibSonomy. Users spend significant time here browsing, filtering, and discovering content. Must support:

- Advanced filtering without overwhelming UI
- Quick preview of posts without requiring detail page navigation
- Export capabilities for research workflows
- Bookmarkable filter states via URL parameters

**Design References**:

- INFORMATION_ARCHITECTURE.md: Section 2.2 (Posts Routes), Section 5.1 (Posts API Mapping)
- DESIGN_SYSTEM.md: Section 7.3 (Patterns - Browse/List Views)
- COMPONENT_LIBRARY.md: PostList, PostCard, FilterBar components

## Layout Structure

- **Header**: Global navigation
- **Filter Bar** (top): Resource type toggle (All/Bookmarks/Publications), search input, sort controls
- **Main Content Area** (70%):
  - Active filters chips (removable)
  - Post count indicator
  - Post list (with infinite scroll or pagination)
  - Loading skeletons between batches
- **Sidebar** (right, 30%):
  - Tag filter panel with autocomplete
  - User filter
  - Group filter
  - Date range picker
  - Export options (BibTeX, CSV, RSS, etc.)

## Components Used

- `AppHeader`
- `FilterBar`
- `FilterChip`
- `PostList`
- `PostCard`
- `TagFilterPanel`
- `UserFilterInput`
- `GroupFilterSelect`
- `DateRangePicker`
- `ExportMenu`
- `InfiniteScrollObserver` (or `PaginationControls`)
- `EmptyState`

## API Calls

```typescript
// Initial load
GET /api/v2/posts?offset=0&limit=20&sortBy=date&order=desc

// With filters
GET /api/v2/posts?resourceType=bookmark&tags=machine-learning,nlp&user=jsmith&offset=0&limit=20&sortBy=relevance

// Search query
GET /api/v2/posts?search=deep+learning&offset=0&limit=20

// Load more (infinite scroll)
GET /api/v2/posts?offset=20&limit=20&[...same filters]

// Export
GET /api/v2/posts?format=bibtex&[...filters]
```

## State Management

- **vue-query**:
  - `useInfiniteQuery(['posts', filters], fetchPosts)` with pagination
  - Automatically caches pages and manages loading states
- **Pinia**:
  - `filterStore` (persist filters to localStorage and URL)
  - `authStore` (user context)
- **Component State**:
  - `localFilters: FilterParams` (before applying to URL)
  - `showFilterSidebar: boolean` (mobile toggle)

## User Interactions

1. User types in search → Debounced (300ms) → Updates URL query → Refetches posts
2. User toggles resource type (All/Bookmark/BibTeX) → Updates URL → Refetches
3. User adds tag filter → Shows autocomplete → Selects tag → Adds to URL → Refetches
4. User clicks filter chip "x" → Removes filter from URL → Refetches
5. User scrolls to bottom → Loads next page (infinite scroll)
6. User clicks "Export as BibTeX" → Downloads file with current filters
7. User clicks post card → Navigates to `/posts/{postId}`
8. User changes sort (Date/Title/Relevance) → Updates URL → Refetches with new sort

## URL Parameters & Query Strings

All filters reflected in URL for bookmarking and sharing:

- `resourceType`: `all` | `bookmark` | `bibtex` (default: `all`)
- `tags`: Comma-separated tag names (e.g., `machine-learning,nlp`)
- `user`: Username filter
- `group`: Group name filter
- `search`: Full-text search query
- `sortBy`: `date` | `title` | `author` | `relevance` (default: `date`)
- `order`: `asc` | `desc` (default: `desc`)
- `offset`: Pagination offset (for direct links)
- `limit`: Items per page (default: 20)

Example: `/posts?resourceType=bibtex&tags=machine-learning&sortBy=date&order=desc`

## Page States

- **Loading state**: Skeleton cards (5-10 placeholder cards)
- **Empty state**:
  - No filters: "No posts found. Be the first to add one!"
  - With filters: "No posts match your filters. Try adjusting your search."
- **Error state**: Toast notification + retry button
- **Success state**: Post cards with smooth fade-in
- **Loading more state**: Spinner at bottom of list while fetching next page

## Responsive Behavior

- **Desktop (>1024px)**: Sidebar visible, filter bar inline, post cards in grid (2-3 columns)
- **Tablet (768-1024px)**: Sidebar toggleable (slide-in), filter bar inline, post cards 2 columns
- **Mobile (<768px)**: Sidebar as bottom sheet/modal, filter bar stacked, post cards single column, floating filter button

## Accessibility Requirements

- Page title: "Browse Posts - BibSonomy"
- Filter controls have clear labels and `aria-label` attributes
- Post count announced to screen readers on update
- Keyboard navigation for filter chips (arrow keys to navigate, Delete/Backspace to remove)
- Focus management when opening/closing filter sidebar
- Infinite scroll with "Load more" button fallback for keyboard users
- Skip to results link

## i18n Keys

```
page.postList.title
page.postList.filterBy
page.postList.resourceType.all
page.postList.resourceType.bookmark
page.postList.resourceType.bibtex
page.postList.sortBy.date
page.postList.sortBy.title
page.postList.sortBy.relevance
page.postList.order.asc
page.postList.order.desc
page.postList.filters.tags
page.postList.filters.user
page.postList.filters.group
page.postList.filters.dateRange
page.postList.activeFilters
page.postList.clearFilters
page.postList.resultsCount
page.postList.noResults
page.postList.noResultsFiltered
page.postList.export.title
page.postList.export.bibtex
page.postList.export.csv
page.postList.export.rss
page.postList.loadMore
page.postList.loading
```

## Design System References

**Colors**:

- Filter bar background: `gray-50`
- Active filter chips: `indigo-100` background, `indigo-700` text
- Post card: `white` background with `gray-200` border, hover `gray-300` border
- Resource type badges: `emerald-100` (bookmark), `blue-100` (publication)

**Typography**:

- Page title: text-3xl, font-bold, `gray-900`
- Filter labels: text-sm, font-medium, `gray-700`
- Post count: text-sm, `gray-600`
- Post titles: text-lg, font-semibold, `gray-900`

**Spacing**:

- Filter bar: py-4 px-6, sticky top-0
- Post list gap: gap-4 (vertical stacking)
- Sidebar filters: p-4, gap-6 between sections
- Active filter chips: gap-2, p-2

**Interactive States**:

- Filter chips: Removable with X button, hover shows `gray-100` background on X
- Post cards: Hover shows elevation (shadow-md), cursor-pointer
- Sort dropdown: Headless UI Listbox with smooth transitions

## Mockup Notes

- Filter bar with pill-style resource type toggle (Headless UI RadioGroup component)
- Resource type toggle uses `SegmentedControl` pattern from component library
- Active filters displayed as removable `Chip` components below filter bar
- Post cards with hover effect (shadow-sm to shadow-md transition)
- Sidebar filters grouped in collapsible `Disclosure` sections (Headless UI)
- Export menu as Headless UI `Menu` dropdown with icons for each format
- Smooth scroll behavior for infinite scroll (Intersection Observer API)
- Loading skeletons match post card layout exactly (same dimensions and spacing)
- Empty state with custom illustration and helpful CTAs ("Create Post" or "Adjust Filters")
- Floating filter button on mobile (bottom-right, opens sidebar as sheet)
- Infinite scroll with sentinel element at 80% of list for smooth loading
- URL updates on filter change (vue-router push with query params)

## Performance Considerations

- Virtualized list rendering for very long lists (consider `vue-virtual-scroller`)
- Debounced search input (300ms) to prevent excessive API calls
- Cached filter options (tags, users, groups) with stale-while-revalidate
- Lazy-load post thumbnails/images with IntersectionObserver
- Prefetch next page when user scrolls to 80% of current list
- Export operations offloaded to Web Worker for large datasets
- URL state synced with vue-router (shallow navigation, no full reload)

## Advanced Features

### Saved Filters

- Authenticated users can save filter combinations
- "Save current filters" button in filter sidebar
- Saved filters appear in dropdown for quick access
- Stored in user preferences (Pinia + API)

### Bulk Operations (Authenticated)

- Select multiple posts with checkboxes
- Bulk actions: Export, Add to Group, Tag
- Selection persists across pagination
- Clear selection button

### Keyboard Shortcuts

- `/` - Focus search input
- `j/k` - Navigate down/up in post list
- `Enter` - Open selected post
- `Esc` - Clear filters/close filter sidebar
- `?` - Show keyboard shortcuts help

## Error Handling

- Network errors: Toast notification with "Retry" button
- Empty results: Contextual empty state (no filters vs filtered)
- Invalid URL parameters: Reset to defaults, show warning toast
- API timeout: Show cached results if available with "Results may be outdated" banner
- Rate limiting: Show "Too many requests" message with retry countdown

## SEO Considerations

- Server-side rendered (SSR) initial post list for SEO
- Dynamic meta tags based on filters (e.g., "Machine Learning Posts - BibSonomy")
- Canonical URL for default view (`/posts`)
- Pagination meta tags (rel="next", rel="prev")
- Structured data (JSON-LD) for posts in list
