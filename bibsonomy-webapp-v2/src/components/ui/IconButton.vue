<script setup lang="ts">
import { computed, type Component } from 'vue'
import type { RouteLocationRaw } from 'vue-router'

interface Props {
  /** Icon component (Lucide icon) */
  icon: Component
  /** Button variant */
  variant?: 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'ghost'
  /** Button size */
  size?: 'sm' | 'md' | 'lg'
  /** Disabled state */
  disabled?: boolean
  /** Button type attribute */
  type?: 'button' | 'submit' | 'reset'
  /** Router link destination */
  to?: RouteLocationRaw
  /** External href */
  href?: string
  /** Aria label (required for accessibility) */
  ariaLabel: string
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'ghost',
  size: 'md',
  disabled: false,
  type: 'button',
})

const componentTag = computed(() => {
  if (props.to) return 'router-link'
  if (props.href) return 'a'
  return 'button'
})

const buttonClasses = computed(() => {
  const base =
    'inline-flex items-center justify-center rounded transition-colors focus:outline-none focus-visible:ring-2 focus-visible:ring-offset-2'

  // Size classes (square buttons)
  const sizeClasses = {
    sm: 'p-1',
    md: 'p-2',
    lg: 'p-3',
  }

  // Variant classes
  const variantClasses = {
    primary:
      'bg-primary-600 text-white hover:bg-primary-700 focus-visible:ring-primary-500 disabled:bg-primary-300',
    secondary:
      'bg-gray-600 text-white hover:bg-gray-700 focus-visible:ring-gray-500 disabled:bg-gray-300',
    success:
      'bg-success-600 text-white hover:bg-success-700 focus-visible:ring-success-500 disabled:bg-success-300',
    danger:
      'bg-danger-600 text-white hover:bg-danger-700 focus-visible:ring-danger-500 disabled:bg-danger-300',
    warning:
      'bg-warning-600 text-white hover:bg-warning-700 focus-visible:ring-warning-500 disabled:bg-warning-300',
    ghost: 'text-gray-600 hover:bg-gray-100 focus-visible:ring-gray-500 disabled:text-gray-300',
  }

  const disabledClass = props.disabled ? 'cursor-not-allowed opacity-60' : 'cursor-pointer'

  return [base, sizeClasses[props.size], variantClasses[props.variant], disabledClass].join(' ')
})

const iconSize = computed(() => {
  const sizes = {
    sm: 14,
    md: 18,
    lg: 24,
  }
  return sizes[props.size]
})
</script>

<template>
  <component
    :is="componentTag"
    :type="componentTag === 'button' ? type : undefined"
    :to="to"
    :href="href"
    :disabled="disabled"
    :class="buttonClasses"
    :aria-label="ariaLabel"
    :aria-disabled="disabled"
  >
    <component :is="icon" :size="iconSize" aria-hidden="true" />
  </component>
</template>
