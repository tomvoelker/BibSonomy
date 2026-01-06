# UserProfilePage

## Route

`/users/:username`

## Access Control

- [x] Public
- [ ] Requires Authentication
- [ ] Requires Admin

## Page Purpose

Display user's public profile including posts, tags, groups, and profile information. Users can view their own profile or other users' public profiles.

**Context**: Public-facing user portfolio. Must support:

- Professional academic profile display
- Content discovery (user's posts, research interests via tags)
- Social connections (groups, collaborators)
- Contact information (optional, user-controlled visibility)
- Export user's publications for CV/website

**Design References**:

- INFORMATION_ARCHITECTURE.md: Section 2.3 (Users Routes), Section 5.2 (Users API Mapping)
- DESIGN_SYSTEM.md: Section 7.6 (Patterns - Profile Pages)
- COMPONENT_LIBRARY.md: UserProfileHeader, UserAvatar, PostList, TagCloud

## Layout Structure

- **Header**: Global navigation
- **Profile Header**:
  - Avatar (placeholder or uploaded image)
  - Real name
  - Username
  - Institution (if public)
  - Homepage link (if public)
  - Biography
  - Joined date
  - Edit Profile button (own profile only)
- **Tab Navigation**:
  - Posts (default)
  - Tags
  - Groups
- **Main Content Area**:
  - Content changes based on active tab
  - Posts tab: User's posts with filters
  - Tags tab: Tag cloud of user's tags
  - Groups tab: List of user's groups
- **Sidebar** (right):
  - Statistics (post count, tag count, group count)
  - Recent activity

## Components Used

- `AppHeader`
- `UserProfileHeader`
- `UserAvatar`
- `TabNav`
- `PostList`
- `TagCloud`
- `GroupList`
- `StatisticsCard`
- `RecentActivityList`
- `EditProfileButton`

## API Calls

```typescript
// On mount
GET /api/v2/users/{username}

// Posts tab
GET /api/v2/users/{username}/posts?offset=0&limit=20&resourceType=all

// Posts with filters
GET /api/v2/users/{username}/posts?resourceType=bibtex&tags=machine-learning

// Tags (for tag cloud)
GET /api/v2/tags?user={username}&maxCount=100

// Groups
GET /api/v2/groups?user={username}
```

## State Management

- **vue-query**:
  - `useQuery(['user', username], fetchUser)`
  - `useQuery(['user-posts', username, filters], fetchUserPosts)`
  - `useQuery(['user-tags', username], fetchUserTags)`
  - `useQuery(['user-groups', username], fetchUserGroups)`
- **Pinia**:
  - `authStore` (check if viewing own profile)
- **Component State**:
  - `activeTab: 'posts' | 'tags' | 'groups'`
  - `postFilters: { resourceType, tags, sortBy, order }`

## User Interactions

1. User clicks "Posts" tab → Shows user's posts
2. User clicks "Tags" tab → Shows tag cloud
3. User clicks "Groups" tab → Shows user's groups
4. User clicks tag in cloud → Filters posts by that tag (switches to Posts tab)
5. User clicks group → Navigates to `/groups/{groupname}`
6. User clicks post → Navigates to `/posts/{postId}`
7. User clicks "Edit Profile" (own profile) → Navigates to `/users/{username}/edit`
8. User clicks homepage link → Opens in new tab
9. User filters posts by resource type → Updates post list
10. User clicks on tag → Navigates to `/tags/{tagname}/posts`

## URL Parameters & Query Strings

- `username`: Username (path parameter)
- `tab`: Active tab (`posts` | `tags` | `groups`) (default: `posts`)
- **Posts tab query params**:
  - `resourceType`: `all` | `bookmark` | `bibtex`
  - `tags`: Comma-separated tags
  - `sortBy`: `date` | `title`
  - `order`: `asc` | `desc`

Example: `/users/jsmith?tab=posts&resourceType=bibtex&tags=ml`

## Page States

- **Loading state**: Skeleton UI for profile header and content
- **Empty state**:
  - No posts: "No posts yet" (own profile: "Start by adding your first post")
  - No tags: "No tags yet"
  - No groups: "Not a member of any groups"
- **Error state - 404**: "User not found"
- **Error state - 500**: "Failed to load profile"
- **Success state**: Profile loaded with posts/tags/groups

## Responsive Behavior

- **Desktop (>1024px)**: Full layout with sidebar
- **Tablet (768-1024px)**: Sidebar below profile header, tabs full width
- **Mobile (<768px)**: Single column, sticky tab navigation, no sidebar stats (inline in profile header)

## Accessibility Requirements

- Page title: "{Real Name} (@{username}) - BibSonomy"
- Profile header uses semantic HTML (`<header>`, `<h1>` for name)
- Tab navigation with `role="tablist"` and `aria-selected`
- Avatar has `alt` text with username
- Statistics announced to screen readers
- Homepage link indicates external link

## i18n Keys

```
page.userProfile.title
page.userProfile.username
page.userProfile.realName
page.userProfile.institution
page.userProfile.homepage
page.userProfile.biography
page.userProfile.joined
page.userProfile.editProfile
page.userProfile.tabs.posts
page.userProfile.tabs.tags
page.userProfile.tabs.groups
page.userProfile.stats.posts
page.userProfile.stats.tags
page.userProfile.stats.groups
page.userProfile.recentActivity
page.userProfile.noPosts
page.userProfile.noPostsOwn
page.userProfile.noTags
page.userProfile.noGroups
page.userProfile.notFound
page.userProfile.error
page.userProfile.posts.filter.resourceType
page.userProfile.posts.filter.tags
page.userProfile.posts.filter.sortBy
```

## Design System References

**Colors**:

- Profile header background: Gradient `gray-50` to `white`
- Avatar border: `indigo-200` (2px)
- Tab navigation: `gray-300` inactive, `indigo-600` active with underline
- Statistics cards: `white` background, `gray-100` border
- Edit Profile button: `indigo-600`

**Typography**:

- Real name: text-3xl, font-bold, `gray-900`
- Username: text-xl, `gray-600`, monospace font
- Institution: text-base, `gray-700`
- Biography: text-base, leading-relaxed, `gray-700`
- Tab labels: text-base, font-medium
- Statistics values: text-2xl, font-bold, `gray-900`
- Statistics labels: text-sm, `gray-600`

**Spacing**:

- Profile header: py-12 px-6, max-w-7xl mx-auto
- Avatar: 120px (desktop), 80px (mobile), -mt-16 (overlaps header bg)
- Tab navigation: sticky top-0, z-10, bg-white, border-b
- Content area: py-8 px-6
- Sidebar: p-6, gap-6

## Mockup Notes

- Profile header with subtle gradient background (`gray-50` to `white`)
- Avatar displayed as large circle with `UserAvatar` component
  - 120px on desktop, 80px on mobile
  - Border ring (`ring-4 ring-white` over gradient)
  - Fallback to initials if no image
  - Upload button overlay on hover (own profile only)
- Biography displayed with line breaks preserved (white-space-pre-wrap)
- Long biography truncated with "Read more" expansion
- Tabs with underline indicator for active tab (Headless UI Tab component)
- Tab content with smooth fade transitions (transition-opacity duration-200)
- Posts tab: Reuses `PostList` component from PostListPage with user filter
- Posts tab includes resource type filter (All/Bookmarks/Publications)
- Tag cloud with interactive hover states:
  - Hover shows post count for tag
  - Click navigates to user's posts filtered by tag
  - Size indicates frequency (text-sm to text-2xl)
  - Color gradient from `gray-600` to `indigo-700`
- Groups displayed as `GroupCard` components in grid layout
- Group cards show role badge (Admin/Moderator/Member)
- Statistics cards with icons from design system:
  - Post count with document icon
  - Tag count with tag icon
  - Group count with users icon
- Recent activity as timeline (last 10 actions):
  - "Posted X", "Joined group Y", "Tagged with Z"
  - Relative timestamps (e.g., "2 hours ago")
  - Chronological order
- Edit Profile button only visible on own profile (computed `isOwnProfile`)
- Homepage link with external link icon, opens in new tab
- Social links (ORCID, Google Scholar, ResearchGate) if provided
- Smooth transitions when switching tabs
- Empty states for each tab:
  - No posts: "No posts yet" (own profile: "Create your first post")
  - No tags: "No tags yet"
  - No groups: "Not a member of any groups" (own profile: "Join or create a group")

## Advanced Features

### Export Options (Own Profile)

- Export publications list as BibTeX (for CV)
- Export bookmarks as HTML (for backup)
- Export profile as vCard (contact info)
- Download all documents (PDFs) as ZIP

### Privacy Controls (Own Profile)

- Quick toggle for profile visibility (Public/Private)
- Quick toggle for individual fields (email, homepage, institution)
- "View as public" mode to see profile as others see it

### Collaboration Features

- "Follow user" button (if feature exists in API)
- Send message button (if messaging feature exists)
- View shared groups (groups in common with viewer)
- Co-author network visualization (if available)

### Academic Integration

- ORCID integration (display ORCID badge, import publications)
- Google Scholar link with citation count (if available)
- ResearchGate profile link
- h-index and citation metrics (if available)

## Performance Considerations

- Lazy-load tab content (only fetch when tab is activated)
- Paginated posts list (20 per page)
- Tag cloud limited to top 100 tags
- Recent activity limited to 10 items
- Avatar images optimized and cached
- Prefetch user data for "View Profile" links on hover
- Tab switching uses client-side routing (no full reload)

## Error Handling

- 404: "User not found" with search suggestions
- 403: "This profile is private" (if feature exists)
- Network errors: Show cached data with "Offline" banner
- Empty states: Helpful messages with CTAs
- Slow loading: Show skeleton UI for each tab

## SEO & Metadata

- Page title: "{Real Name} (@{username}) - BibSonomy"
- Meta description: Biography snippet or "View {name}'s posts, tags, and groups on BibSonomy"
- Open Graph tags for social sharing (profile image, name, bio)
- Twitter Card metadata
- Canonical URL: `/users/{username}`
- Structured data (JSON-LD) for Person schema with:
  - name, email (if public), affiliation, sameAs (ORCID, Scholar)
  - Works (publications) as CreativeWork items

## Analytics

- Track profile views (own profile vs others)
- Track tab interactions (which tabs are most viewed)
- Track outbound clicks (homepage, ORCID, social links)
- Track export downloads
- Track tag cloud interactions
