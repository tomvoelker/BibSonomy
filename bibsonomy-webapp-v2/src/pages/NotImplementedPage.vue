<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { useRouter, useRoute } from 'vue-router'
import MainLayout from '@/components/MainLayout.vue'
import { Construction } from 'lucide-vue-next'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()

const featureName = (route.query['feature'] as string) || 'This feature'

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.back()
}
</script>

<template>
  <MainLayout>
    <div class="bg-white border-l border-r border-gray-300 max-w-[1170px] mx-auto py-10 px-4 min-h-[500px]">
      <div class="max-w-[600px] mx-auto text-center">
        <!-- Construction Icon -->
        <div class="my-5">
          <Construction :size="80" class="text-warning-500" />
        </div>

        <h1 class="text-4xl font-medium leading-tight mt-5 mb-5 text-gray-800">
          {{ t('error.notImplementedHeader') }}
        </h1>

        <!-- Warning Alert -->
        <div class="p-4 mb-5 border border-warning-200 rounded bg-warning-50 text-warning-800 text-left">
          <strong class="block mb-2.5">{{ featureName }}</strong>
          {{ t('error.notImplementedMessage') }}
        </div>

        <!-- Info Box -->
        <div class="p-5 my-8 border border-info-200 rounded bg-info-50 text-info-700 text-left">
          <h4 class="mt-0 mb-2.5 text-base font-bold">
            Currently Implemented Features:
          </h4>
          <ul class="my-2.5 pl-5 list-disc">
            <li class="my-1">Browse posts (bookmarks and publications)</li>
            <li class="my-1">View post details</li>
            <li class="my-1">Language switching (English/German)</li>
            <li class="my-1">Responsive design matching original BibSonomy</li>
          </ul>
        </div>

        <!-- Action Buttons -->
        <div class="mt-8">
          <button
            @click="goBack"
            type="button"
            class="px-4 py-2.5 text-lg leading-normal rounded-md text-white bg-primary-600 border border-primary-700 cursor-pointer mr-2.5 hover:bg-primary-700 transition-colors"
          >
            {{ t('common.back') }}
          </button>

          <button
            @click="goHome"
            type="button"
            class="px-4 py-2.5 text-lg leading-normal rounded-md text-gray-800 bg-white border border-gray-300 cursor-pointer hover:bg-gray-100 transition-colors"
          >
            {{ t('error.goHome') }}
          </button>
        </div>
      </div>
    </div>
  </MainLayout>
</template>
