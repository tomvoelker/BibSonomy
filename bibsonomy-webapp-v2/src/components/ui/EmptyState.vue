<script setup lang="ts">
import { type Component } from 'vue'

interface Props {
  /** Icon to display (Lucide icon component) */
  icon?: Component
  /** Title text */
  title?: string
  /** Message text */
  message: string
  /** Semantic heading level (h2, h3, h4, h5, h6) for accessibility */
  headingLevel?: 'h2' | 'h3' | 'h4' | 'h5' | 'h6'
}

const props = withDefaults(defineProps<Props>(), {
  headingLevel: 'h3',
})
</script>

<template>
  <div class="flex flex-col items-center justify-center py-12 px-4 text-center">
    <!-- Icon -->
    <component
      v-if="props.icon"
      :is="props.icon"
      :size="48"
      class="text-gray-400 mb-4"
      aria-hidden="true"
    />

    <!-- Title (semantic heading level configurable for accessibility) -->
    <component
      v-if="props.title"
      :is="props.headingLevel"
      class="text-lg font-medium text-gray-900 mb-2"
    >
      {{ props.title }}
    </component>

    <!-- Message -->
    <p class="text-gray-600 max-w-md">
      {{ props.message }}
    </p>

    <!-- Action slot (optional) -->
    <div v-if="$slots['action']" class="mt-6">
      <slot name="action" />
    </div>
  </div>
</template>
