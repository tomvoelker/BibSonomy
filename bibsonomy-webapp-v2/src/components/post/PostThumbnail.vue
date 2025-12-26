<script setup lang="ts">
import { computed } from 'vue'
import { EyeOff, Users } from 'lucide-vue-next'
import type { Post } from '@/types/models'

interface Props {
  post: Post
}

const props = defineProps<Props>()

// Check if post is private
const isPrivate = computed(() => {
  return props.post.visibility === 'private' || props.post.groups.some((g) => g.name === 'private')
})

// Check if post is in specific groups
const isInGroups = computed(() => {
  return (
    props.post.visibility === 'groups' ||
    props.post.groups.some((g) => g.name !== 'public' && g.name !== 'private')
  )
})

// Get thumbnail URL if available
const thumbnailUrl = computed(() => {
  // TODO: implement when backend provides thumbnail URLs
  return undefined
})
</script>

<template>
  <div
    class="post-thumbnail w-16 h-16 md:w-20 md:h-20 relative bg-gray-100 rounded overflow-hidden flex items-center justify-center"
  >
    <!-- Thumbnail image or placeholder -->
    <img
      v-if="thumbnailUrl"
      :src="thumbnailUrl"
      :alt="`Thumbnail for ${post.resource.resourceType}`"
      class="w-full h-full object-cover"
    />
    <span v-else class="text-2xl">
      {{ post.resource.resourceType === 'bookmark' ? '🔖' : '📄' }}
    </span>

    <!-- Overlay badges (top-right corner) -->
    <div class="absolute top-0.5 right-0.5 flex flex-col gap-0.5">
      <!-- Private -->
      <div v-if="isPrivate" class="bg-gray-700 text-white rounded-full p-0.5" title="Private">
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
