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
- DESIGN_SYSTEM.md: Section 11 (Component Patterns)
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

// Statistics (see MVP_API_SWITCHOVER.md for implementation status)
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

```text
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
- Secondary CTAs: `slate-200` with `slate-300` hover
- Statistics cards: `white` background with `slate-100` border

**Typography**:

- Hero heading: Display font (text-5xl, font-bold)
- Hero subheading: text-xl, font-normal, `slate-200`
- Section headings: text-2xl, font-semibold, `slate-900`
- Body text: text-base, `slate-700`

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

---

## Legacy Homepage Implementation Analysis

This section documents the legacy bibsonomy-webapp homepage implementation for reference during modernization.

### Legacy Files Structure

| File | Purpose |
|------|---------|
| `bibsonomy-webapp/src/main/webapp/WEB-INF/jsp/home.jspx` | Main homepage JSP template |
| `bibsonomy-webapp/src/main/webapp/WEB-INF/tags/home/bibhome.tagx` | "Simple" header variant (Jumbotron-style) |
| `bibsonomy-webapp/src/main/webapp/WEB-INF/tags/home/pumahome.tagx` | "PUMA" header variant (with carousel) |
| `bibsonomy-webapp/src/main/webapp/WEB-INF/tags/layout/resourceLayout.tagx` | Main layout wrapper with sidebar support |
| `bibsonomy-webapp/src/main/webapp/WEB-INF/tags/layout/sidebar/sidebarTagCloudItem.tagx` | Tag cloud sidebar component |
| `bibsonomy-webapp/src/main/webapp/WEB-INF/tags/tags/cloud.tagx` | Tag cloud rendering logic |

### Legacy Layout Structure

```
┌────────────────────────────────────────────────────────────┐
│                    HEADER / NAVIGATION                      │
├────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │   HERO SECTION (Jumbotron or Carousel)               │  │
│  │   - Only shown for non-logged-in users               │  │
│  │   - "Simple" variant: Jumbotron with headline/CTA    │  │
│  │   - "PUMA" variant: Carousel with 3 slides           │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌─────────────────────────────────────┐ ┌──────────────┐  │
│  │          MAIN CONTENT               │ │   SIDEBAR    │  │
│  │                                     │ │              │  │
│  │  ┌───────────────┬───────────────┐  │ │  Tag Cloud   │  │
│  │  │  BOOKMARKS    │ PUBLICATIONS  │  │ │  "busytags"  │  │
│  │  │  (col-md-6)   │  (col-md-6)   │  │ │              │  │
│  │  │               │               │  │ │              │  │
│  │  │  Post list    │  Post list    │  │ │              │  │
│  │  │  with next/   │  with next/   │  │ │              │  │
│  │  │  prev nav     │  prev nav     │  │ │              │  │
│  │  └───────────────┴───────────────┘  │ │              │  │
│  │                                     │ │              │  │
│  └─────────────────────────────────────┘ └──────────────┘  │
│                                                             │
├────────────────────────────────────────────────────────────┤
│                         FOOTER                              │
└────────────────────────────────────────────────────────────┘
```

### Legacy Hero Section Variants

#### 1. Simple (BibSonomy) - `bibhome.tagx`
- Bootstrap Jumbotron container
- Headline from `system.about.bs.headline`
- Lead text with project name substitution
- Optional logo image (theme-configurable)
- CTA buttons:
  - **Register** (green button, `btn-success`) - links to register URL
  - **Learn More** (blue button, `btn-primary`) - links to `/gettingStarted`
  - **Login** (blue button, `btn-primary`) - modal trigger or preferred login

#### 2. PUMA Variant - `pumahome.tagx`
- Bootstrap Carousel with 3 slides:
  1. "Collect" - collecting publications/bookmarks
  2. "Organize" - tagging and organizing
  3. "Share" - sharing with community
- Each slide has image + caption with title and description
- Welcome headline and introduction text below carousel

### Legacy Tag Cloud Features

The tag cloud sidebar (`sidebarTagCloudItem.tagx` + `cloud.tagx`) implements:
- **Minimum frequency filtering** (`minFreq` parameter)
- **Font size scaling** based on tag frequency (`computeTagFontsize` function)
- **Two display modes**:
  - `user` mode: Uses user-specific tag counts
  - `home` mode: Uses global tag counts
- **Logarithmic font sizing**: Tags sized from ~100% to ~200% based on frequency
- **Tag highlighting**: Ability to highlight specific tags (e.g., user's own tags)
- **Tooltips**: Optional hover tooltips showing post count

### Legacy Data Displayed

1. **Bookmarks Section**:
   - Recent bookmarks from public users
   - Column layout (shares space with publications)
   - Next/Previous pagination

2. **Publications Section**:
   - Recent BibTeX publications from public users
   - Column layout (shares space with bookmarks)
   - Next/Previous pagination

3. **Tag Cloud (Sidebar)**:
   - Called "busytags" - platform-wide popular tags
   - Tags sorted alphabetically for display
   - Font size indicates frequency
   - Clicking tag navigates to `/tag/{tagname}`

4. **News Bar (Optional)**:
   - Latest blog posts/bookmarks from news group
   - Shows as compact list if present

### Legacy vs v2 Comparison

| Feature | Legacy | v2 Current | v2 Needed |
|---------|--------|------------|-----------|
| Hero/Jumbotron | Yes (for guests) | Yes | - |
| Carousel | PUMA variant only | No | Not required |
| Bookmarks list | Yes | Yes | - |
| Publications list | Yes | Yes | - |
| Tag cloud | Yes (sidebar) | Yes (sidebar) | - |
| Resource type toggle | URL-based | SegmentedControl | - |
| Pagination | Next/Prev | Not implemented | Add |
| News bar | Optional | No | Not required |
| Login modal | Yes | Not implemented | Future |
| Search bar | Header | Header | - |
| User auth state detection | Yes | Not implemented | Future |

### Legacy i18n Keys Used

```
system.about.bs.headline      - Hero headline
system.about.bs.lead          - Hero lead text (with project name param)
system.about.bs.register      - Register button text
system.about.bs.learnmore     - Learn more button text
navi.login                    - Login button text
navi.search                   - Search placeholder
busytags                      - Tag cloud section title
navi.news                     - News section title
posts / post                  - Plural/singular for tooltips
```

### Current v2 Implementation Status

**Implemented**:
- [x] Hero/Jumbotron section with CTA buttons
- [x] Main layout with sidebar
- [x] Bookmarks and Publications sections (separate cards)
- [x] Tag cloud in sidebar (with logarithmic sizing)
- [x] Resource type toggle (SegmentedControl)
- [x] Responsive layout
- [x] i18n support

**Not Yet Implemented**:
- [ ] Pagination for post lists
- [ ] Tag page navigation (currently shows "not implemented")
- [ ] Login button/modal in Jumbotron
- [ ] User authentication state detection
- [ ] News bar (optional feature)

**Design Differences**:
- v2 uses modern Tailwind CSS styling vs Bootstrap 3
- v2 uses SegmentedControl for filtering vs URL-based
- v2 has cleaner card-based post display
- v2 sidebar is simpler (just tag cloud for now)
