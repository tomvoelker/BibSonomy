<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  /** Spinner size */
  size?: 'sm' | 'md' | 'lg'
  /** Custom color (defaults to primary) */
  color?: 'primary' | 'white' | 'gray'
}

const props = withDefaults(defineProps<Props>(), {
  size: 'md',
  color: 'primary',
})

const sizeClasses = computed(() => {
  const sizes = {
    sm: 'h-4 w-4',
    md: 'h-8 w-8',
    lg: 'h-12 w-12',
  }
  return sizes[props.size]
})

const colorClass = computed(() => {
  const colors = {
    primary: 'text-primary-600',
    white: 'text-white',
    gray: 'text-gray-600',
  }
  return colors[props.color]
})
</script>

<template>
  <div class="flex items-center justify-center" role="status" aria-live="polite">
    <svg
      :class="[sizeClasses, colorClass, 'animate-spin']"
      xmlns="http://www.w3.org/2000/svg"
      fill="none"
      viewBox="0 0 24 24"
      aria-hidden="true"
    >
      <circle
        class="opacity-25"
        cx="12"
        cy="12"
        r="10"
        stroke="currentColor"
        stroke-width="4"
      ></circle>
      <path
        class="opacity-75"
        fill="currentColor"
        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
      ></path>
    </svg>
    <span class="sr-only">Loading...</span>
  </div>
</template>
