<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  /** Badge variant - controls color scheme */
  variant?: 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info' | 'gray'
  /** Badge size */
  size?: 'xs' | 'sm' | 'md'
  /** Use outlined style instead of filled */
  outlined?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'gray',
  size: 'sm',
  outlined: false,
})

const badgeClasses = computed(() => {
  const base = 'inline-flex items-center gap-1 font-medium rounded-full'

  // Size classes
  const sizeClasses = {
    xs: 'px-1.5 py-0.5 text-xs',
    sm: 'px-2 py-0.5 text-xs',
    md: 'px-2.5 py-1 text-sm',
  }

  // Variant classes (filled)
  const filledVariants = {
    primary: 'bg-primary-100 text-primary-700 border border-primary-200',
    secondary: 'bg-gray-100 text-gray-700 border border-gray-200',
    success: 'bg-success-100 text-success-700 border border-success-200',
    danger: 'bg-danger-100 text-danger-700 border border-danger-200',
    warning: 'bg-warning-100 text-warning-700 border border-warning-200',
    info: 'bg-info-100 text-info-700 border border-info-200',
    gray: 'bg-gray-100 text-gray-600 border border-gray-200',
  }

  // Variant classes (outlined)
  const outlinedVariants = {
    primary: 'bg-white text-primary-600 border border-primary-600',
    secondary: 'bg-white text-gray-600 border border-gray-600',
    success: 'bg-white text-success-600 border border-success-600',
    danger: 'bg-white text-danger-600 border border-danger-600',
    warning: 'bg-white text-warning-600 border border-warning-600',
    info: 'bg-white text-info-600 border border-info-600',
    gray: 'bg-white text-gray-600 border border-gray-400',
  }

  const variantClass = props.outlined
    ? outlinedVariants[props.variant]
    : filledVariants[props.variant]

  return [base, sizeClasses[props.size], variantClass].join(' ')
})
</script>

<template>
  <span :class="badgeClasses">
    <slot />
  </span>
</template>
