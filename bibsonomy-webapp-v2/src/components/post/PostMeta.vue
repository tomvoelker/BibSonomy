<script setup lang="ts">
import { computed } from 'vue'
import UserLink from '@/components/user/UserLink.vue'
import Badge from '@/components/ui/Badge.vue'
import type { Post } from '@/types/models'

interface Props {
  post: Post
}

const props = defineProps<Props>()

// Format date
const formattedDate = computed(() => {
  if (!props.post.createdAt) return ''
  const date = new Date(props.post.createdAt)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
})

// Get groups (if any)
const groups = computed(() => props.post.groups || [])
</script>

<template>
  <div class="text-xs text-gray-600 flex items-center gap-2 flex-wrap">
    <!-- User -->
    <UserLink :user="post.user" />

    <!-- Date -->
    <span v-if="formattedDate">{{ formattedDate }}</span>

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
