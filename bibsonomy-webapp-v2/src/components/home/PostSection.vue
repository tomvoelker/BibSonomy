<script setup lang="ts">
import { type Component } from 'vue'
import { useI18n } from 'vue-i18n'
import PostSectionHeader from './PostSectionHeader.vue'
import Spinner from '@/components/ui/Spinner.vue'
import EmptyState from '@/components/ui/EmptyState.vue'
import type { Post } from '@/types/models'

interface Props {
  title: string
  posts: Post[]
  loading?: boolean
  icon?: Component
  /** Component to use for rendering each post (BookmarkCard, PublicationCard, etc.) */
  cardComponent: Component
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
})

const { t } = useI18n()

const emit = defineEmits<{
  filter: []
  sort: []
  export: []
}>()
</script>

<template>
  <div class="mb-8">
    <!-- Section Header -->
    <PostSectionHeader
      :title="title"
      :count="posts.length"
      :icon="icon"
      @filter="emit('filter')"
      @sort="emit('sort')"
      @export="emit('export')"
    />

    <!-- Loading State -->
    <div v-if="loading" class="py-8 flex justify-center">
      <Spinner />
    </div>

    <!-- Empty State -->
    <EmptyState
      v-else-if="posts.length === 0"
      :message="t('post.noPosts')"
    />

    <!-- Posts Grid -->
    <div v-else class="grid gap-4">
      <component
        :is="cardComponent"
        v-for="post in posts"
        :key="post.id"
        :post="post"
      />
    </div>
  </div>
</template>
