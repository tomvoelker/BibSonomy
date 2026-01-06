<script setup lang="ts">
import { Edit, Copy, History, Share2 } from 'lucide-vue-next'
import IconButton from '@/components/ui/IconButton.vue'

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
      ariaLabel="Edit post"
      @click="emit('edit')"
    />
    <IconButton
      :icon="Copy"
      variant="ghost"
      size="sm"
      ariaLabel="Copy post"
      @click="emit('copy')"
    />
    <IconButton
      :icon="History"
      variant="ghost"
      size="sm"
      ariaLabel="View history"
      @click="emit('history')"
    />
    <IconButton
      :icon="Share2"
      variant="ghost"
      size="sm"
      ariaLabel="Export post"
      @click="emit('export')"
    />
  </div>
</template>
