# SearchPage

## Route

`/search`

## Access Control

- [x] Public
- [ ] Requires Authentication
- [ ] Requires Admin

## Page Purpose

Full-text search across all posts with advanced filtering. Primary search interface with rich query capabilities, sorting, and result previews.

## Layout Structure

- **Header**: Global navigation
- **Search Header**:
  - Large search input (pre-filled from query)
  - Search button
  - Advanced filters toggle
- **Advanced Filters Panel** (collapsible):
  - Resource type filter
  - Tag filter
  - User filter
  - Group filter
  - Date range picker
- **Main Content Area**:
  - Active filters chips
  - Result count with query highlight
  - Sort controls
  - Search results (posts with highlighted matches)
  - Pagination or infinite scroll
- **Sidebar** (right):
  - Search tips
  - Related tags
  - Top results by type (Bookmarks / Publications)

## Components Used

- `AppHeader`
- `SearchInput`
- `AdvancedFiltersPanel`
- `FilterChip`
- `SearchResultCard` (with text highlights)
- `SearchResultsList`
- `SortControls`
- `SearchTips`
- `RelatedTagsList`
- `TopResultsByType`
- `InfiniteScrollObserver`
- `EmptyState`

## API Calls

```typescript
// Basic search
GET /api/v2/search?q=machine+learning&offset=0&limit=20

// With advanced filters
GET /api/v2/search?q=deep+learning&resourceType=bibtex&tags=nlp,ai&user=jsmith&offset=0&limit=20&sortBy=relevance&order=desc

// Load more (infinite scroll)
GET /api/v2/search?q=...&offset=20&limit=20

// Related tags based on search results
// Derived from search results or separate endpoint if available
```

## State Management

- **vue-query**:
  - `useInfiniteQuery(['search', query, filters], searchPosts)`
  - Automatically caches search results
- **Pinia**:
  - `searchStore` (persist recent searches to localStorage)
  - `filterStore` (persist filters to URL)
- **Component State**:
  - `query: string`
  - `advancedFiltersOpen: boolean`
  - `filters: SearchFilters`

## User Interactions

1. User types in search box → Updates query (debounced 300ms) → Updates URL → Executes search
2. User clicks "Search" button → Executes search immediately
3. User toggles "Advanced Filters" → Expands/collapses filter panel
4. User selects resource type filter → Updates URL → Re-executes search
5. User adds tag filter → Updates URL → Re-executes search
6. User clicks filter chip "x" → Removes filter → Re-executes search
7. User clicks search result → Navigates to `/posts/{postId}`
8. User clicks related tag → Adds to search query (or replaces) → Re-executes search
9. User scrolls to bottom → Loads next page of results
10. User changes sort → Updates URL → Re-executes search with new sort
11. User clicks "Clear all filters" → Removes all filters → Re-executes search

## URL Parameters & Query Strings

All parameters reflected in URL for bookmarking and sharing:

- `q`: Search query (required)
- `resourceType`: `all` | `bookmark` | `bibtex` (default: `all`)
- `tags`: Comma-separated tag names
- `user`: Username filter
- `group`: Group name filter
- `sortBy`: `relevance` | `date` | `title` (default: `relevance`)
- `order`: `asc` | `desc` (default: `desc`)
- `offset`: Pagination offset
- `limit`: Items per page (default: 20)

Example: `/search?q=machine+learning&resourceType=bibtex&tags=nlp&sortBy=relevance`

## Page States

- **Loading state**: Skeleton result cards (5-10 placeholders)
- **Empty state - No query**: "Enter a search query to find posts"
- **Empty state - No results**: "No results found for '{query}'. Try different keywords or filters."
- **Error state**: Toast notification + retry button
- **Success state**: Results with highlighted search terms
- **Loading more state**: Spinner at bottom while fetching next page

## Responsive Behavior

- **Desktop (>1024px)**: Full layout with sidebar, advanced filters inline
- **Tablet (768-1024px)**: Sidebar below results, advanced filters collapsible
- **Mobile (<768px)**: Single column, advanced filters as bottom sheet, floating filter button

## Accessibility Requirements

- Page title: "Search: {query} - BibSonomy"
- Search input with `aria-label="Search posts"`
- Result count announced to screen readers
- Search result highlights accessible (not just visual)
- Advanced filters panel with `aria-expanded` on toggle button
- Keyboard navigation for results
- Focus management when opening/closing advanced filters

## i18n Keys

```
page.search.title
page.search.placeholder
page.search.button
page.search.advancedFilters.toggle
page.search.advancedFilters.title
page.search.filters.resourceType
page.search.filters.tags
page.search.filters.user
page.search.filters.group
page.search.filters.dateRange
page.search.filters.clearAll
page.search.resultsCount
page.search.resultsFor
page.search.sortBy.relevance
page.search.sortBy.date
page.search.sortBy.title
page.search.order.asc
page.search.order.desc
page.search.noQuery
page.search.noResults
page.search.loadMore
page.search.loading
page.search.tips.title
page.search.tips.quotes
page.search.tips.wildcards
page.search.tips.operators
page.search.relatedTags.title
page.search.topResults.bookmarks
page.search.topResults.publications
```

## Mockup Notes

- Large, prominent search input at top (similar to Google)
- Advanced filters panel with smooth expand/collapse animation
- Search result cards with:
  - Title (with matching terms highlighted)
  - Snippet (with matching terms highlighted)
  - Resource type badge
  - Tags
  - User and date
  - Relevance score (optional, visual indicator)
- Active filters displayed as removable chips
- Result count with query in bold (e.g., "42 results for **machine learning**")
- Related tags as clickable chips
- Search tips as expandable section
- Highlighting uses subtle background color (yellow/light blue)
- Smooth scroll behavior for infinite scroll
- Loading skeletons match result card layout
- Empty state with search illustration and tips
- Advanced filters grouped and clearly labeled
