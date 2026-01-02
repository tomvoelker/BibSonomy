<script setup lang="ts">
import { computed } from 'vue'
import Card from '@/components/ui/Card.vue'
import Link from '@/components/ui/Link.vue'
import PostThumbnail from './PostThumbnail.vue'
import PostMeta from './PostMeta.vue'
import PostTags from './PostTags.vue'
import PostActions from './PostActions.vue'
import type { Post } from '@/types/models'
import {
  getPostTitle,
  formatAuthors,
  getPublicationYear,
  isPublication,
  isBookmark,
} from '@/types/models'

interface Props {
  post: Post
  /** Show detailed view with actions */
  detailed?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  detailed: true,
})

const emit = defineEmits<{
  edit: []
  copy: []
  history: []
  export: []
}>()

// Get post description (only for publications)
const description = computed(() => {
  if (isPublication(props.post)) {
    // For publications, show authors and year from resource
    const resource = props.post.resource
    const authorList = resource?.authors || []
    const authorNames = authorList.map((a: { name?: string }) => a.name || '').join(' and ')
    const authors = formatAuthors(authorNames)
    const year = getPublicationYear(props.post)
    const journal = resource?.journal

    let desc = authors
    if (year) desc += ` (${year})`
    if (journal) desc += ` - ${journal}`
    return desc
  }
  return null
})

// Get display URL for bookmarks (show domain for cleaner display)
const displayUrl = computed(() => {
  if (!isBookmark(props.post) || !props.post.url) return null
  try {
    const url = new URL(props.post.url)
    return url.hostname + (url.pathname !== '/' ? url.pathname : '')
  } catch {
    return props.post.url
  }
})
</script>

<template>
  <Card
    class="flex gap-3 md:gap-4 p-3 md:p-4 hover:shadow-md transition-shadow h-auto md:h-[180px]"
  >
    <!-- Thumbnail (smaller on mobile) -->
    <div class="flex-shrink-0">
      <PostThumbnail :post="post" />
    </div>

    <!-- Content -->
    <div class="flex-1 min-w-0 flex flex-col">
      <!-- Title -->
      <Link
        :href="`/error/not-implemented?feature=Post+Details`"
        class="text-sm md:text-base font-semibold block mb-1 line-clamp-2"
        :class="{ 'font-bold': isPublication(post) }"
      >
        {{ getPostTitle(post) }}
      </Link>

      <!-- URL for bookmarks -->
      <a
        v-if="displayUrl && post.url"
        :href="post.url"
        target="_blank"
        rel="noopener noreferrer"
        class="text-xs text-gray-500 hover:text-primary-600 truncate mb-1 block"
        :title="post.url"
      >
        {{ displayUrl }}
      </a>

      <!-- Description/Authors for publications -->
      <div v-if="description" class="text-xs md:text-sm text-gray-700 mb-2 line-clamp-2">
        {{ description }}
      </div>

      <!-- Spacer to push meta to bottom (only on desktop) -->
      <div class="flex-1 hidden md:block"></div>

      <!-- Tags -->
      <PostTags :tags="post.tags" class="mb-2" />

      <!-- Meta (user, date, groups) -->
      <PostMeta :post="post" />
    </div>

    <!-- Actions (vertical column on right, hidden on mobile) -->
    <div v-if="detailed" class="hidden md:flex flex-col gap-1 flex-shrink-0">
      <PostActions
        :post="post"
        @edit="emit('edit')"
        @copy="emit('copy')"
        @history="emit('history')"
        @export="emit('export')"
      />
    </div>
  </Card>
</template>
