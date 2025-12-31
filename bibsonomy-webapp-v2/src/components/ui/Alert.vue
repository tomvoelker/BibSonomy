<script setup lang="ts">
import { computed, type Component } from 'vue'
import { Info, CheckCircle, AlertTriangle, XCircle } from 'lucide-vue-next'

interface Props {
  /** Alert variant - controls color scheme and icon */
  variant?: 'info' | 'success' | 'warning' | 'danger'
  /** Title text */
  title?: string
  /** Custom icon (overrides default variant icon) */
  icon?: Component
  /** Show close button */
  dismissible?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'info',
  dismissible: false,
})

const emit = defineEmits<{
  dismiss: []
}>()

const variantClasses = computed(() => {
  const variants = {
    info: 'bg-info-50 border-info-200 text-info-800',
    success: 'bg-success-50 border-success-200 text-success-800',
    warning: 'bg-warning-50 border-warning-200 text-warning-800',
    danger: 'bg-danger-50 border-danger-200 text-danger-800',
  }
  return variants[props.variant]
})

const defaultIcon = computed(() => {
  const icons = {
    info: Info,
    success: CheckCircle,
    warning: AlertTriangle,
    danger: XCircle,
  }
  return props.icon || icons[props.variant]
})

const iconColor = computed(() => {
  const colors = {
    info: 'text-info-600',
    success: 'text-success-600',
    warning: 'text-warning-600',
    danger: 'text-danger-600',
  }
  return colors[props.variant]
})
</script>

<template>
  <div :class="['rounded border p-4 flex gap-3', variantClasses]" role="alert">
    <!-- Icon -->
    <component
      :is="defaultIcon"
      :size="20"
      :class="['flex-shrink-0 mt-0.5', iconColor]"
      aria-hidden="true"
    />

    <!-- Content -->
    <div class="flex-1 min-w-0">
      <h4 v-if="title" class="font-medium mb-1">{{ title }}</h4>
      <div class="text-sm">
        <slot />
      </div>
    </div>

    <!-- Dismiss button -->
    <button
      v-if="dismissible"
      type="button"
      @click="emit('dismiss')"
      class="flex-shrink-0 hover:opacity-70 transition-opacity focus:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 rounded"
      :class="iconColor"
      aria-label="Dismiss"
    >
      <XCircle :size="20" />
    </button>
  </div>
</template>
