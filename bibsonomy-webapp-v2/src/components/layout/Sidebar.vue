<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useTags } from '@/composables/useTags'
import type { Tag } from '@/types/models'

const { t } = useI18n()

const fallbackTags: Tag[] = [
  { name: 'deep-learning', count: 150 },
  { name: 'machine-learning', count: 200 },
  { name: 'neural-networks', count: 120 },
  { name: 'computer-vision', count: 95 },
  { name: 'nlp', count: 85 },
  { name: 'python', count: 180 },
  { name: 'tensorflow', count: 110 },
  { name: 'pytorch', count: 105 },
  { name: 'keras', count: 90 },
  { name: 'scikit-learn', count: 75 },
  { name: 'pandas', count: 140 },
  { name: 'numpy', count: 130 },
]

const { data, isError } = useTags({ limit: 50, maxCount: 50 })

const resolvedTags = computed(() => {
  if (isError.value || !data.value || data.value.length === 0) {
    return fallbackTags
  }
  return data.value
})

const sortedTags = computed(() => {
  return [...resolvedTags.value].sort((a, b) => a.name.localeCompare(b.name))
})

// Calculate tag sizes and colors based on count
const tagSizes = computed(() => {
  const counts = sortedTags.value.map((tag) => tag.countPublic ?? tag.count ?? 1)
  const minCount = Math.min(...counts)
  const maxCount = Math.max(...counts)
  const range = maxCount - minCount

  return sortedTags.value.map((tag) => {
    const count = tag.countPublic ?? tag.count ?? 1
    // Scale from 0.85em to 1.8em
    const normalized = range > 0 ? (count - minCount) / range : 0.5
    const size = 0.85 + normalized * 0.95

    // Determine opacity/weight based on popularity (higher = more prominent)
    const opacity = 0.6 + normalized * 0.4 // 0.6 to 1.0
    const weight = normalized > 0.7 ? '600' : normalized > 0.4 ? '500' : '400'

    return {
      ...tag,
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
      <div class="flex flex-wrap gap-x-3 gap-y-2 items-center justify-center leading-relaxed">
        <a
          v-for="tag in tagSizes"
          :key="tag.name"
          :href="`/error/not-implemented?feature=Tag+Page`"
          class="text-primary-600 hover:text-primary-800 no-underline transition-all hover:scale-105"
          :style="{
            fontSize: tag.fontSize,
            opacity: tag.opacity,
            fontWeight: tag.fontWeight,
          }"
        >
          {{ tag.name }}
        </a>
      </div>
    </div>

    <!-- Recent Activity (placeholder) -->
    <div class="pt-4 border-t border-gray-300">
      <h3 class="text-sm font-bold text-gray-800 mb-3">Recent Activity</h3>
      <p class="text-sm text-gray-600">Recent posts will appear here...</p>
    </div>
  </aside>
</template>
