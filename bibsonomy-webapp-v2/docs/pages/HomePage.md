# HomePage

## Route

`/`

## Access Control

- [x] Public
- [ ] Requires Authentication
- [ ] Requires Admin

## Page Purpose

Landing page for BibSonomy. Introduces the platform to new visitors and provides quick access to popular posts, recent activity, and key features. For authenticated users, shows personalized content feed.

**Context**: Primary entry point for all users. Must balance:

- First-time visitor education (what is BibSonomy?)
- Quick access to core features for returning users
- Discovery of popular/trending content
- Call-to-action for registration

**Design References**:

- INFORMATION_ARCHITECTURE.md: Section 1.1 (Public Pages)
- DESIGN_SYSTEM.md: Section 7 (Patterns - Landing Pages)
- COMPONENT_LIBRARY.md: Layout Components, Domain Components

## Layout Structure

- **Header**: Global navigation with logo, search bar, user menu (login/register for guests)
- **Hero Section**: Brief intro to BibSonomy with CTA buttons (Register, Browse, Learn More)
- **Main Content Area**:
  - Popular Posts (last 10 days)
  - Recent Public Posts (timeline view)
  - Tag Cloud (trending tags)
- **Sidebar** (right):
  - Statistics (total posts, users, groups)
  - Quick Start Guide for new users
  - Featured Groups
- **Footer**: Links, about, documentation

## Components Used

**From Layout Components**:

- `AppHeader` - Global navigation with search and auth controls
- `AppFooter` - Site links, legal, social

**From Domain Components**:

- `PostList` - Display recent/popular posts (compact variant)
- `PostCard` - Individual post preview
- `TagCloud` - Visual tag frequency display
- `GroupCard` - Featured group previews

**Custom to HomePage**:

- `HeroSection` - Landing hero with value proposition and CTAs
- `StatisticsCard` - Platform metrics (total posts, users, groups)
- `FeaturedGroupsList` - Curated/popular groups
- `QuickStartGuide` - Onboarding tips for new users

**From UI Components** (Base):

- `Button` - CTAs in hero section
- `Card` - Container for statistics and featured content
- `Skeleton` - Loading states

## API Calls

```typescript
// On mount
GET /api/v2/search/popular?days=10&limit=10

// Recent posts
GET /api/v2/posts?offset=0&limit=20&sortBy=date&order=desc

// Tag cloud
GET /api/v2/tags?maxCount=50&minFreq=5

// Statistics (if endpoint exists, otherwise computed from other calls)
// GET /api/v2/stats/overview
```

## State Management

- **vue-query**:
  - `useQuery(['popular-posts'], fetchPopularPosts)`
  - `useQuery(['recent-posts'], fetchRecentPosts)`
  - `useQuery(['tag-cloud'], fetchTagCloud)`
- **Pinia**:
  - `authStore` (check if user is logged in)
  - `uiStore` (theme, language)
- **Component State**:
  - `heroExpanded: boolean` (expand/collapse hero section on scroll)

## User Interactions

1. User clicks "Browse Posts" → Navigates to `/posts`
2. User clicks "Register" → Navigates to `/register`
3. User clicks on a tag in tag cloud → Navigates to `/tags/{tagname}/posts`
4. User clicks on a popular post → Navigates to `/posts/{postId}`
5. User scrolls down → Hero section collapses to compact header
6. User changes language toggle → Updates i18n locale, persists to localStorage

## URL Parameters & Query Strings

None (root route)

## Page States

- **Loading state**: Skeleton UI for post list, tag cloud, and stats cards
- **Empty state**: Unlikely on home page (fallback: "No recent posts")
- **Error state**: Toast notification if API fails, show cached data if available
- **Success state**: Fully loaded content with smooth transitions

## Responsive Behavior

- **Desktop (>1024px)**: Full layout with sidebar, hero section spans full width
- **Tablet (768-1024px)**: Sidebar moves below main content, hero slightly compressed
- **Mobile (<768px)**: Single column, hero CTA stacked vertically, tag cloud limited to 20 tags

## Accessibility Requirements

- Page title: "BibSonomy - Social Bookmarking and Publication Sharing"
- Meta description for SEO
- Skip to content link for keyboard navigation
- Hero CTA buttons have clear focus states
- Tag cloud uses semantic `<nav>` element with `aria-label="Popular tags"`
- Post list announces count to screen readers

## i18n Keys

```
page.home.title
page.home.heroHeading
page.home.heroSubheading
page.home.ctaBrowse
page.home.ctaRegister
page.home.ctaLearnMore
page.home.sectionPopular
page.home.sectionRecent
page.home.sectionTags
page.home.statsUsers
page.home.statsPosts
page.home.statsGroups
page.home.quickStart.title
page.home.quickStart.step1
page.home.quickStart.step2
page.home.quickStart.step3
```

## Design System References

**Colors** (from DESIGN_SYSTEM.md):

- Hero background: Gradient from `indigo-600` to `indigo-800`
- Primary CTAs: `indigo-600` with `indigo-700` hover
- Secondary CTAs: `gray-200` with `gray-300` hover
- Statistics cards: `white` background with `gray-100` border

**Typography**:

- Hero heading: Display font (text-5xl, font-bold)
- Hero subheading: text-xl, font-normal, `gray-200`
- Section headings: text-2xl, font-semibold, `gray-900`
- Body text: text-base, `gray-700`

**Spacing**:

- Hero section: py-20 (desktop), py-12 (mobile)
- Content sections: py-16 gap-8
- Card spacing: p-6 with gap-4 between elements

**Layout Breakpoints**:

- Mobile (<768px): Single column, stacked sections
- Tablet (768-1024px): 2-column grid for posts/stats
- Desktop (>1024px): Full layout with sidebar

## Mockup Notes

- Hero section uses gradient background (`indigo-600` to `indigo-800` from design system)
- Hero CTAs use `Button` component (variant="primary" for Register, variant="ghost" for Browse)
- Popular posts displayed as `PostCard` components in grid layout (3 columns on desktop, 2 on tablet, 1 on mobile)
- Recent posts in vertical `PostList` with compact variant
- Tag cloud uses weighted font sizes (text-sm to text-3xl based on frequency)
- Statistics cards use `Card` component with icon, number, and label
- Featured groups as horizontal scrollable list on mobile
- Smooth fade-in animations (transition-opacity duration-500) for content sections on initial load
- Sticky header on scroll with subtle shadow (shadow-sm)
- Skeleton loaders match exact layout of loaded content
- Empty state unlikely but shows friendly "No recent posts" with illustration

## Performance Considerations

- Hero image/background optimized and lazy-loaded if needed
- Initial API calls parallelized (popular posts + recent posts + tag cloud)
- Statistics cached for 5 minutes (low priority, can be stale)
- Infinite scroll or pagination for recent posts (load 20 at a time)
- Tag cloud limited to top 50 tags to prevent overwhelming DOM
- Images in post cards lazy-loaded below fold

## Analytics & Tracking

- Track hero CTA clicks (Register, Browse, Learn More)
- Track tag cloud interactions (which tags are clicked)
- Track popular post clicks
- Time on page before scrolling
- Bounce rate from hero section

## Content Strategy

- Hero text should be bilingual (German/English) via i18n
- Statistics update in real-time (WebSocket or polling) if feasible
- Featured groups curated by admins (not just by post count)
- Quick Start Guide personalized based on auth state
