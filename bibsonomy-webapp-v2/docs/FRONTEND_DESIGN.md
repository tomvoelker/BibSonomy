# BibSonomy Frontend Design Documentation

**Version:** 1.0
**Last Updated:** 2025-12-15
**Status:** Initial Design Specification

---

## Overview

This document serves as the main entry point for the BibSonomy frontend modernization project. It provides a comprehensive design specification for building a modern, accessible Vue 3 frontend that replaces the legacy 2010-era JSP/Spring MVC webapp while maintaining similar functionality.

**Project Goals:**

- Modernize the user interface with contemporary design patterns
- Improve accessibility (WCAG 2.1 AA compliance)
- Enhance mobile responsiveness
- Maintain core functionality from the legacy webapp
- Provide a foundation for future feature development

---

## Document Structure

The frontend design documentation is organized into the following sections:

### 1. **[Design System](DESIGN_SYSTEM.md)** ⭐ Start Here

The visual language and UI foundation for the entire application.

**What's Inside:**

- **Color Palette**: Modern indigo primary colors, semantic colors, light/dark mode
- **Typography**: System font stacks, type scale, font weights
- **Spacing System**: 8px base unit, consistent spacing scale
- **Shadows & Elevation**: 5-level shadow system
- **Border Radius**: Consistent rounding scale
- **Transitions & Animations**: Duration scale, easing functions
- **Breakpoints**: Mobile-first responsive breakpoints
- **Iconography**: Heroicons recommendation
- **Design Tokens**: Semantic token mapping, Tailwind configuration

**Key Highlights:**

- Moves from 2010-era bright blue to modern indigo palette
- WCAG 2.1 AA compliant contrast ratios throughout
- Detailed Tailwind CSS implementation examples
- Complete dark mode specifications

**When to Use:** Reference this for any visual design decisions, Tailwind class choices, or styling questions.

---

### 2. **[Information Architecture](INFORMATION_ARCHITECTURE.md)** 🗺️

The structure, navigation, and organization of the entire application.

**What's Inside:**

- **Sitemap**: Complete page hierarchy (57 pages total)
  - Public pages (17)
  - Authenticated pages (19)
  - Admin pages (4)
- **Routes & URLs**: Vue Router definitions with parameters and guards
- **Navigation Structure**: Header, sidebar, footer, mobile patterns
- **User Flows**: 6 detailed user journeys
- **Page-to-API Mapping**: Every page mapped to REST API v2 endpoints
- **State Management Strategy**: vue-query + Pinia + component state
- **Internationalization**: German/English support with vue-i18n

**Key Highlights:**

- All 57 pages documented with route definitions
- Complete user flows (registration, bookmarking, import, groups, search, settings)
- Clear state management decision tree
- URL parameter conventions for bookmarking and sharing

**When to Use:** Reference this for routing, navigation, state management decisions, or understanding user flows.

---

### 3. **[Component Library](COMPONENT_LIBRARY.md)** 🧩

Specifications for all reusable Vue 3 components.

**What's Inside:**

- **Base UI Components (16)**: Button, Input, Select, Modal, Tabs, Badge, Alert, etc.
- **Layout Components (6)**: AppHeader, AppFooter, Container, Card, Sidebar, Section
- **Domain Components (12)**: PostCard, TagCloud, UserAvatar, BibtexDisplay, SearchBar, etc.
- **Form Components (5)**: LoginForm, PostForm, UserSettingsForm, GroupForm, etc.
- **Navigation Components (4)**: Breadcrumbs, TopNav, UserMenu, LanguageSwitcher

**Key Highlights:**

- 60+ components with full TypeScript interfaces
- Props, events, slots, and accessibility requirements for each
- Example usage for every component
- Usage guidelines and best practices

**When to Use:** Reference this when implementing components, understanding props/events, or ensuring consistency.

---

### 4. **[Page Specifications](pages/)** 📄

Detailed specifications for each page in the application.

**What's Inside:**
Individual markdown files for each major page:

**Core Pages:**

- [HomePage.md](pages/HomePage.md) - Landing page with hero, popular posts, tag cloud
- [PostListPage.md](pages/PostListPage.md) - Browse/filter posts with advanced filters
- [PostDetailPage.md](pages/PostDetailPage.md) - Single post view with metadata
- [PostEditPage.md](pages/PostEditPage.md) - Create/edit posts (unified form)

**User Pages:**

- [UserProfilePage.md](pages/UserProfilePage.md) - User profile with posts/tags/groups tabs
- [UserSettingsPage.md](pages/UserSettingsPage.md) - Settings (profile, preferences, API keys, account)

**Group Pages:**

- [GroupListPage.md](pages/GroupListPage.md) - Browse/search groups
- [GroupDetailPage.md](pages/GroupDetailPage.md) - Group page with posts/members tabs

**Additional Pages:** (in progress)

- LoginPage, RegisterPage
- TagCloudPage, TagDetailPage
- SearchPage, PopularPostsPage
- ImportPage (with sub-pages for BibTeX, DOI, URL, PDF, etc.)
- CommunityPostsPage
- Admin pages

**Each Page Spec Includes:**

- Route definition and access control
- Page purpose and layout structure
- Components used
- API calls with TypeScript examples
- State management (vue-query, Pinia, component state)
- User interactions (step-by-step)
- URL parameters and query strings
- Page states (loading, empty, error, success)
- Responsive behavior (desktop/tablet/mobile)
- Accessibility requirements
- i18n translation keys
- Mockup notes (visual design guidance)

**When to Use:** Reference individual page specs when implementing pages or understanding UX flows.

---

## Technical Stack

### Core Technologies

**Frontend Framework:**

- **Vue 3** (Composition API) - Progressive JavaScript framework
- **TypeScript** (strict mode) - Type safety
- **Vite** - Build tool (fast, modern)

**Routing & State:**

- **vue-router** - Client-side routing
- **@tanstack/vue-query** - Server state management (caching, mutations)
- **Pinia** - Client/UI state (auth, preferences, global UI)

**Styling:**

- **Tailwind CSS** - Utility-first CSS framework
- **Headless UI** - Accessible component primitives
- **Heroicons** - Icon system

**Internationalization:**

- **vue-i18n** - German (default) and English

**Validation:**

- **Zod** - Runtime type validation for API responses

### Code Quality & Testing

**Testing:**

- **Vitest** - Unit and integration tests
- **@testing-library/vue** - Component testing
- **jest-axe** - Accessibility testing

**Linting & Formatting:**

- **ESLint** - Code linting
- **Prettier** - Code formatting
- **TypeScript** - Type checking

---

## Architecture Principles

### 1. Mobile-First Responsive Design

Design for mobile screens first, then progressively enhance for larger screens.

**Breakpoints:**

- Mobile: `< 768px` (default)
- Tablet: `768px - 1024px` (md)
- Desktop: `> 1024px` (lg+)

**Approach:**

- Single column on mobile
- Multi-column grids on desktop
- Touch-friendly targets (44×44px minimum)
- Simplified navigation on mobile (hamburger menu, bottom tabs)

### 2. Accessibility First

WCAG 2.1 Level AA compliance is mandatory, not optional.

**Requirements:**

- Semantic HTML elements (`<nav>`, `<main>`, `<article>`, etc.)
- Proper ARIA attributes for complex widgets
- Keyboard navigation for all interactions
- Focus management (modals, dropdowns)
- Screen reader announcements for dynamic content
- Color contrast ratios (4.5:1 for text, 3:1 for UI components)
- Alternative text for images
- Error messages associated with form fields

**Tools:**

- jest-axe for automated accessibility testing
- Manual testing with VoiceOver (Mac) or NVDA (Windows)

### 3. State Management Strategy

Use the right tool for the right type of state.

**@tanstack/vue-query (Server State):**

- All API data fetching
- Caching and background refetching
- Pagination and infinite scroll
- Optimistic updates

**Pinia (Client State):**

- Authentication (token, current user)
- User preferences (language, theme, items per page)
- Global UI state (sidebar open/closed, active filters)

**Component Local State:**

- Form input values (before submission)
- Modal open/closed state
- Accordion expanded/collapsed
- Tooltip visibility

**Decision Tree:**

```
Is this data from the API?
├── YES → Use @tanstack/vue-query
└── NO → Is this state needed across multiple unrelated components?
    ├── YES → Use Pinia store
    └── NO → Use component local state (ref/reactive)
```

### 4. Component Design Principles

**Composition over Inheritance:**

- Build complex components from smaller, reusable pieces
- Use slots for customization
- Emit events for parent communication

**TypeScript Interfaces:**

- Define clear prop interfaces
- Type all emitted events
- Export types for reusability

**Accessibility Built-In:**

- Every component meets WCAG 2.1 AA
- Not an afterthought or addon

**Internationalization Ready:**

- All user-facing text uses vue-i18n
- No hardcoded strings
- Support for German (default) and English

### 5. API Integration Pattern

All API calls follow a consistent pattern using @tanstack/vue-query.

**Query Pattern:**

```typescript
// composables/usePosts.ts
import { useQuery } from '@tanstack/vue-query'

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
```

**Mutation Pattern:**

```typescript
export function useCreatePost() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (postData: PostCreateRequest) => api.post('/api/v2/posts', postData),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['posts'] })
    },
  })
}
```

**Usage in Component:**

```vue
<script setup lang="ts">
import { usePosts, useCreatePost } from '@/composables/usePosts'

const { data: posts, isLoading, error } = usePosts(params)
const { mutate: createPost, isPending } = useCreatePost()
</script>
```

---

## Development Workflow

### Setting Up the Project

```bash
# Create Vue 3 project
npm create vue@latest bibsonomy-frontend
# Select: TypeScript, Router, Pinia, Vitest, ESLint, Prettier

cd bibsonomy-frontend

# Install dependencies
npm install @tanstack/vue-query @headlessui/vue @heroicons/vue vue-i18n zod

# Install dev dependencies
npm install -D @testing-library/vue jest-axe tailwindcss postcss autoprefixer

# Initialize Tailwind
npx tailwindcss init -p
```

### Project Structure

```
bibsonomy-frontend/
├── public/
│   └── locales/          # i18n translation files
│       ├── en/
│       │   └── translation.json
│       └── de/
│           └── translation.json
├── src/
│   ├── assets/           # Static assets
│   ├── components/
│   │   ├── ui/           # Base UI components
│   │   ├── layout/       # Layout components
│   │   ├── domain/       # Domain-specific components
│   │   ├── forms/        # Form components
│   │   └── navigation/   # Navigation components
│   ├── composables/      # Vue composables
│   │   ├── useApi.ts     # API client setup
│   │   ├── usePosts.ts   # Post-related queries
│   │   ├── useUsers.ts   # User-related queries
│   │   ├── useGroups.ts  # Group-related queries
│   │   └── useAuth.ts    # Auth helpers
│   ├── pages/            # Page components (route views)
│   │   ├── HomePage.vue
│   │   ├── PostListPage.vue
│   │   ├── PostDetailPage.vue
│   │   └── ...
│   ├── router/
│   │   ├── index.ts      # Route definitions
│   │   └── guards.ts     # Route guards
│   ├── stores/           # Pinia stores
│   │   ├── auth.ts
│   │   ├── preferences.ts
│   │   └── ui.ts
│   ├── plugins/          # Vue plugins
│   │   ├── i18n.ts       # vue-i18n config
│   │   └── query.ts      # vue-query client
│   ├── types/            # TypeScript types
│   │   ├── models.ts     # Domain models
│   │   ├── api.ts        # API types
│   │   └── components.ts # Component types
│   ├── utils/            # Utility functions
│   ├── App.vue
│   └── main.ts
├── docs/                 # Design documentation (this folder)
│   ├── FRONTEND_DESIGN.md
│   ├── DESIGN_SYSTEM.md
│   ├── INFORMATION_ARCHITECTURE.md
│   ├── COMPONENT_LIBRARY.md
│   └── pages/
├── tailwind.config.js
├── vite.config.ts
├── tsconfig.json
└── package.json
```

### Development Commands

```bash
# Development server
npm run dev

# Type check
npm run type-check

# Lint
npm run lint

# Format
npm run format

# Test
npm run test

# Test with coverage
npm run test:coverage

# Build for production
npm run build

# Preview production build
npm run preview
```

---

## Implementation Roadmap

### Phase 1: Foundation (Weeks 1-2)

**Goals:** Set up project structure, design system, and base components.

**Tasks:**

1. ✅ Create project structure
2. ✅ Configure Tailwind with design tokens
3. ✅ Set up vue-i18n with German/English translations
4. ✅ Implement base UI components (Button, Input, Select, Modal, etc.)
5. ✅ Implement layout components (AppHeader, AppFooter, Container, etc.)
6. ✅ Set up vue-query and Pinia stores
7. ✅ Configure route guards
8. ✅ Implement dark mode toggle

**Deliverables:**

- Working component library
- Storybook (optional) for component documentation
- Functional dark mode
- Responsive layout system

### Phase 2: Core Pages (Weeks 3-4)

**Goals:** Implement core browsing and viewing functionality.

**Tasks:**

1. ✅ HomePage (landing page)
2. ✅ PostListPage (browse posts with filters)
3. ✅ PostDetailPage (view single post)
4. ✅ UserProfilePage (view user profile)
5. ✅ GroupListPage (browse groups)
6. ✅ GroupDetailPage (view group)
7. ✅ TagCloudPage (browse tags)
8. ✅ SearchPage (search posts/users/groups)

**Deliverables:**

- Public browsing experience functional
- Search and filtering working
- Responsive on all devices
- Accessible (WCAG 2.1 AA)

### Phase 3: User Actions (Weeks 5-6)

**Goals:** Implement authenticated user functionality.

**Tasks:**

1. ✅ LoginPage + RegisterPage
2. ✅ PostEditPage (create/edit posts - both bookmarks and publications)
3. ✅ UserSettingsPage (profile, preferences, API keys, account)
4. ✅ GroupManagement pages (create, edit, members, invitations)
5. ✅ ImportPages (BibTeX, DOI, URL, PDF, bookmarks, EndNote)
6. ✅ Document upload/management

**Deliverables:**

- Full authenticated user experience
- Post creation/editing working
- Import workflows functional
- Group management complete

### Phase 4: Community Features (Weeks 7-8)

**Goals:** Implement community and social features.

**Tasks:**

1. ✅ CommunityPostsPage (Gold Standard posts)
2. ✅ Reference/Part-of relations (for community posts)
3. ✅ Notifications system
4. ✅ Group invitations and join requests
5. ✅ Export functionality (BibTeX, CSV, RSS, etc.)

**Deliverables:**

- Community features functional
- Notifications working
- Export in all formats

### Phase 5: Admin & Polish (Weeks 9-10)

**Goals:** Admin interface and final polish.

**Tasks:**

1. ✅ Admin dashboard
2. ✅ User management (admin)
3. ✅ Group management (admin)
4. ✅ Post moderation (admin)
5. ✅ Performance optimization
6. ✅ Accessibility audit and fixes
7. ✅ Cross-browser testing
8. ✅ Final UX polish

**Deliverables:**

- Admin functionality complete
- Performance optimized
- Fully accessible
- Production-ready

---

## Design Decisions & Rationale

### Why Tailwind CSS?

**Pros:**

- Rapid prototyping with utility classes
- Consistent design system through configuration
- Small production bundle (PurgeCSS)
- Great developer experience with IntelliSense
- Easy dark mode implementation

**Cons:**

- HTML can get verbose
- Learning curve for team members

**Decision:** Use Tailwind for consistency and speed. Extract repeated patterns into components.

### Why Headless UI?

**Pros:**

- Fully accessible out of the box
- Unstyled (perfect with Tailwind)
- Maintained by Tailwind Labs
- TypeScript support

**Cons:**

- Limited component selection
- Requires custom styling

**Decision:** Use Headless UI for complex interactive components (Modal, Dropdown, Tabs, etc.).

### Why @tanstack/vue-query?

**Pros:**

- Best-in-class server state management
- Automatic caching and refetching
- Built-in loading/error states
- Optimistic updates
- Pagination and infinite scroll support

**Cons:**

- Learning curve
- Adds bundle size

**Decision:** Essential for modern data fetching. The benefits far outweigh the costs.

### Why Pinia over Vuex?

**Pros:**

- Simpler API
- Full TypeScript support
- Composition API style
- Smaller bundle size
- Official recommendation for Vue 3

**Decision:** Pinia is the modern standard for Vue 3.

### Why vue-i18n over Custom Solution?

**Pros:**

- Industry standard for Vue i18n
- Rich feature set (pluralization, interpolation)
- TypeScript support
- Small bundle size

**Decision:** Proven solution with great DX.

---

## Accessibility Checklist

Before marking any page as "complete," ensure:

- [ ] All interactive elements keyboard accessible
- [ ] Focus visible on all focusable elements
- [ ] Logical tab order (left-to-right, top-to-bottom)
- [ ] Skip to main content link present
- [ ] Headings properly nested (h1 → h2 → h3, no skipping)
- [ ] Images have alt text
- [ ] Form labels associated with inputs
- [ ] Error messages linked to form fields via `aria-describedby`
- [ ] Color contrast ratios meet WCAG 2.1 AA (4.5:1 for text, 3:1 for UI)
- [ ] No reliance on color alone for information
- [ ] ARIA labels on icon-only buttons
- [ ] Modals trap focus and return focus on close
- [ ] `aria-live` regions for dynamic content
- [ ] `prefers-reduced-motion` respected
- [ ] Tested with screen reader (VoiceOver or NVDA)
- [ ] Tested with keyboard only (no mouse)
- [ ] Passed axe DevTools audit

---

## i18n Checklist

Before marking any page as "complete," ensure:

- [ ] All user-facing text uses `{{ t('key') }}`
- [ ] No hardcoded strings in templates
- [ ] Translation keys follow naming convention (`category.subcategory.key`)
- [ ] English and German translations provided
- [ ] Pluralization handled correctly (e.g., "1 post" vs "2 posts")
- [ ] Interpolation used for dynamic content (e.g., "Welcome, {username}")
- [ ] Language switcher updates locale and persists preference
- [ ] Page titles and meta descriptions translated

---

## Testing Strategy

### Unit Tests

**What to Test:**

- Utility functions (pure functions)
- Composables (API integration)
- Store actions (state mutations)

**Tools:** Vitest

### Component Tests

**What to Test:**

- Component rendering with props
- User interactions (clicks, inputs)
- Events emitted
- Conditional rendering

**Tools:** @testing-library/vue, Vitest

### Integration Tests

**What to Test:**

- User flows (register → login → create post)
- API integration (using MSW for mocking)
- Route navigation

**Tools:** @testing-library/vue, Vitest, MSW

### Accessibility Tests

**What to Test:**

- Automated WCAG checks
- Keyboard navigation
- Screen reader announcements

**Tools:** jest-axe, manual testing

### E2E Tests (Optional)

**What to Test:**

- Critical user paths
- Cross-browser compatibility

**Tools:** Playwright or Cypress

---

## Deployment

### Build Configuration

```bash
# Production build
npm run build

# Output: dist/
```

### Environment Variables

```env
# .env.production
VITE_API_BASE_URL=https://api.bibsonomy.org
VITE_APP_NAME=BibSonomy
```

### Deployment Targets

- **Static Hosting:** Netlify, Vercel, Cloudflare Pages
- **Server:** Nginx, Apache
- **CDN:** Cloudflare, AWS CloudFront

### Performance Targets

- **First Contentful Paint (FCP):** < 1.5s
- **Largest Contentful Paint (LCP):** < 2.5s
- **Time to Interactive (TTI):** < 3.5s
- **Cumulative Layout Shift (CLS):** < 0.1
- **First Input Delay (FID):** < 100ms

---

## Resources

### Documentation

- [Vue 3 Documentation](https://vuejs.org/)
- [Tailwind CSS Documentation](https://tailwindcss.com/)
- [Headless UI Documentation](https://headlessui.com/)
- [vue-query Documentation](https://tanstack.com/query/latest/docs/vue/overview)
- [Pinia Documentation](https://pinia.vuejs.org/)
- [vue-i18n Documentation](https://vue-i18n.intlify.dev/)
- [WCAG 2.1 Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)

### Tools

- [Heroicons](https://heroicons.com/) - Icon library
- [Tailwind UI](https://tailwindui.com/) - Component examples (paid)
- [WebAIM Contrast Checker](https://webaim.org/resources/contrastchecker/)
- [axe DevTools](https://www.deque.com/axe/devtools/) - Accessibility testing
- [MSW](https://mswjs.io/) - API mocking for tests

### Learning Resources

- [Vue 3 Composition API Guide](https://vuejs.org/guide/extras/composition-api-faq.html)
- [TypeScript + Vue](https://vuejs.org/guide/typescript/overview.html)
- [Tailwind CSS Tutorial](https://tailwindcss.com/docs/utility-first)
- [Accessible Components](https://inclusive-components.design/)
- [A11y Project](https://www.a11yproject.com/)

---

## Support & Contribution

### Getting Help

If you have questions about the frontend design:

1. Check the relevant documentation section
2. Review the component library specifications
3. Look at page specifications for UX patterns
4. Ask in the team chat/Slack

### Contributing to Documentation

To update this documentation:

1. Edit the relevant markdown file
2. Ensure examples are accurate
3. Update the version history
4. Submit for review

### Reporting Issues

If you find issues with the design specifications:

1. Check if already reported
2. Create detailed issue with:
   - Which document/section
   - Description of issue
   - Suggested improvement
3. Label appropriately (design, documentation, accessibility, etc.)

---

## Version History

| Version | Date       | Changes                               |
| ------- | ---------- | ------------------------------------- |
| 1.0     | 2025-12-15 | Initial frontend design specification |

---

## Next Steps

### For Developers

1. **Review Design System** - Understand colors, typography, spacing
2. **Review Information Architecture** - Understand routes and navigation
3. **Review Component Library** - Understand available components
4. **Start with Phase 1** - Set up project and implement base components
5. **Build Page by Page** - Reference individual page specs

### For Designers

1. **Review Design System** - Visual language reference
2. **Create High-Fidelity Mockups** - Based on specifications (optional)
3. **Design New Features** - Following established patterns
4. **Collaborate with Developers** - Refine specifications

### For Product Managers

1. **Review User Flows** - Understand user journeys
2. **Prioritize Features** - Based on API availability
3. **Define Acceptance Criteria** - Using page specifications
4. **Plan Releases** - Following implementation roadmap

---

## Conclusion

This frontend design documentation provides a comprehensive blueprint for building a modern, accessible, and maintainable Vue 3 application for BibSonomy. By following these specifications, we ensure:

✅ **Consistency** - Unified design language and component library
✅ **Accessibility** - WCAG 2.1 AA compliance throughout
✅ **Maintainability** - Clear patterns and TypeScript types
✅ **Performance** - Optimized for speed and responsiveness
✅ **Internationalization** - German and English support
✅ **Developer Experience** - Modern tooling and clear documentation

The design preserves the core functionality of BibSonomy while transforming the UX from a 2010-era application to a contemporary, polished research tool that users will enjoy using.

Happy building! 🚀

---

**Maintained by:** BibSonomy Frontend Team
**Last Review:** 2025-12-15
