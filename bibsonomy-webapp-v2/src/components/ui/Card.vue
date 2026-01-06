<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  /** Add padding to card content */
  padded?: boolean
  /** Make card clickable with hover effect */
  clickable?: boolean
  /** Add border to card */
  bordered?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  padded: true,
  clickable: false,
  bordered: true,
})

const cardClasses = computed(() => {
  const base = 'bg-white rounded'

  const paddingClass = props.padded ? 'p-4' : ''
  const borderClass = props.bordered ? 'border border-gray-300' : ''
  const clickableClass = props.clickable ? 'cursor-pointer hover:shadow-md transition-shadow' : ''

  return [base, paddingClass, borderClass, clickableClass].filter(Boolean).join(' ')
})
</script>

<template>
  <div :class="cardClasses">
    <slot />
  </div>
</template>
