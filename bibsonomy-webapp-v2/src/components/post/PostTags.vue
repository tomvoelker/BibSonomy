<script setup lang="ts">
import TagBadge from '@/components/tag/TagBadge.vue'
import type { Tag } from '@/types/models'

interface Props {
  tags: Tag[]
}

defineProps<Props>()
</script>

<template>
  <div v-if="tags.length > 0" class="flex flex-wrap gap-1.5">
    <TagBadge v-for="tag in tags" :key="tag.name" :tag="tag" />
  </div>
</template>
