<script setup lang="ts">
import { computed } from 'vue'

interface Option {
  id: string
  label: string
}

interface Props {
  options: Option[]
  modelValue: string
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const activeOption = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})
</script>

<template>
  <div class="inline-flex bg-gray-200 rounded-lg p-1">
    <button
      v-for="option in options"
      :key="option.id"
      @click="activeOption = option.id"
      class="px-3 py-1.5 text-sm font-medium rounded transition-all"
      :class="
        activeOption === option.id
          ? 'bg-white text-gray-900 shadow-sm'
          : 'text-gray-600 hover:text-gray-900'
      "
    >
      {{ option.label }}
    </button>
  </div>
</template>
