<script setup lang="ts">
interface Props {
  /** Remove default padding */
  noPadding?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  noPadding: false,
})
</script>

<template>
  <div :class="['page-container', { 'p-0': noPadding }]">
    <slot />
  </div>
</template>
