<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Search } from 'lucide-vue-next'

const { t } = useI18n()
const router = useRouter()

const searchQuery = ref('')

const handleSearch = () => {
  router.push({ path: '/error/not-implemented', query: { feature: 'Search' } })
}
</script>

<template>
  <div id="search">
    <form @submit.prevent="handleSearch" class="m-0">
      <div class="relative flex border-collapse">
        <!-- Search Scope Dropdown -->
        <div class="flex-shrink-0">
          <span
            class="flex items-center bg-gray-200 border border-gray-300 border-r-0 px-3 py-1.5 text-sm text-gray-600 rounded-l"
          >
            <span>{{ t('search.search') }}</span>
            <span class="ml-1">▼</span>
          </span>
        </div>

        <!-- Search Input -->
        <input
          v-model="searchQuery"
          type="text"
          class="flex-1 h-[34px] px-3 py-1.5 text-sm text-gray-600 bg-white border border-gray-300 rounded-none shadow-inner focus:outline-none focus:ring-1 focus:ring-primary-500 focus:border-primary-500"
        />

        <!-- Search Button -->
        <button
          type="submit"
          class="flex-shrink-0 px-3 py-1.5 text-sm text-gray-800 bg-white border border-gray-300 border-l-0 rounded-r cursor-pointer h-[34px] hover:bg-gray-50 focus:outline-none focus:ring-1 focus:ring-primary-500"
        >
          <Search :size="14" class="align-middle" />
        </button>
      </div>
    </form>
  </div>
</template>
