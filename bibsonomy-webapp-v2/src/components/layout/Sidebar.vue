<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useTags } from '@/composables/useTags'

const { t } = useI18n()

// Fetch globally popular tags from the API (sorted by frequency, descending)
// Limit to 50 tags to match legacy webapp behavior
const { data: tagsData, isLoading, isError } = useTags({ limit: 50 })

// Calculate tag sizes and colors based on global count
const tagSizes = computed(() => {
  const tags = tagsData.value || []
  if (tags.length === 0) return []

  // Use 'count' field which contains the global usage count
  const counts = tags.map((tag) => tag.count ?? 0)
  const minCount = Math.min(...counts)
  const maxCount = Math.max(...counts)
  const range = maxCount - minCount

  return tags.map((tag) => {
    const count = tag.count ?? 0
    // Scale from 0.85em to 1.8em
    const normalized = range > 0 ? (count - minCount) / range : 0.5
    const size = 0.85 + normalized * 0.95

    // Determine opacity/weight based on popularity (higher = more prominent)
    const opacity = 0.6 + normalized * 0.4 // 0.6 to 1.0
    const weight = normalized > 0.7 ? '600' : normalized > 0.4 ? '500' : '400'

    return {
      name: tag.name,
      count,
      fontSize: `${size}em`,
      opacity,
      fontWeight: weight,
    }
  })
})
</script>

<template>
  <aside class="bg-gray-100 p-4 rounded space-y-4">
    <!-- Popular Tags Cloud -->
    <div>
      <h3 class="text-sm font-bold text-gray-800 mb-3">
        {{ t('tag.popular') }}
      </h3>

      <!-- Loading state -->
      <div v-if="isLoading" class="text-sm text-gray-500 text-center py-2">
        {{ t('common.loading') }}...
      </div>

      <!-- Error state -->
      <div v-else-if="isError" class="text-sm text-red-500 text-center py-2">
        {{ t('error.loadFailed') }}
      </div>

      <!-- Empty state -->
      <div v-else-if="tagSizes.length === 0" class="text-sm text-gray-500 text-center py-2">
        {{ t('tag.noTags') }}
      </div>

      <!-- Tags cloud -->
      <div
        v-else
        class="flex flex-wrap gap-x-3 gap-y-2 items-center justify-center leading-relaxed"
      >
        <RouterLink
          v-for="tag in tagSizes"
          :key="tag.name"
          :to="`/error/not-implemented?feature=Tag+Page`"
          class="text-primary-600 hover:text-primary-800 no-underline transition-all hover:scale-105"
          :style="{
            fontSize: tag.fontSize,
            opacity: tag.opacity,
            fontWeight: tag.fontWeight,
          }"
        >
          {{ tag.name }}
        </RouterLink>
      </div>
    </div>

    <!-- Recent Activity (placeholder) -->
    <div class="pt-4 border-t border-gray-300">
      <h3 class="text-sm font-bold text-gray-800 mb-3">Recent Activity</h3>
      <p class="text-sm text-gray-600">Recent posts will appear here...</p>
    </div>
  </aside>
</template>
