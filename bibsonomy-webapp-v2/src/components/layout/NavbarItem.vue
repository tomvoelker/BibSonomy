<script setup lang="ts">
import { computed, type Component } from 'vue'
import { useRoute } from 'vue-router'

interface Props {
  /** Link destination */
  to: string
  /** Optional icon component (Lucide icon) */
  icon?: Component
  /** Manual active state control (if not provided, uses route matching) */
  active?: boolean
}

const props = defineProps<Props>()
const route = useRoute()

const isActive = computed(() => {
  if (props.active !== undefined) return props.active
  return route.path === props.to
})

const linkClasses = computed(() => {
  const base = 'block px-4 py-2.5 text-sm text-gray-100 no-underline leading-5 transition-colors'
  const activeClass = isActive.value
    ? 'bg-info-600 text-white'
    : 'hover:bg-info-600'

  return `${base} ${activeClass}`
})
</script>

<template>
  <li class="relative block">
    <a :href="to" :class="linkClasses">
      <component
        v-if="icon"
        :is="icon"
        :size="16"
        class="align-middle inline-block"
        aria-hidden="true"
      />
      <slot />
    </a>
  </li>
</template>
