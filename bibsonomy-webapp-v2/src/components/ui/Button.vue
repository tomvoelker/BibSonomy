<script setup lang="ts">
import { computed, type Component } from 'vue'
import type { RouteLocationRaw } from 'vue-router'

interface Props {
  /** Button variant - controls color scheme */
  variant?: 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'link'
  /** Button size */
  size?: 'sm' | 'md' | 'lg'
  /** Disabled state */
  disabled?: boolean
  /** Loading state - shows spinner */
  loading?: boolean
  /** Optional icon component (Lucide icon) */
  icon?: Component
  /** Button type attribute */
  type?: 'button' | 'submit' | 'reset'
  /** Router link destination (if provided, renders as router-link) */
  to?: RouteLocationRaw
  /** External href (if provided, renders as anchor) */
  href?: string
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'md',
  disabled: false,
  loading: false,
  type: 'button',
})

const componentTag = computed(() => {
  if (props.to) return 'router-link'
  if (props.href) return 'a'
  return 'button'
})

const buttonClasses = computed(() => {
  const base = 'inline-flex items-center justify-center gap-2 font-medium transition-colors rounded focus:outline-none focus-visible:ring-2 focus-visible:ring-offset-2'

  // Size classes
  const sizeClasses = {
    sm: 'px-3 py-1.5 text-sm',
    md: 'px-4 py-2 text-base',
    lg: 'px-6 py-3 text-lg',
  }

  // Variant classes
  const variantClasses = {
    primary: 'bg-primary-600 text-white hover:bg-primary-700 focus-visible:ring-primary-500 disabled:bg-primary-300',
    secondary: 'bg-gray-600 text-white hover:bg-gray-700 focus-visible:ring-gray-500 disabled:bg-gray-300',
    success: 'bg-success-600 text-white hover:bg-success-700 focus-visible:ring-success-500 disabled:bg-success-300',
    danger: 'bg-danger-600 text-white hover:bg-danger-700 focus-visible:ring-danger-500 disabled:bg-danger-300',
    warning: 'bg-warning-600 text-white hover:bg-warning-700 focus-visible:ring-warning-500 disabled:bg-warning-300',
    link: 'text-primary-600 hover:underline focus-visible:ring-primary-500 disabled:text-primary-300',
  }

  const disabledClass = (props.disabled || props.loading) ? 'cursor-not-allowed opacity-60' : 'cursor-pointer'

  return [
    base,
    sizeClasses[props.size],
    variantClasses[props.variant],
    disabledClass,
  ].join(' ')
})
</script>

<template>
  <component
    :is="componentTag"
    :type="componentTag === 'button' ? type : undefined"
    :to="to"
    :href="href"
    :disabled="disabled || loading"
    :class="buttonClasses"
    :aria-disabled="disabled || loading"
  >
    <!-- Icon (left side) -->
    <component
      v-if="icon && !loading"
      :is="icon"
      :size="size === 'sm' ? 14 : size === 'lg' ? 20 : 16"
      aria-hidden="true"
    />

    <!-- Loading spinner -->
    <svg
      v-if="loading"
      class="animate-spin"
      :class="size === 'sm' ? 'h-3.5 w-3.5' : size === 'lg' ? 'h-5 w-5' : 'h-4 w-4'"
      xmlns="http://www.w3.org/2000/svg"
      fill="none"
      viewBox="0 0 24 24"
      aria-hidden="true"
    >
      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
      <path
        class="opacity-75"
        fill="currentColor"
        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
      ></path>
    </svg>

    <!-- Default slot for button content -->
    <slot />
  </component>
</template>
