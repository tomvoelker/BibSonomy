<script setup lang="ts">
import { RouterLink } from 'vue-router'
import type { Tag } from '@/types/models'

interface Props {
  tag: Tag
  /** Make the tag clickable */
  clickable?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  clickable: true,
})
</script>

<template>
  <RouterLink
    v-if="clickable"
    :to="`/tags/${tag.name}`"
    class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium border border-primary-600 text-primary-600 hover:bg-primary-50 transition-colors no-underline"
  >
    {{ tag.name }}
  </RouterLink>
  <span
    v-else
    class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium border border-primary-600 text-primary-600"
  >
    {{ tag.name }}
  </span>
</template>
