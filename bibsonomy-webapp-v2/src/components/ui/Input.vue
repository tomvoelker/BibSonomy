<script setup lang="ts">
import { computed, useAttrs } from 'vue'

interface Props {
  /** Input value (v-model) */
  modelValue?: string | number
  /** Input type */
  type?: 'text' | 'email' | 'password' | 'number' | 'search' | 'tel' | 'url'
  /** Label text */
  label?: string
  /** Placeholder text */
  placeholder?: string
  /** Error message */
  error?: string
  /** Helper text */
  helperText?: string
  /** Disabled state */
  disabled?: boolean
  /** Required field */
  required?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  disabled: false,
  required: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: string | number]
}>()

const attrs = useAttrs()

const inputId = computed(() => attrs.id as string || `input-${Math.random().toString(36).substr(2, 9)}`)

const inputClasses = computed(() => {
  const base = 'block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-offset-0 transition-colors'

  const stateClasses = props.error
    ? 'border-danger-300 text-danger-900 placeholder-danger-300 focus:ring-danger-500 focus:border-danger-500'
    : 'border-gray-300 focus:ring-primary-500 focus:border-primary-500'

  const disabledClass = props.disabled
    ? 'bg-gray-50 text-gray-500 cursor-not-allowed'
    : 'bg-white'

  return [base, stateClasses, disabledClass].join(' ')
})

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  const value = props.type === 'number' ? Number(target.value) : target.value
  emit('update:modelValue', value)
}
</script>

<template>
  <div class="w-full">
    <!-- Label -->
    <label
      v-if="label"
      :for="inputId"
      class="block text-sm font-medium text-gray-700 mb-1"
    >
      {{ label }}
      <span v-if="required" class="text-danger-500" aria-label="required">*</span>
    </label>

    <!-- Input -->
    <input
      :id="inputId"
      :type="type"
      :value="modelValue"
      :placeholder="placeholder"
      :disabled="disabled"
      :required="required"
      :class="inputClasses"
      :aria-invalid="!!error"
      :aria-describedby="error ? `${inputId}-error` : helperText ? `${inputId}-helper` : undefined"
      @input="handleInput"
      v-bind="$attrs"
    />

    <!-- Helper text -->
    <p
      v-if="helperText && !error"
      :id="`${inputId}-helper`"
      class="mt-1 text-sm text-gray-500"
    >
      {{ helperText }}
    </p>

    <!-- Error message -->
    <p
      v-if="error"
      :id="`${inputId}-error`"
      class="mt-1 text-sm text-danger-600"
      role="alert"
    >
      {{ error }}
    </p>
  </div>
</template>
