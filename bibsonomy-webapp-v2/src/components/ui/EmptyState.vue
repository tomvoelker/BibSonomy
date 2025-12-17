<script setup lang="ts">
import { type Component } from 'vue'

interface Props {
  /** Icon to display (Lucide icon component) */
  icon?: Component
  /** Title text */
  title?: string
  /** Message text */
  message: string
}

const props = defineProps<Props>()
</script>

<template>
  <div class="flex flex-col items-center justify-center py-12 px-4 text-center">
    <!-- Icon -->
    <component
      v-if="icon"
      :is="icon"
      :size="48"
      class="text-gray-400 mb-4"
      aria-hidden="true"
    />

    <!-- Title -->
    <h3 v-if="title" class="text-lg font-medium text-gray-900 mb-2">
      {{ title }}
    </h3>

    <!-- Message -->
    <p class="text-gray-600 max-w-md">
      {{ message }}
    </p>

    <!-- Action slot (optional) -->
    <div v-if="$slots.action" class="mt-6">
      <slot name="action" />
    </div>
  </div>
</template>
