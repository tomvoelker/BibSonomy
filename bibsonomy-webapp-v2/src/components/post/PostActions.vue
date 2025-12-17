<script setup lang="ts">
import { Edit, Copy, History, Share2 } from 'lucide-vue-next'
import IconButton from '@/components/ui/IconButton.vue'
import type { Post } from '@/types/models'

interface Props {
  post: Post
}

defineProps<Props>()

const emit = defineEmits<{
  edit: []
  copy: []
  history: []
  export: []
}>()
</script>

<template>
  <div class="flex flex-col gap-1">
    <IconButton
      :icon="Edit"
      variant="ghost"
      size="sm"
      aria-label="Edit post"
      @click="emit('edit')"
    />
    <IconButton
      :icon="Copy"
      variant="ghost"
      size="sm"
      aria-label="Copy post"
      @click="emit('copy')"
    />
    <IconButton
      :icon="History"
      variant="ghost"
      size="sm"
      aria-label="View history"
      @click="emit('history')"
    />
    <IconButton
      :icon="Share2"
      variant="ghost"
      size="sm"
      aria-label="Export post"
      @click="emit('export')"
    />
  </div>
</template>
