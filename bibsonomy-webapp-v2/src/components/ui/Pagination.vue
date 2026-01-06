<script setup lang="ts">
import { computed } from 'vue'
import { ChevronLeft, ChevronRight } from 'lucide-vue-next'
import Button from './Button.vue'

interface Props {
  totalCount: number
  offset: number
  limit: number
}

const props = defineProps<Props>()

const emit = defineEmits<{
  prev: []
  next: []
}>()

const currentPage = computed(() => Math.floor(props.offset / props.limit) + 1)
const totalPages = computed(() => Math.ceil(props.totalCount / props.limit))
const hasPrev = computed(() => props.offset > 0)
const hasNext = computed(() => props.offset + props.limit < props.totalCount)
</script>

<template>
  <div
    v-if="totalPages > 1"
    class="flex items-center justify-between py-4 border-t border-gray-200"
  >
    <!-- Previous Button -->
    <Button variant="secondary" :disabled="!hasPrev" @click="emit('prev')">
      <ChevronLeft :size="16" />
      Previous
    </Button>

    <!-- Page Info -->
    <span class="text-sm text-gray-600">
      Page {{ currentPage }} of {{ totalPages }} ({{ totalCount }} total)
    </span>

    <!-- Next Button -->
    <Button variant="secondary" :disabled="!hasNext" @click="emit('next')">
      Next
      <ChevronRight :size="16" />
    </Button>
  </div>
</template>
