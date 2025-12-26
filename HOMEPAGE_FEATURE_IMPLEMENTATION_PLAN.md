# Homepage V2 - Critical Features Implementation Plan

This document outlines how to implement the critical missing features to align the V2 homepage with the legacy V1 homepage functionality.

**Excluded (not needed):**
- Side-by-side layout (✅ already done)
- Memento/Archive link (❌ deprecated)
- Blog/News posts (❌ out of scope)
- Edit tags inline (❌ out of scope)

## Implementation Status Summary

### ✅ Completed (Frontend-Only Features)
1. **Tag Icon Before Tags List** - Tag icon appears before tags
2. **Relative Time Display** - Shows "2 hours ago" with full datetime tooltip
3. **Login Button in Jumbotron** - Login button with i18n support
4. **Pagination** - Next/Prev navigation for filtered views
5. **Collection/Private/Group Badges** - Shows private and group badges on thumbnails

### ⏳ Pending (Requires Backend Changes)
1. **"Other People" Count Badge** - Needs `resourceUserCount` field in API DTOs
2. **Hidden/System Tags with Popover** - Needs `systemTags` field investigation
3. **Thumbnail Preview Images** - Needs thumbnail URL implementation

---

## 1. "Other People" Count Badge

### What It Does
Shows a badge with a number indicating how many other users have bookmarked/saved the same resource. In legacy: appears as a badge in the top-right of each post card.

### Current State
- **Backend**: ✅ Data available in domain model (`Resource.count` field)
- **API**: ⚠️ NOT exposed in REST API DTOs
- **Frontend**: ❌ Not displayed

### Backend Changes Needed

#### 1.1 Update DTOs (REST API v2)

**File**: `bibsonomy-rest-api-v2/src/main/kotlin/org/bibsonomy/api/dto/PostDto.kt`

Add `resourceUserCount` field to ResourceDto:

```kotlin
sealed interface ResourceDto {
    val resourceUserCount: Int?  // Add to interface
}

data class BookmarkDto(
    val url: String,
    val title: String,
    val urlHash: String?,
    override val resourceUserCount: Int? = null  // Add field
) : ResourceDto

data class BibTexDto(
    val bibtexKey: String?,
    val entryType: String,
    // ... other fields ...
    override val resourceUserCount: Int? = null  // Add field
) : ResourceDto
```

#### 1.2 Update Mapper

**File**: `bibsonomy-rest-api-v2/src/main/kotlin/org/bibsonomy/api/mapper/PostMapper.kt`

```kotlin
fun Bookmark.toDto(): BookmarkDto {
    return BookmarkDto(
        url = this.url ?: "",
        title = this.title ?: "",
        urlHash = this.interHash ?: this.intraHash,
        resourceUserCount = this.count  // Add mapping
    )
}

fun BibTex.toDto(): BibTexDto {
    return BibTexDto(
        // ... existing fields ...
        resourceUserCount = this.count  // Add mapping
    )
}
```

### Frontend Changes Needed

#### 1.3 Update TypeScript Models

**File**: `bibsonomy-webapp-v2/src/types/models.ts`

```typescript
export const BookmarkResourceSchema = z.object({
  resourceType: z.literal('bookmark'),
  url: z.string(),
  title: z.string(),
  urlHash: z.string().nullable().optional(),
  resourceUserCount: z.number().optional(),  // Add field
})

export const BibTexResourceSchema = z.object({
  resourceType: z.literal('bibtex'),
  // ... existing fields ...
  resourceUserCount: z.number().optional(),  // Add field
})
```

#### 1.4 Display Badge in PostCard

**File**: `bibsonomy-webapp-v2/src/components/post/PostCard.vue`

Add to template (in title section):

```vue
<template>
  <Card class="flex gap-3 md:gap-4 p-3 md:p-4 hover:shadow-md transition-shadow h-auto md:h-[180px]">
    <!-- ... existing content ... -->

    <div class="flex-1 min-w-0 flex flex-col">
      <!-- Title + Count Badge -->
      <div class="flex items-start justify-between gap-2 mb-1">
        <Link
          :href="`/error/not-implemented?feature=Post+Details`"
          class="text-sm md:text-base font-semibold line-clamp-2 flex-1"
          :class="{ 'font-bold': isPublication(post) }"
        >
          {{ getPostTitle(post) }}
        </Link>

        <!-- Other People Count Badge -->
        <Badge
          v-if="post.resource.resourceUserCount && post.resource.resourceUserCount > 1"
          variant="gray"
          size="sm"
          :title="`${post.resource.resourceUserCount} users have this resource`"
        >
          {{ post.resource.resourceUserCount }}
        </Badge>
      </div>

      <!-- ... rest of content ... -->
    </div>
  </Card>
</template>

<script setup lang="ts">
import Badge from '@/components/ui/Badge.vue'
// ... other imports ...
</script>
```

### Testing

1. **Backend**: Check OpenAPI docs show the new field
2. **API**: `curl http://localhost:8080/api/v2/posts | jq '.items[0].resource.resourceUserCount'`
3. **Frontend**: Hover over badge to see tooltip

---

## 2. Relative Time Display ("2 hours ago") ✅ COMPLETED

### What It Does
Shows "2 hours ago", "3 days ago" instead of "Jan 15, 2025". Legacy shows this as the primary date format with full timestamp in tooltip.

### Current State
- **Backend**: ✅ Data available (createdAt field)
- **API**: ✅ Exposed as ISO 8601 timestamp
- **Frontend**: ✅ IMPLEMENTED - Shows relative time with clock icon and full datetime tooltip

### Frontend Changes Needed

#### 2.1 Install Time Formatting Library

```bash
cd bibsonomy-webapp-v2
bun add date-fns
```

#### 2.2 Create Time Utility

**File**: `bibsonomy-webapp-v2/src/utils/date.ts` (NEW or UPDATE)

```typescript
import { formatDistanceToNow, format } from 'date-fns'

/**
 * Format date as relative time (e.g., "2 hours ago")
 */
export function formatRelativeTime(date: string | Date): string {
  const dateObj = typeof date === 'string' ? new Date(date) : date
  return formatDistanceToNow(dateObj, { addSuffix: true })
}

/**
 * Format full date and time for tooltips
 */
export function formatFullDateTime(date: string | Date): string {
  const dateObj = typeof date === 'string' ? new Date(date) : date
  return format(dateObj, 'PPpp') // e.g., "Apr 29, 2023, 11:30 AM"
}
```

#### 2.3 Update PostMeta Component

**File**: `bibsonomy-webapp-v2/src/components/post/PostMeta.vue`

```vue
<script setup lang="ts">
import { computed } from 'vue'
import { Clock } from 'lucide-vue-next'  // Add icon
import UserLink from '@/components/user/UserLink.vue'
import Badge from '@/components/ui/Badge.vue'
import { formatRelativeTime, formatFullDateTime } from '@/utils/date'
import type { Post } from '@/types/models'

interface Props {
  post: Post
}

const props = defineProps<Props>()

// Relative time for display
const relativeTime = computed(() => {
  if (!props.post.createdAt) return ''
  return formatRelativeTime(props.post.createdAt)
})

// Full timestamp for tooltip
const fullDateTime = computed(() => {
  if (!props.post.createdAt) return ''
  return formatFullDateTime(props.post.createdAt)
})

const groups = computed(() => props.post.groups || [])
</script>

<template>
  <div class="text-xs text-gray-600 flex items-center gap-2 flex-wrap">
    <!-- Clock Icon + Relative Time -->
    <span
      v-if="relativeTime"
      class="flex items-center gap-1"
      :title="fullDateTime"
    >
      <Clock :size="12" class="text-gray-500" />
      {{ relativeTime }}
    </span>

    <!-- User -->
    <span class="text-gray-500">by</span>
    <UserLink :user="post.user" />

    <!-- Groups -->
    <Badge
      v-for="group in groups"
      :key="group.name"
      size="xs"
      variant="secondary"
    >
      {{ group.name }}
    </Badge>
  </div>
</template>
```

### Testing

1. Create post and check it shows "X minutes ago"
2. Hover to see full timestamp tooltip
3. Wait and refresh - time should update

---

## 3. Tag Icon Before Tags List ✅ COMPLETED

### What It Does
Shows a small tag icon (🏷️) before the list of tags. Pure visual enhancement.

### Current State
- **Frontend**: ✅ IMPLEMENTED - Tag icon appears before tags list

### Frontend Changes Needed

#### 3.1 Update PostTags Component

**File**: `bibsonomy-webapp-v2/src/components/post/PostTags.vue`

```vue
<script setup lang="ts">
import { Tag as TagIcon } from 'lucide-vue-next'  // Import icon
import TagBadge from '@/components/tag/TagBadge.vue'
import type { Tag } from '@/types/models'

interface Props {
  tags: Tag[]
}

defineProps<Props>()
</script>

<template>
  <div v-if="tags.length > 0" class="flex items-center gap-1.5">
    <!-- Tag Icon -->
    <TagIcon :size="14" class="text-gray-500 flex-shrink-0" />

    <!-- Tags -->
    <div class="flex flex-wrap gap-1">
      <TagBadge v-for="tag in tags" :key="tag.name" :tag="tag" />
    </div>
  </div>
</template>
```

### Testing

1. Check tag icon appears before tags
2. Ensure icon doesn't break layout on mobile

---

## 4. Hidden/System Tags with Popover

### What It Does
Shows system tags (like `sys:imported`, `sys:bibsonomy`) in a collapsible popover behind an asterisk icon.

### Current State
- **Backend**: ✅ Data might be available (need to check if systemTags are exposed)
- **API**: ⚠️ Likely NOT exposed
- **Frontend**: ❌ Not implemented

### Backend Investigation Needed

**Check if this field exists in domain model:**

```bash
# Search for systemTags or hiddenSystemTags in Post.java
grep -n "systemTag" bibsonomy-model/src/main/java/org/bibsonomy/model/Post.java
```

**If available**, follow same pattern as "Other People Count":
1. Add to DTOs
2. Map in PostMapper
3. Update TypeScript schemas

### Frontend Changes Needed

#### 4.1 Update TypeScript Models (if backend exposes data)

**File**: `bibsonomy-webapp-v2/src/types/models.ts`

```typescript
export const PostSchema = z.object({
  id: z.number(),
  resource: ResourceSchema,
  description: z.string().optional(),
  user: UserSchema,
  groups: z.array(GroupSchema),
  tags: z.array(TagSchema),
  systemTags: z.array(TagSchema).optional(),  // Add field
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime().optional().nullable(),
  visibility: z.enum(['public', 'private', 'groups']),
})
```

#### 4.2 Create SystemTagsPopover Component

**File**: `bibsonomy-webapp-v2/src/components/post/SystemTagsPopover.vue` (NEW)

```vue
<script setup lang="ts">
import { ref } from 'vue'
import { Asterisk, X } from 'lucide-vue-next'
import TagBadge from '@/components/tag/TagBadge.vue'
import type { Tag } from '@/types/models'

interface Props {
  systemTags: Tag[]
}

defineProps<Props>()

const isOpen = ref(false)
</script>

<template>
  <div v-if="systemTags.length > 0" class="relative inline-block">
    <!-- Trigger Button -->
    <button
      @click="isOpen = !isOpen"
      class="text-gray-500 hover:text-gray-700 p-1"
      title="System tags"
    >
      <Asterisk :size="12" />
    </button>

    <!-- Popover -->
    <div
      v-if="isOpen"
      class="absolute left-0 top-full mt-1 z-50 bg-white border border-gray-300 rounded shadow-lg p-3 min-w-[200px]"
    >
      <div class="flex items-center justify-between mb-2">
        <span class="text-xs font-semibold text-gray-700">System Tags</span>
        <button @click="isOpen = false" class="text-gray-500 hover:text-gray-700">
          <X :size="14} />
        </button>
      </div>

      <div class="flex flex-wrap gap-1">
        <TagBadge
          v-for="tag in systemTags"
          :key="tag.name"
          :tag="tag"
          :clickable="false"
        />
      </div>
    </div>
  </div>
</template>
```

#### 4.3 Use in PostTags

**File**: `bibsonomy-webapp-v2/src/components/post/PostTags.vue`

```vue
<script setup lang="ts">
import { Tag as TagIcon } from 'lucide-vue-next'
import TagBadge from '@/components/tag/TagBadge.vue'
import SystemTagsPopover from './SystemTagsPopover.vue'
import type { Tag, Post } from '@/types/models'

interface Props {
  post: Post  // Change to accept full post instead of just tags
}

defineProps<Props>()
</script>

<template>
  <div v-if="post.tags.length > 0 || post.systemTags?.length" class="flex items-center gap-1.5">
    <TagIcon :size="14" class="text-gray-500 flex-shrink-0" />

    <!-- System Tags Popover -->
    <SystemTagsPopover
      v-if="post.systemTags?.length"
      :system-tags="post.systemTags"
    />

    <!-- Regular Tags -->
    <div class="flex flex-wrap gap-1">
      <TagBadge v-for="tag in post.tags" :key="tag.name" :tag="tag" />
    </div>
  </div>
</template>
```

### Testing

1. Check if systemTags appear in API response
2. Click asterisk icon to see popover
3. Verify close button works

---

## 5. Thumbnail Preview Images from Server

### What It Does
Shows actual thumbnail/preview images for bookmarks from server instead of emoji placeholders.

### Current State
- **Backend**: ⚠️ Preview service might exist (check `project.img` property)
- **API**: ❌ Not exposed
- **Frontend**: ❌ Shows emoji only

### Backend Investigation Needed

**Check legacy config:**

```bash
# Check if preview service URL is configured
grep -r "project.img" bibsonomy-webapp/src/main/webapp/WEB-INF/*.properties
```

**Legacy pattern**: `${project.img}preview/bookmark/${interHash}?preview=MEDIUM`

### Option A: Proxy Through REST API v2

Add thumbnail URL generation to DTO mapping.

**File**: `bibsonomy-rest-api-v2/src/main/kotlin/org/bibsonomy/api/mapper/PostMapper.kt`

```kotlin
fun Bookmark.toDto(): BookmarkDto {
    val thumbnailUrl = if (!this.interHash.isNullOrBlank()) {
        "/api/v2/thumbnails/bookmark/${this.interHash}"  // New endpoint
    } else null

    return BookmarkDto(
        url = this.url ?: "",
        title = this.title ?: "",
        urlHash = this.interHash ?: this.intraHash,
        thumbnailUrl = thumbnailUrl,  // Add field
        resourceUserCount = this.count
    )
}
```

**Create controller**: `ThumbnailController.kt` (proxy to legacy preview service)

### Option B: Direct URL in Frontend

If preview service URL is public, construct URL directly in frontend.

#### 5.1 Update TypeScript Models

**File**: `bibsonomy-webapp-v2/src/types/models.ts`

```typescript
export const BookmarkResourceSchema = z.object({
  resourceType: z.literal('bookmark'),
  url: z.string(),
  title: z.string(),
  urlHash: z.string().nullable().optional(),
  resourceUserCount: z.number().optional(),
  thumbnailUrl: z.string().optional(),  // Add field
})
```

#### 5.2 Update PostThumbnail Component

**File**: `bibsonomy-webapp-v2/src/components/post/PostThumbnail.vue`

```vue
<script setup lang="ts">
import { computed } from 'vue'
import { Bookmark, EyeOff } from 'lucide-vue-next'
import Badge from '@/components/ui/Badge.vue'
import type { Post } from '@/types/models'
import { isBookmark } from '@/types/models'

interface Props {
  post: Post
}

const props = defineProps<Props>()

// Get thumbnail URL if available
const thumbnailUrl = computed(() => {
  if (isBookmark(props.post) && props.post.resource.thumbnailUrl) {
    return props.post.resource.thumbnailUrl
  }
  return undefined
})

// ... rest of component ...
</script>

<template>
  <div class="post-thumbnail w-16 h-16 md:w-20 md:h-20 relative bg-gray-100 rounded overflow-hidden">
    <!-- Thumbnail image -->
    <img
      v-if="thumbnailUrl"
      :src="thumbnailUrl"
      :alt="`Preview for ${post.resource.title}`"
      class="w-full h-full object-cover"
      @error="(e) => (e.target as HTMLImageElement).style.display = 'none'"
    />

    <!-- Fallback icon -->
    <div
      v-if="!thumbnailUrl"
      class="w-full h-full flex items-center justify-center text-2xl"
    >
      {{ post.resource.resourceType === 'bookmark' ? '🔖' : '📄' }}
    </div>

    <!-- ... badges ... -->
  </div>
</template>
```

### Testing

1. Check if preview images load
2. Verify fallback to emoji if image fails
3. Test with different bookmark types

---

## 6. Collection/Private/Group Badges on Thumbnails ✅ COMPLETED

### What It Does
Shows visual indicators on thumbnails:
- 🔖 Bookmark icon: Post is in current user's collection (requires auth store - not yet implemented)
- 👁️‍🗨️ Eye-slash icon: Post is private
- Group indicators for group-only posts

### Current State
- **Backend**: ✅ Data available (`post.groups`, `post.user`, `post.visibility`)
- **API**: ✅ Exposed in DTOs
- **Frontend**: ✅ IMPLEMENTED - Shows private and group badges (collection badge pending auth implementation)

### Frontend Changes Needed

#### 6.1 Add Auth Context

**File**: `bibsonomy-webapp-v2/src/stores/auth.ts`

Ensure current username is available:

```typescript
export const useAuthStore = defineStore('auth', () => {
  const username = ref<string | null>(localStorage.getItem('username'))
  // ... rest of store ...

  return {
    username,
    // ... other exports ...
  }
})
```

#### 6.2 Update PostThumbnail Component

**File**: `bibsonomy-webapp-v2/src/components/post/PostThumbnail.vue`

```vue
<script setup lang="ts">
import { computed } from 'vue'
import { Bookmark as BookmarkIcon, EyeOff, Users } from 'lucide-vue-next'
import Badge from '@/components/ui/Badge.vue'
import { useAuthStore } from '@/stores/auth'
import type { Post } from '@/types/models'

interface Props {
  post: Post
}

const props = defineProps<Props>()
const authStore = useAuthStore()

// Check if post is in current user's collection
const inMyCollection = computed(() => {
  return authStore.username && props.post.user.username === authStore.username
})

// Check if post is private
const isPrivate = computed(() => {
  return props.post.visibility === 'private' ||
    props.post.groups.some((g) => g.name === 'private')
})

// Check if post is in specific groups
const isInGroups = computed(() => {
  return props.post.visibility === 'groups' ||
    props.post.groups.some((g) => g.name !== 'public' && g.name !== 'private')
})

// ... rest of component ...
</script>

<template>
  <div class="post-thumbnail w-16 h-16 md:w-20 md:h-20 relative bg-gray-100 rounded overflow-hidden">
    <!-- ... thumbnail image ... -->

    <!-- Overlay badges (top-right corner) -->
    <div class="absolute top-0.5 right-0.5 flex flex-col gap-0.5">
      <!-- In My Collection -->
      <div
        v-if="inMyCollection"
        class="bg-blue-500 text-white rounded-full p-0.5"
        title="In my collection"
      >
        <BookmarkIcon :size="10" fill="currentColor" />
      </div>

      <!-- Private -->
      <div
        v-if="isPrivate"
        class="bg-gray-700 text-white rounded-full p-0.5"
        title="Private"
      >
        <EyeOff :size="10" />
      </div>

      <!-- In Groups -->
      <div
        v-if="isInGroups"
        class="bg-green-600 text-white rounded-full p-0.5"
        title="Shared with groups"
      >
        <Users :size="10" />
      </div>
    </div>
  </div>
</template>
```

### Testing

1. Login and check own posts show bookmark icon
2. Create private post and check eye-slash appears
3. Share post with group and check group icon

---

## 7. Pagination (Next/Prev Navigation) ✅ COMPLETED

### What It Does
Shows "Next" and "Previous" buttons at bottom of post lists to navigate through pages.

### Current State
- **Backend**: ✅ Data available (totalCount, offset, limit in API)
- **API**: ✅ Exposed in PaginatedResponse
- **Frontend**: ✅ IMPLEMENTED - Pagination appears when viewing filtered sections (bookmarks or publications only)

### Frontend Changes Needed

#### 7.1 Create Pagination Component

**File**: `bibsonomy-webapp-v2/src/components/ui/Pagination.vue` (NEW)

```vue
<script setup lang="ts">
import { computed } from 'vue'
import { ChevronLeft, ChevronRight } from 'lucide-vue-next'
import Button from './Button.vue'

interface Props {
  totalCount: number
  offset: number
  limit: number
}

const props = defineProps<Props>()

const emit = defineEmits<{
  prev: []
  next: []
}>()

const currentPage = computed(() => Math.floor(props.offset / props.limit) + 1)
const totalPages = computed(() => Math.ceil(props.totalCount / props.limit))
const hasPrev = computed(() => props.offset > 0)
const hasNext = computed(() => props.offset + props.limit < props.totalCount)
</script>

<template>
  <div v-if="totalPages > 1" class="flex items-center justify-between py-4 border-t border-gray-200">
    <!-- Previous Button -->
    <Button
      variant="secondary"
      :disabled="!hasPrev"
      @click="emit('prev')"
    >
      <ChevronLeft :size="16" />
      Previous
    </Button>

    <!-- Page Info -->
    <span class="text-sm text-gray-600">
      Page {{ currentPage }} of {{ totalPages }}
      ({{ totalCount }} total)
    </span>

    <!-- Next Button -->
    <Button
      variant="secondary"
      :disabled="!hasNext"
      @click="emit('next')"
    >
      Next
      <ChevronRight :size="16" />
    </Button>
  </div>
</template>
```

#### 7.2 Use in HomePage

**File**: `bibsonomy-webapp-v2/src/pages/HomePage.vue`

```vue
<script setup lang="ts">
import { ref, computed } from 'vue'
import { usePosts } from '@/composables/usePosts'
import Pagination from '@/components/ui/Pagination.vue'
// ... other imports ...

// Pagination state
const filters = ref({
  limit: 10,
  offset: 0
})

const { data, isLoading } = usePosts(filters)

// Pagination handlers
function handlePrev() {
  filters.value.offset = Math.max(0, filters.value.offset - filters.value.limit)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function handleNext() {
  filters.value.offset += filters.value.limit
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// ... rest of component ...
</script>

<template>
  <MainLayout>
    <PageContainer>
      <!-- ... existing content ... -->

      <div class="flex flex-col lg:flex-row -mx-4">
        <div class="w-full lg:flex-[0_0_75%] lg:max-w-[75%] px-4">
          <!-- Posts sections -->
          <!-- ... -->

          <!-- Pagination -->
          <Pagination
            v-if="data"
            :total-count="data.totalCount"
            :offset="data.offset"
            :limit="data.limit"
            @prev="handlePrev"
            @next="handleNext"
          />
        </div>

        <!-- Sidebar -->
        <!-- ... -->
      </div>
    </PageContainer>
  </MainLayout>
</template>
```

### Testing

1. Load homepage with more than 10 posts
2. Click "Next" and verify new posts load
3. Click "Previous" and verify return to first page
4. Check disabled states work correctly

---

## 8. Login Button in Jumbotron ✅ COMPLETED

### What It Does
Shows a "Login" button in the jumbotron for non-authenticated users.

### Current State
- **Frontend**: ✅ IMPLEMENTED - Login button added to jumbotron with i18n support

### Frontend Changes Needed

#### 8.1 Update Jumbotron Component

**File**: `bibsonomy-webapp-v2/src/components/home/Jumbotron.vue`

```vue
<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { Edit, LogIn } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import { useBranding } from '@/composables/useBranding'
import Button from '@/components/ui/Button.vue'

const { t } = useI18n()
const { branding } = useBranding()
const authStore = useAuthStore()
</script>

<template>
  <!-- Only show jumbotron if NOT logged in -->
  <div v-if="!authStore.isAuthenticated" class="bg-gray-100 rounded-lg p-4 md:p-8 mb-5">
    <!-- Headline -->
    <h2 class="text-2xl md:text-3xl font-medium text-gray-800 mb-3 leading-tight md:leading-[38px]">
      {{ t('home.headline') }}
    </h2>

    <!-- Lead text -->
    <p class="text-base md:text-xl font-light leading-relaxed mb-5 text-gray-800">
      {{ t('home.lead', { projectName: branding.projectName }) }}
    </p>

    <!-- Divider -->
    <hr class="border-0 border-t border-gray-300 my-3 md:my-5" />

    <!-- CTA Buttons -->
    <div class="flex flex-col sm:flex-row gap-2 mt-3 md:mt-5">
      <Button
        variant="success"
        :icon="Edit"
        href="/error/not-implemented?feature=Register"
      >
        {{ t('home.register') }}
      </Button>

      <Button
        variant="info"
        href="/error/not-implemented?feature=Getting+Started"
      >
        {{ t('home.learnmore') }}
      </Button>

      <!-- Login Button (NEW) -->
      <Button
        variant="primary"
        :icon="LogIn"
        href="/error/not-implemented?feature=Login"
      >
        {{ t('home.login') }}
      </Button>
    </div>
  </div>
</template>
```

#### 8.2 Add Translation

**File**: `bibsonomy-webapp-v2/public/locales/en/translation.json`

```json
{
  "home": {
    "headline": "Welcome to BibSonomy",
    "lead": "{projectName} is a social bookmarking and publication sharing system.",
    "register": "Register",
    "learnmore": "Learn More",
    "login": "Login"
  }
}
```

**File**: `bibsonomy-webapp-v2/public/locales/de/translation.json`

```json
{
  "home": {
    "headline": "Willkommen bei BibSonomy",
    "lead": "{projectName} ist ein Social-Bookmarking- und Publikations-Sharing-System.",
    "register": "Registrieren",
    "learnmore": "Mehr erfahren",
    "login": "Anmelden"
  }
}
```

### Testing

1. Logout (or open in incognito)
2. Check jumbotron appears with Login button
3. Login and verify jumbotron disappears

---

## Implementation Priority Order

Recommended order based on complexity and dependencies:

1. **Tag Icon** (easiest, pure visual)
2. **Relative Time Display** (standalone, no backend needed)
3. **Login Button in Jumbotron** (simple addition)
4. **Pagination** (important UX, no backend needed)
5. **Other People Count Badge** (requires backend changes)
6. **Collection/Private/Group Badges** (depends on auth)
7. **Thumbnail Preview Images** (needs investigation)
8. **Hidden/System Tags** (needs backend investigation)

## Testing Checklist

After implementing each feature:

- [ ] Backend changes compile and tests pass
- [ ] OpenAPI docs show new fields (if applicable)
- [ ] Frontend TypeScript compiles without errors
- [ ] Feature works in Chrome DevTools (desktop viewport)
- [ ] Feature works on mobile viewport (375px)
- [ ] No console errors
- [ ] i18n strings work in both English and German
- [ ] Feature doesn't break existing functionality

## Notes

- All backend changes require rebuilding REST API v2
- All frontend changes require running `bun run type-check`
- Use `bun run dev` for hot reloading during development
- Test against real API at `http://localhost:8080`
