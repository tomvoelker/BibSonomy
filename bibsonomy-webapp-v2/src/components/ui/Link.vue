<script setup lang="ts">
import { computed } from 'vue'
import type { RouteLocationRaw } from 'vue-router'

interface Props {
  /** Router link destination (if provided, renders as router-link) */
  to?: RouteLocationRaw
  /** External href (if provided, renders as anchor) */
  href?: string
  /** Open in new tab (only for external links) */
  external?: boolean
  /** Disable default link styling */
  unstyled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  external: false,
  unstyled: false,
})

const componentTag = computed(() => {
  if (props.to) return 'router-link'
  if (props.href) return 'a'
  return 'span'
})

const linkClasses = computed(() => {
  if (props.unstyled) return ''
  return 'text-primary-600 hover:text-primary-700 hover:underline transition-colors focus:outline-none focus-visible:ring-2 focus-visible:ring-primary-500 focus-visible:ring-offset-2 rounded-sm'
})

const linkTarget = computed(() => (props.external ? '_blank' : undefined))
const linkRel = computed(() => (props.external ? 'noopener noreferrer' : undefined))
</script>

<template>
  <component
    :is="componentTag"
    :to="to"
    :href="href"
    :target="linkTarget"
    :rel="linkRel"
    :class="linkClasses"
  >
    <slot />
  </component>
</template>
