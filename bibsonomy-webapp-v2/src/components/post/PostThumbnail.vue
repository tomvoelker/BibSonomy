<script setup lang="ts">
import { computed } from 'vue'
import { Bookmark, FileText, ExternalLink, EyeOff } from 'lucide-vue-next'
import Badge from '@/components/ui/Badge.vue'
import type { Post } from '@/types/models'

interface Props {
  post: Post
}

const props = defineProps<Props>()

// Check if post is private or in collection
// Note: These are placeholder checks - adjust based on actual data structure
const isPrivate = computed(() => false) // TODO: implement based on post visibility
const inCollection = computed(() => false) // TODO: implement based on post collection status

// Get thumbnail URL if available
const thumbnailUrl = computed(() => {
  // TODO: implement based on actual post structure
  return undefined
})

// Use appropriate icon based on resource type
const resourceIcon = computed(() => {
  return props.post.resourceType === 'bookmark' ? ExternalLink : FileText
})
</script>

<template>
  <div class="post-thumbnail">
    <!-- Thumbnail image or styled placeholder icon -->
    <img
      v-if="thumbnailUrl"
      :src="thumbnailUrl"
      :alt="`Thumbnail for ${post.resourceType}`"
      class="w-full h-full object-cover"
    />
    <component :is="resourceIcon" v-else :size="24" class="text-gray-400" />

    <!-- Overlay badges -->
    <div class="absolute top-1 right-1 flex gap-1">
      <Badge v-if="isPrivate" size="xs" variant="danger">
        <EyeOff :size="12" />
      </Badge>
      <Badge v-if="inCollection" size="xs" variant="info">
        <Bookmark :size="12" />
      </Badge>
    </div>
  </div>
</template>
