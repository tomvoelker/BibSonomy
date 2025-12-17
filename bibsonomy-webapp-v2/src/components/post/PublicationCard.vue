<script setup lang="ts">
import PostCard from './PostCard.vue'
import type { Post } from '@/types/models'

interface Props {
  post: Post
  detailed?: boolean
}

defineProps<Props>()

const emit = defineEmits<{
  edit: []
  copy: []
  history: []
  export: []
}>()
</script>

<template>
  <PostCard
    :post="post"
    :detailed="detailed"
    @edit="emit('edit')"
    @copy="emit('copy')"
    @history="emit('history')"
    @export="emit('export')"
  />
</template>
