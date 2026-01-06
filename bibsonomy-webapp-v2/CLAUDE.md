# Frontend (Vue 3) - Development Guide

This guide is specific to the **bibsonomy-webapp-v2** module - the modern Vue 3 frontend for BibSonomy.

> **Parent guide**: See `../CLAUDE.md` for project-wide guidance and architecture overview.

## Module Overview

**bibsonomy-webapp-v2** is a modern single-page application (SPA) built with Vue 3, TypeScript, and Vite. It consumes the REST API v2 and provides a responsive, accessible user interface.

**Tech Stack**:

- **Vue 3** (Composition API + SFC)
- **TypeScript** (strict mode)
- **Vite** (build tool and dev server)
- **@tanstack/vue-query** (server state management)
- **Pinia** (client/UI state - auth, prefs)
- **vue-router** (client-side routing)
- **vue-i18n** (German/English translations)
- **Tailwind CSS** (styling with utility classes)
- **Zod** (runtime type validation)
- **Vitest** (testing framework)

**Package Manager**: Bun (modern, faster) - can use npm if preferred

## Build & Run

### Prerequisites

- **Node.js 18+** (for npm) OR **Bun 1.0+** (recommended)
- REST API v2 running at `http://localhost:8080` (see `../bibsonomy-rest-api-v2/CLAUDE.md`)

### Development Server

```bash
# Install dependencies (first time only)
bun install
# or: npm install

# Run development server
bun run dev
# or: npm run dev

# Opens at: http://localhost:5173
```

The dev server includes:

- Hot Module Replacement (HMR)
- TypeScript type checking
- Auto-reload on file changes

### Build for Production

```bash
# Build optimized production bundle
bun run build
# or: npm run build

# Output: dist/

# Preview production build locally
bun run preview
# or: npm run preview
```

### Other Commands

```bash
# Type checking (without building)
bun run type-check
# or: npm run type-check

# Linting
bun run lint
# or: npm run lint

# Format code
bun run format
# or: npm run format

# Run tests
bun test
# or: npm test
```

## Project Structure

```
bibsonomy-webapp-v2/
├── public/
│   └── locales/              # i18n translation JSON files
│       ├── en/
│       │   └── translation.json
│       └── de/
│           └── translation.json
├── src/
│   ├── components/           # Vue components
│   │   ├── ui/               # Reusable UI primitives (buttons, modals, etc.)
│   │   ├── posts/            # Post-related components
│   │   ├── users/            # User-related components
│   │   └── layout/           # Layout components (header, nav, footer)
│   ├── pages/                # Page-level components (route targets)
│   │   ├── HomePage.vue
│   │   ├── PostDetailPage.vue
│   │   └── UserProfilePage.vue
│   ├── composables/          # Composition API composables
│   │   ├── api/              # API-related composables (vue-query)
│   │   │   ├── usePosts.ts
│   │   │   └── useUsers.ts
│   │   └── useAuth.ts        # Auth helpers (Pinia integration)
│   ├── stores/               # Pinia stores
│   │   └── auth.ts           # Auth state (user, token)
│   ├── router/               # Vue Router configuration
│   │   └── index.ts
│   ├── plugins/              # Vue plugins
│   │   ├── i18n.ts           # vue-i18n setup
│   │   └── query.ts          # vue-query client setup
│   ├── types/                # TypeScript types and Zod schemas
│   │   ├── api.ts            # API response types
│   │   └── schemas.ts        # Zod validation schemas
│   ├── utils/                # Utility functions
│   │   └── date.ts           # Date formatting, etc.
│   ├── App.vue               # Root component
│   └── main.ts               # Application entry point
├── index.html                # HTML entry point
├── vite.config.ts            # Vite configuration
├── tsconfig.json             # TypeScript configuration
├── tailwind.config.js        # Tailwind CSS configuration
└── package.json              # Dependencies and scripts
```

## Architecture & Patterns

### Component Architecture

```
┌─────────────────────────────────────────┐
│ Pages (Route Targets)                   │  pages/*.vue
│ - Orchestrate data fetching             │  - Use composables
│ - Compose multiple components           │  - Handle route params
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│ Feature Components                      │  components/posts/, etc.
│ - Business logic components             │  - PostList.vue
│ - Domain-specific UI                    │  - UserCard.vue
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│ UI Components                           │  components/ui/
│ - Reusable primitives                   │  - Button.vue
│ - No business logic                     │  - Modal.vue
└─────────────────────────────────────────┘
```

### Data Flow

```
┌─────────────────────────────────────────┐
│ Component (Setup Script)                │
│ const { data } = usePosts()             │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│ Composable (vue-query)                  │  composables/api/usePosts.ts
│ - useQuery() / useMutation()            │  - Caching & deduplication
│ - Calls API client                      │  - Automatic retries
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│ REST API v2                             │  http://localhost:8080/api/v2
│ - Returns JSON DTOs                     │  - Validated with Zod
└─────────────────────────────────────────┘
```

## Core Patterns

### 1. API Integration with vue-query

**Always use `@tanstack/vue-query`** for server state (API data).

```typescript
// composables/api/usePosts.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import type { PostDto, CreatePostRequest } from '@/types/api'
import { PostDtoSchema } from '@/types/schemas'

// Fetch posts list
export function usePosts(userName?: string, tags?: string[]) {
  return useQuery({
    queryKey: ['posts', { userName, tags }],
    queryFn: async () => {
      const params = new URLSearchParams()
      if (userName) params.append('userName', userName)
      tags?.forEach((tag) => params.append('tags', tag))

      const response = await fetch(`/api/v2/posts?${params}`)
      if (!response.ok) {
        throw new Error(`Failed to fetch posts: ${response.statusText}`)
      }

      const data = await response.json()

      // Validate with Zod
      return PostDtoSchema.array().parse(data)
    },
  })
}

// Fetch single post
export function usePost(userName: string, resourceHash: string) {
  return useQuery({
    queryKey: ['posts', userName, resourceHash],
    queryFn: async () => {
      const response = await fetch(`/api/v2/posts/${userName}/${resourceHash}`)
      if (!response.ok) {
        if (response.status === 404) throw new Error('Post not found')
        throw new Error('Failed to fetch post')
      }

      const data = await response.json()
      return PostDtoSchema.parse(data)
    },
    retry: false, // Don't retry 404s
  })
}

// Create post mutation
export function useCreatePost() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (request: CreatePostRequest) => {
      const response = await fetch('/api/v2/posts', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(request),
      })

      if (!response.ok) throw new Error('Failed to create post')

      const data = await response.json()
      return PostDtoSchema.parse(data)
    },
    onSuccess: () => {
      // Invalidate and refetch posts list
      queryClient.invalidateQueries({ queryKey: ['posts'] })
    },
  })
}
```

**Usage in component**:

```vue
<script setup lang="ts">
import { usePosts, useCreatePost } from '@/composables/api/usePosts'
import { ref } from 'vue'

const userName = ref<string>()
const tags = ref<string[]>([])

// Query
const { data: posts, isLoading, error } = usePosts(userName.value, tags.value)

// Mutation
const { mutate: createPost, isPending } = useCreatePost()

function handleCreatePost() {
  createPost({
    title: 'New Post',
    url: 'https://example.com',
    tags: ['example'],
  })
}
</script>

<template>
  <div>
    <div v-if="isLoading">Loading...</div>
    <div v-else-if="error">Error: {{ error.message }}</div>
    <PostList v-else :posts="posts" />

    <button @click="handleCreatePost" :disabled="isPending">Create Post</button>
  </div>
</template>
```

### 2. Pinia for Client/UI State

**Only use Pinia for client-side state** (auth, UI preferences, etc.). Server data goes in vue-query.

```typescript
// stores/auth.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  // State
  const token = ref<string | null>(localStorage.getItem('token'))
  const userName = ref<string | null>(localStorage.getItem('userName'))

  // Getters
  const isAuthenticated = computed(() => !!token.value)

  // Actions
  function login(newToken: string, newUserName: string) {
    token.value = newToken
    userName.value = newUserName
    localStorage.setItem('token', newToken)
    localStorage.setItem('userName', newUserName)
  }

  function logout() {
    token.value = null
    userName.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userName')
  }

  return {
    // State
    token,
    userName,
    // Getters
    isAuthenticated,
    // Actions
    login,
    logout,
  }
})
```

**Usage**:

```vue
<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
</script>

<template>
  <div v-if="authStore.isAuthenticated">
    <p>Welcome, {{ authStore.userName }}!</p>
    <button @click="authStore.logout">Logout</button>
  </div>
  <div v-else>
    <a href="/login">Login</a>
  </div>
</template>
```

### 3. Internationalization (i18n)

**Always use `vue-i18n`** for translatable text. Never hardcode user-facing strings.

**Translation files**: `public/locales/{lang}/translation.json`

```json
// public/locales/en/translation.json
{
  "nav": {
    "home": "Home",
    "posts": "Posts",
    "profile": "Profile"
  },
  "post": {
    "title": "Title",
    "url": "URL",
    "tags": "Tags",
    "created": "Created",
    "createNew": "Create Post",
    "deleteConfirm": "Are you sure you want to delete this post?"
  }
}

// public/locales/de/translation.json
{
  "nav": {
    "home": "Startseite",
    "posts": "Beiträge",
    "profile": "Profil"
  },
  "post": {
    "title": "Titel",
    "url": "URL",
    "tags": "Tags",
    "created": "Erstellt",
    "createNew": "Beitrag erstellen",
    "deleteConfirm": "Möchten Sie diesen Beitrag wirklich löschen?"
  }
}
```

**Usage in component**:

```vue
<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()

function switchLanguage(lang: 'en' | 'de') {
  locale.value = lang
}
</script>

<template>
  <nav>
    <a href="/">{{ t('nav.home') }}</a>
    <a href="/posts">{{ t('nav.posts') }}</a>
  </nav>

  <button @click="switchLanguage('en')">English</button>
  <button @click="switchLanguage('de')">Deutsch</button>

  <h1>{{ t('post.createNew') }}</h1>
</template>
```

### 4. Type Safety with Zod

**Validate all API responses** with Zod schemas.

```typescript
// types/schemas.ts
import { z } from 'zod'

export const PostDtoSchema = z.object({
  id: z.string(),
  title: z.string(),
  url: z.string().url().nullable(),
  description: z.string().nullable(),
  tags: z.array(z.string()),
  userName: z.string(),
  created: z.string().datetime(), // ISO 8601 string
})

export const PostListDtoSchema = z.object({
  posts: z.array(PostDtoSchema),
  total: z.number(),
})

export type PostDto = z.infer<typeof PostDtoSchema>
export type PostListDto = z.infer<typeof PostListDtoSchema>
```

**Usage**:

```typescript
// composables/api/usePosts.ts
import { PostDtoSchema } from '@/types/schemas'

export function usePosts() {
  return useQuery({
    queryKey: ['posts'],
    queryFn: async () => {
      const response = await fetch('/api/v2/posts')
      const data = await response.json()

      // Validate - throws ZodError if invalid
      return PostDtoSchema.array().parse(data)
    },
  })
}
```

### 5. Component Composition Pattern

**Prefer composition over prop drilling**.

```vue
<!-- ❌ BAD - Prop drilling -->
<UserProfile :user="user" :is-loading="isLoading" :on-update="handleUpdate" />

<!-- ✅ GOOD - Let component fetch its own data -->
<UserProfile :user-name="userName" />
```

**Component with composable**:

```vue
<!-- components/users/UserProfile.vue -->
<script setup lang="ts">
import { useUser } from '@/composables/api/useUsers'

const props = defineProps<{
  userName: string
}>()

const { data: user, isLoading } = useUser(props.userName)
</script>

<template>
  <div v-if="isLoading">Loading...</div>
  <div v-else-if="user">
    <h1>{{ user.name }}</h1>
    <p>{{ user.email }}</p>
  </div>
</template>
```

## Styling with Tailwind CSS

**Use Tailwind utility classes** for styling. Avoid custom CSS unless necessary.

```vue
<template>
  <div class="container mx-auto px-4 py-8">
    <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100">
      {{ t('post.title') }}
    </h1>

    <div class="mt-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <PostCard
        v-for="post in posts"
        :key="post.id"
        :post="post"
        class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-4 hover:shadow-lg transition-shadow"
      />
    </div>
  </div>
</template>
```

**Common patterns**:

```vue
<!-- Responsive layout -->
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">

<!-- Dark mode support -->
<div class="bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100">

<!-- Button -->
<button class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500">

<!-- Input -->
<input class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
```

## Accessibility (a11y)

**Always consider accessibility**:

```vue
<!-- ✅ Semantic HTML -->
<nav>
  <ul>
    <li><a href="/">Home</a></li>
  </ul>
</nav>

<!-- ✅ ARIA labels for icon buttons -->
<button aria-label="Delete post" @click="deletePost">
  <TrashIcon />
</button>

<!-- ✅ Focus management -->
<button class="focus:outline-none focus:ring-2 focus:ring-blue-500" @click="handleClick">
  Click me
</button>

<!-- ✅ Form labels -->
<label for="title" class="block mb-2">
  {{ t('post.title') }}
</label>
<input id="title" type="text" v-model="title" />

<!-- ✅ Loading/error states -->
<div role="status" aria-live="polite">
  <span v-if="isLoading">Loading...</span>
  <span v-else-if="error">Error: {{ error.message }}</span>
</div>
```

## Testing Strategy

**Keep tests minimal and focused on critical flows**.

### Integration Tests (Preferred)

Test user flows with real API calls (can use MSW to mock API).

```typescript
// tests/integration/posts.test.ts
import { mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import PostListPage from '@/pages/PostListPage.vue'

describe('PostListPage', () => {
  it('displays posts from API', async () => {
    const wrapper = mount(PostListPage)

    // Wait for data to load
    await wrapper.vm.$nextTick()
    await new Promise((resolve) => setTimeout(resolve, 100))

    // Check that posts are displayed
    expect(wrapper.findAll('.post-card').length).toBeGreaterThan(0)
  })

  it('shows error message when API fails', async () => {
    // Mock fetch to return error
    global.fetch = vi.fn(() => Promise.reject(new Error('API error')))

    const wrapper = mount(PostListPage)
    await wrapper.vm.$nextTick()

    expect(wrapper.text()).toContain('Error')
  })
})
```

### Component Tests (When Needed)

```typescript
// tests/components/PostCard.test.ts
import { mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import PostCard from '@/components/posts/PostCard.vue'

describe('PostCard', () => {
  it('renders post title and url', () => {
    const post = {
      id: '1',
      title: 'Test Post',
      url: 'https://example.com',
      tags: ['test'],
      userName: 'john',
      created: '2025-01-01T00:00:00Z',
    }

    const wrapper = mount(PostCard, {
      props: { post },
    })

    expect(wrapper.text()).toContain('Test Post')
    expect(wrapper.find('a').attributes('href')).toBe('https://example.com')
  })
})
```

### Test Coverage Goals

- ✅ Critical user flows (login, create post, view posts)
- ✅ Error handling (API failures, 404s)
- ✅ i18n (language switching works)
- ❌ **NOT** exhaustive unit test coverage
- ❌ **NO** snapshot tests (brittle, low value)

## Common Development Tasks

### Adding a New Page

1. **Create page component**: `src/pages/YourPage.vue`
2. **Add route**: `src/router/index.ts`
3. **Create necessary composables**: `src/composables/api/useYourData.ts`
4. **Add translations**: `public/locales/{en,de}/translation.json`
5. **Test manually** in browser

Example:

```typescript
// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import YourPage from '@/pages/YourPage.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/your-page',
      name: 'YourPage',
      component: YourPage,
    },
  ],
})
```

### Creating a Reusable Component

1. **Decide category**: UI primitive or feature component?
   - UI: `src/components/ui/YourComponent.vue`
   - Feature: `src/components/posts/YourComponent.vue`

2. **Define props with TypeScript**:

```vue
<script setup lang="ts">
interface Props {
  title: string
  description?: string
  onDelete?: () => void
}

const props = defineProps<Props>()
</script>

<template>
  <div>
    <h2>{{ props.title }}</h2>
    <p v-if="props.description">{{ props.description }}</p>
    <button v-if="props.onDelete" @click="props.onDelete">Delete</button>
  </div>
</template>
```

### Adding API Integration

1. **Define Zod schema**: `src/types/schemas.ts`
2. **Create composable**: `src/composables/api/useYourData.ts`
3. **Use in component**:

```vue
<script setup lang="ts">
import { useYourData } from '@/composables/api/useYourData'

const { data, isLoading, error } = useYourData()
</script>
```

### Debugging Tips

```typescript
// Enable vue-query devtools (automatically in dev mode)
// Access at bottom-left of page

// Console logging in composable
export function usePosts() {
  const query = useQuery({
    queryKey: ['posts'],
    queryFn: async () => {
      console.log('Fetching posts...')
      const data = await fetchPosts()
      console.log('Posts fetched:', data)
      return data
    },
  })

  console.log('Query state:', query.status.value, query.data.value)
  return query
}

// Vue DevTools
// Install browser extension: https://devtools.vuejs.org/
```

## Code Style & Best Practices

### TypeScript

```typescript
// ✅ Use strict TypeScript
interface User {
  name: string
  email: string
  age?: number // Optional with ?
}

// ✅ Type component props
defineProps<{
  user: User
  isEditable: boolean
}>()

// ✅ Type event emitters
const emit = defineEmits<{
  update: [user: User]
  delete: [userId: string]
}>()

// ❌ Avoid 'any'
const data: any = await fetch() // NO!
```

### Vue Composition API

```vue
<script setup lang="ts">
// ✅ Use Composition API with <script setup>
import { ref, computed, watch } from 'vue'

const count = ref(0)
const doubled = computed(() => count.value * 2)

watch(count, (newVal) => {
  console.log('Count changed:', newVal)
})

// ✅ Destructure only what you need
const { data, isLoading } = usePosts()

// ✅ Use composables for reusable logic
const { userName, logout } = useAuth()
</script>
```

### Component Structure

```vue
<script setup lang="ts">
// 1. Imports
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

// 2. Props & Emits
const props = defineProps<{ ... }>()
const emit = defineEmits<{ ... }>()

// 3. Composables
const { t } = useI18n()
const { data } = usePosts()

// 4. Local state
const isOpen = ref(false)

// 5. Computed
const filteredPosts = computed(() => ...)

// 6. Methods
function handleClick() { ... }

// 7. Lifecycle (if needed)
onMounted(() => { ... })
</script>

<template>
  <!-- Keep template clean and readable -->
</template>

<style scoped>
/* Minimal custom CSS - prefer Tailwind */
</style>
```

## Common Gotchas

1. **vue-query reactivity**: Use `.value` to access reactive data: `data.value`, `isLoading.value`
2. **i18n in script**: Use `t()` from `useI18n()`, not template-only `$t()`
3. **Route params**: Use `useRoute()` from vue-router, not `$route`
4. **Zod validation errors**: Always wrap in try-catch or handle in vue-query `onError`
5. **Dark mode**: Remember to add dark mode variants: `dark:bg-gray-800`
6. **API URL**: Vite proxies `/api/*` to backend (see `vite.config.ts`)

## Performance Tips

1. **Lazy load routes**: Use `() => import()` for route components
2. **Debounce search inputs**: Use `useDebouncedRef()` or similar
3. **Virtual scrolling**: For long lists, use virtual scrolling library
4. **Image optimization**: Use `<img loading="lazy">` for below-fold images
5. **Code splitting**: Vite automatically splits by route

## Configuration Files

### vite.config.ts

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // REST API v2
        changeOrigin: true,
      },
    },
  },
  resolve: {
    alias: {
      '@': '/src', // Use @/ for imports
    },
  },
})
```

### tsconfig.json

```json
{
  "compilerOptions": {
    "strict": true,
    "target": "ES2020",
    "module": "ESNext",
    "moduleResolution": "bundler",
    "jsx": "preserve",
    "paths": {
      "@/*": ["./src/*"]
    }
  }
}
```

## Additional Resources

- **Parent guide**: `../CLAUDE.md` - Project-wide architecture
- **REST API guide**: `../bibsonomy-rest-api-v2/CLAUDE.md` - Backend API documentation
- **Vue 3 docs**: https://vuejs.org/
- **@tanstack/vue-query**: https://tanstack.com/query/latest/docs/vue/overview
- **Pinia**: https://pinia.vuejs.org/
- **vue-i18n**: https://vue-i18n.intlify.dev/
- **Tailwind CSS**: https://tailwindcss.com/
- **Zod**: https://zod.dev/

---

**Remember**: Keep it simple. Use composables for API calls, Pinia for auth, and Tailwind for styling. Test critical flows, not every component. Translate everything with i18n.
