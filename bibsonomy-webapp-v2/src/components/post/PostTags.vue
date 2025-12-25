<script setup lang="ts">
import { Tag as TagIcon } from 'lucide-vue-next'
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
