# CommunityPage

## Route

`/community`

## Access Control

- [x] Public
- [ ] Requires Authentication
- [ ] Requires Admin

## Page Purpose

Browse and explore Gold Standard/Community posts - curated, high-quality posts with scholarly relationships (references, part-of relations). Supports citation network visualization.

## Layout Structure

- **Header**: Global navigation
- **Page Header**:
  - Title: "Community Posts"
  - Subtitle: "Gold Standard and curated publications"
  - "Add to Community" button (authenticated users with permissions)
- **Filter Bar**:
  - Resource type (Publications only, typically)
  - Sort options (Date / Citations / References)
  - View toggle (List / Graph)
- **Main Content Area**:
  - **List View**: Community posts with citation indicators
  - **Graph View**: Citation network visualization (optional, advanced)
- **Sidebar** (right):
  - Most cited publications
  - Recent additions
  - Community statistics

## Components Used

- `AppHeader`
- `PageHeader`
- `FilterBar`
- `CommunityPostList`
- `CommunityPostCard` (with citation indicators)
- `CitationGraphVisualization` (optional)
- `MostCitedList`
- `RecentCommunityPostsList`
- `CommunityStats`
- `AddToCommunityButton`

## API Calls

```typescript
// List community posts
GET /api/v2/community?offset=0&limit=20&sortBy=date&order=desc

// With filters
GET /api/v2/community?sortBy=citations&order=desc

// Get specific community post with relations
GET /api/v2/community/{postId}

// Get references for a post
GET /api/v2/community/{postId}/references

// Get part-of relations for a post
GET /api/v2/community/{postId}/part-of

// Add post to community (authenticated, with permissions)
POST /api/v2/community
{
  "resource": { ... },
  "tags": [...],
  "groups": [...]
}

// Add reference relation
POST /api/v2/community/{postId}/references
{
  "targetPostId": 456
}

// Add part-of relation
POST /api/v2/community/{postId}/part-of
{
  "parentPostId": 789
}
```

## State Management

- **vue-query**:
  - `useInfiniteQuery(['community-posts', filters], fetchCommunityPosts)`
  - `useQuery(['community-post', postId], fetchCommunityPost)` (for detail view)
  - `useQuery(['post-references', postId], fetchPostReferences)`
  - `useQuery(['post-part-of', postId], fetchPostPartOf)`
  - `useQuery(['most-cited'], fetchMostCited)`
  - `useQuery(['recent-community'], fetchRecentCommunity)`
- **Pinia**:
  - `authStore` (check if user can add to community)
  - `filterStore` (persist filters)
- **Component State**:
  - `viewMode: 'list' | 'graph'`
  - `sortBy: SortBy`
  - `selectedPost: number | null` (for graph view)

## User Interactions

1. User clicks "List View" / "Graph View" → Toggles view mode
2. User clicks community post → Navigates to `/community/{postId}` (detail view with references)
3. User clicks "Add to Community" → Opens modal/wizard to add post
4. User clicks "References" count → Expands to show referenced posts
5. User clicks "Cited by" count → Shows posts that cite this one
6. User clicks "Part of" → Navigates to parent post
7. User scrolls to bottom → Loads next page (list view)
8. User hovers over node (graph view) → Highlights connected nodes
9. User clicks node (graph view) → Opens post detail panel
10. User changes sort → Updates list order

## URL Parameters & Query Strings

- `sortBy`: `date` | `citations` | `references` (default: `date`)
- `order`: `asc` | `desc` (default: `desc`)
- `view`: `list` | `graph` (default: `list`)
- `offset`: Pagination offset
- `limit`: Items per page (default: 20)

Example: `/community?sortBy=citations&order=desc&view=list`

## Page States

- **Loading state**: Skeleton cards (list view) or loading spinner (graph view)
- **Empty state**: "No community posts yet. Be the first to contribute!"
- **Error state**: Toast notification + retry button
- **Success state**: Posts loaded with citation indicators
- **Graph rendering state**: Loading spinner while building graph

## Responsive Behavior

- **Desktop (>1024px)**: Full layout with sidebar, graph view available
- **Tablet (768-1024px)**: Sidebar below, list view only (graph too complex)
- **Mobile (<768px)**: Single column, list view only, compact cards

## Accessibility Requirements

- Page title: "Community Posts - BibSonomy"
- Citation indicators with `aria-label` (e.g., "Cited by 5 publications")
- Reference links accessible via keyboard
- Graph visualization with text alternative (list of connections)
- View toggle with `role="tablist"`

## i18n Keys

```
page.community.title
page.community.subtitle
page.community.addToCommunity
page.community.view.list
page.community.view.graph
page.community.sortBy.date
page.community.sortBy.citations
page.community.sortBy.references
page.community.order.asc
page.community.order.desc
page.community.post.references
page.community.post.citedBy
page.community.post.partOf
page.community.post.viewReferences
page.community.post.viewCitations
page.community.mostCited.title
page.community.recentAdditions.title
page.community.stats.totalPosts
page.community.stats.totalReferences
page.community.stats.avgCitations
page.community.noPosts
page.community.loading
page.community.graph.loading
page.community.graph.error
page.community.graph.legend
```

## Mockup Notes

- Community post cards with:
  - Title
  - Authors
  - Year
  - Citation indicators (badges with counts)
  - Reference count badge
  - "Part of" indicator if applicable
  - Tags
- Citation indicators color-coded (green for many citations)
- References expandable inline (accordion style)
- Graph view (optional, advanced feature):
  - Nodes for posts
  - Edges for citations/references
  - Color coding by year or type
  - Interactive zoom/pan
  - Selected node detail panel
- Most cited list with citation count badges
- Recent additions with timestamp
- Community stats as cards with icons
- "Add to Community" button prominent (for authorized users)
- Smooth transitions when expanding references
- Loading states for graph rendering
