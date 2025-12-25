# TagCloudPage

## Route

`/tags`

## Access Control

- [x] Public
- [ ] Requires Authentication
- [ ] Requires Admin

## Page Purpose

Explore all tags in BibSonomy as an interactive tag cloud. Tags are sized by frequency and organized for discovery. Users can click tags to see posts with that tag.

## Layout Structure

- **Header**: Global navigation
- **Page Header**:
  - Title: "Tags"
  - Search input for tags
- **Filter Controls**:
  - Minimum frequency slider
  - Maximum tag count slider
  - Sort options (Frequency / Alphabetical / Recent)
- **Main Content Area**:
  - Interactive tag cloud (responsive font sizes)
  - Tag count indicator
- **Sidebar** (right):
  - Popular Tags (top 10)
  - Recent Tags (last 10 used)
  - Related Tags (when hovering over a tag)

## Components Used

- `AppHeader`
- `PageHeader`
- `SearchInput`
- `TagCloudFilterControls`
- `InteractiveTagCloud`
- `PopularTagsList`
- `RecentTagsList`
- `RelatedTagsPanel`

## API Calls

```typescript
// On mount
GET /api/v2/tags?maxCount=100&minFreq=1

// With filters
GET /api/v2/tags?maxCount=200&minFreq=5

// Search tags
GET /api/v2/tags?search={query}&maxCount=50

// Popular tags
GET /api/v2/tags?sortBy=frequency&order=desc&limit=10

// Recent tags
GET /api/v2/tags?sortBy=recent&order=desc&limit=10

// Related tags (on hover)
GET /api/v2/tags/{tagname}/related?limit=10
```

## State Management

- **vue-query**:
  - `useQuery(['tags', filters], fetchTags)`
  - `useQuery(['popular-tags'], fetchPopularTags)`
  - `useQuery(['recent-tags'], fetchRecentTags)`
  - `useQuery(['related-tags', tagname], fetchRelatedTags, { enabled: !!hoveredTag })`
- **Pinia**:
  - `filterStore` (persist filters to localStorage)
- **Component State**:
  - `minFreq: number`
  - `maxCount: number`
  - `sortBy: SortBy`
  - `searchQuery: string`
  - `hoveredTag: string | null` (for related tags)

## User Interactions

1. User adjusts minimum frequency slider → Updates tag cloud (debounced)
2. User adjusts maximum tag count slider → Updates tag cloud (debounced)
3. User types in search → Filters tags (debounced 300ms)
4. User clicks tag in cloud → Navigates to `/tags/{tagname}/posts`
5. User hovers over tag → Shows related tags in sidebar
6. User clicks related tag → Navigates to that tag's posts
7. User clicks popular tag → Navigates to that tag's posts
8. User clicks recent tag → Navigates to that tag's posts
9. User changes sort → Reorders tag cloud

## URL Parameters & Query Strings

- `minFreq`: Minimum frequency (default: 1)
- `maxCount`: Maximum tag count (default: 100)
- `sortBy`: `frequency` | `alphabetical` | `recent` (default: `frequency`)
- `search`: Search query

Example: `/tags?minFreq=5&maxCount=200&sortBy=frequency`

## Page States

- **Loading state**: Skeleton cloud with placeholder tags
- **Empty state**:
  - No filters: "No tags found. Be the first to tag a post!"
  - With filters: "No tags match your criteria. Try lowering the minimum frequency."
- **Error state**: Toast notification + retry button
- **Success state**: Interactive tag cloud with varying font sizes

## Responsive Behavior

- **Desktop (>1024px)**: Full layout with sidebar, large tag cloud
- **Tablet (768-1024px)**: Sidebar below cloud, medium tag cloud
- **Mobile (<768px)**: Single column, compact tag cloud, no sidebar (related tags as bottom sheet on tap)

## Accessibility Requirements

- Page title: "Tag Cloud - BibSonomy"
- Tag cloud as `<nav>` with `aria-label="Tag cloud"`
- Each tag as clickable link with frequency in `aria-label` (e.g., "machine-learning, 42 posts")
- Filter sliders with proper `aria-label` and live region for value
- Tag count announced to screen readers on update
- Keyboard navigation for tag cloud (Tab to navigate, Enter to select)

## i18n Keys

```
page.tagCloud.title
page.tagCloud.search.placeholder
page.tagCloud.filters.minFreq
page.tagCloud.filters.maxCount
page.tagCloud.filters.sortBy.frequency
page.tagCloud.filters.sortBy.alphabetical
page.tagCloud.filters.sortBy.recent
page.tagCloud.tagCount
page.tagCloud.popularTags.title
page.tagCloud.recentTags.title
page.tagCloud.relatedTags.title
page.tagCloud.relatedTags.loading
page.tagCloud.relatedTags.empty
page.tagCloud.noTags
page.tagCloud.noTagsFiltered
page.tagCloud.loading
page.tagCloud.tag.postsCount
```

## Mockup Notes

- Tag cloud with varying font sizes (smallest: 12px, largest: 48px)
- Tags color-coded by category (optional: color gradient based on frequency)
- Smooth font size transitions when filters change
- Hover effect on tags (slight color change, underline)
- Related tags panel slides in from right on hover (desktop)
- Filter sliders with live value display
- Popular tags with badge showing post count
- Recent tags with timestamp
- Search bar with autocomplete suggestions
- Empty state with illustration of tags
- Smooth fade-in animations for tag cloud
- Responsive wrapping of tags
- Tag cloud centered on page
