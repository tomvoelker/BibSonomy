<script setup lang="ts">
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
  <a
    v-if="clickable"
    :href="`/tags/${tag.name}`"
    class="inline-flex items-center px-1.5 py-0.5 rounded text-[10px] md:text-xs font-medium border border-primary-600 text-primary-600 hover:bg-primary-50 transition-colors no-underline"
  >
    {{ tag.name }}
  </a>
  <span
    v-else
    class="inline-flex items-center px-1.5 py-0.5 rounded text-[10px] md:text-xs font-medium border border-primary-600 text-primary-600"
  >
    {{ tag.name }}
  </span>
</template>
