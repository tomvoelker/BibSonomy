<script setup lang="ts">
import { type Component } from 'vue'
import { Filter, ArrowUpDown, Share2 } from 'lucide-vue-next'
import Badge from '@/components/ui/Badge.vue'
import IconButton from '@/components/ui/IconButton.vue'

interface Props {
  title: string
  count: number
  icon?: Component
}

defineProps<Props>()

const emit = defineEmits<{
  filter: []
  sort: []
  export: []
}>()
</script>

<template>
  <div class="flex items-center justify-between border-b border-gray-300 pb-3 mb-4">
    <!-- Left: Title + Count -->
    <div class="flex items-center gap-2">
      <component v-if="icon" :is="icon" :size="20" class="text-gray-600" aria-hidden="true" />
      <h3 class="text-xl font-medium text-gray-800">{{ title }}</h3>
      <Badge variant="gray">{{ count }}</Badge>
    </div>

    <!-- Right: Action Buttons -->
    <div class="flex gap-2">
      <IconButton
        :icon="Filter"
        variant="ghost"
        size="sm"
        aria-label="Filter posts"
        @click="emit('filter')"
      />
      <IconButton
        :icon="ArrowUpDown"
        variant="ghost"
        size="sm"
        aria-label="Sort posts"
        @click="emit('sort')"
      />
      <IconButton
        :icon="Share2"
        variant="ghost"
        size="sm"
        aria-label="Export posts"
        @click="emit('export')"
      />
    </div>
  </div>
</template>
