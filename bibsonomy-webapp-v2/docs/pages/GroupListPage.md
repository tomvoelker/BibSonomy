# GroupListPage

## Route

`/groups`

## Access Control

- [x] Public
- [ ] Requires Authentication
- [ ] Requires Admin

## Page Purpose

Browse and discover groups on BibSonomy. Users can search for groups, filter by visibility, and create new groups (if authenticated).

## Layout Structure

- **Header**: Global navigation
- **Page Header**:
  - Title: "Groups"
  - "Create Group" button (authenticated users only)
  - Search input
- **Filter Bar**:
  - Visibility filter (All / Public / Private / Viewable)
  - Sort options (Name / Member count / Post count)
- **Main Content Area**:
  - Active filters
  - Group count
  - Group list (cards with infinite scroll or pagination)
- **Sidebar** (right, optional):
  - My Groups (authenticated users)
  - Popular Groups
  - Group Categories (if implemented)

## Components Used

- `AppHeader`
- `PageHeader`
- `SearchInput`
- `FilterBar`
- `GroupCard`
- `GroupList`
- `CreateGroupButton`
- `MyGroupsList`
- `PopularGroupsList`
- `InfiniteScrollObserver`
- `EmptyState`

## API Calls

```typescript
// On mount
GET /api/v2/groups?offset=0&limit=20

// With filters
GET /api/v2/groups?visibility=public&search=research&offset=0&limit=20

// My groups (authenticated)
GET /api/v2/groups?user={currentUsername}

// Popular groups
GET /api/v2/groups?sortBy=memberCount&order=desc&limit=10
```

## State Management

- **vue-query**:
  - `useInfiniteQuery(['groups', filters], fetchGroups)`
  - `useQuery(['my-groups', username], fetchMyGroups)` (if authenticated)
  - `useQuery(['popular-groups'], fetchPopularGroups)`
- **Pinia**:
  - `authStore` (check if authenticated)
  - `filterStore` (persist filters to URL)
- **Component State**:
  - `searchQuery: string`
  - `visibilityFilter: VisibilityFilter`
  - `sortBy: SortBy`

## User Interactions

1. User types in search → Debounced (300ms) → Updates URL → Refetches groups
2. User selects visibility filter → Updates URL → Refetches groups
3. User clicks "Create Group" → Navigates to `/groups/new`
4. User clicks group card → Navigates to `/groups/{groupname}`
5. User scrolls to bottom → Loads next page
6. User clicks on "My Groups" item → Navigates to that group
7. User clicks on "Popular Groups" item → Navigates to that group

## URL Parameters & Query Strings

- `search`: Search query
- `visibility`: `all` | `public` | `private` | `viewable` (default: `all`)
- `sortBy`: `name` | `memberCount` | `postCount` (default: `name`)
- `order`: `asc` | `desc` (default: `asc`)
- `offset`: Pagination offset
- `limit`: Items per page (default: 20)

Example: `/groups?search=research&visibility=public&sortBy=memberCount&order=desc`

## Page States

- **Loading state**: Skeleton cards (5-10 placeholder cards)
- **Empty state**:
  - No filters: "No groups found. Be the first to create one!"
  - With filters: "No groups match your search. Try different filters."
- **Error state**: Toast notification + retry button
- **Success state**: Group cards with smooth fade-in
- **Loading more state**: Spinner at bottom while fetching next page

## Responsive Behavior

- **Desktop (>1024px)**: Sidebar visible, group cards in grid (3 columns)
- **Tablet (768-1024px)**: No sidebar, group cards 2 columns
- **Mobile (<768px)**: Single column, floating "Create Group" button

## Accessibility Requirements

- Page title: "Browse Groups - BibSonomy"
- Search input with `aria-label="Search groups"`
- Filter controls with clear labels
- Group count announced to screen readers on update
- Keyboard navigation for group cards
- Create Group button clearly labeled
- Skip to results link

## i18n Keys

```
page.groupList.title
page.groupList.createGroup
page.groupList.search.placeholder
page.groupList.filter.visibility.all
page.groupList.filter.visibility.public
page.groupList.filter.visibility.private
page.groupList.filter.visibility.viewable
page.groupList.sortBy.name
page.groupList.sortBy.memberCount
page.groupList.sortBy.postCount
page.groupList.order.asc
page.groupList.order.desc
page.groupList.resultsCount
page.groupList.noResults
page.groupList.noResultsFiltered
page.groupList.myGroups.title
page.groupList.myGroups.empty
page.groupList.popularGroups.title
page.groupList.loadMore
page.groupList.loading
```

## Mockup Notes

- Group cards with:
  - Group icon/avatar
  - Group name (display name)
  - Description (truncated)
  - Visibility badge (Public/Private/Viewable)
  - Member count
  - Post count
  - Join/Request button (if not member)
- Create Group button as prominent CTA (primary color)
- Search bar with icon
- Filter bar as pill-style buttons
- My Groups sidebar as compact list
- Popular Groups sidebar with member count badges
- Hover effect on group cards (slight elevation)
- Smooth transitions when filtering
- Empty state with illustration and "Create Group" CTA
