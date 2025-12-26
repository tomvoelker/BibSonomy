<script setup lang="ts">
import { computed } from 'vue'
import { Clock } from 'lucide-vue-next'
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

// Get groups (if any)
const groups = computed(() => props.post.groups || [])
</script>

<template>
  <div class="text-xs text-gray-600 flex items-center gap-2 flex-wrap">
    <!-- Clock Icon + Relative Time -->
    <span v-if="relativeTime" class="flex items-center gap-1" :title="fullDateTime">
      <Clock :size="12" class="text-gray-500" />
      {{ relativeTime }}
    </span>

    <!-- User -->
    <span class="text-gray-500">by</span>
    <UserLink :user="post.user" />

    <!-- Groups -->
    <Badge v-for="group in groups" :key="group.name" size="xs" variant="secondary">
      {{ group.name }}
    </Badge>
  </div>
</template>
