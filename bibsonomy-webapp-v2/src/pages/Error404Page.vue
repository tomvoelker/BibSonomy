<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import MainLayout from '@/components/MainLayout.vue'
import { Info } from 'lucide-vue-next'

const { t } = useI18n()
const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<template>
  <MainLayout>
    <div
      class="bg-white border-l border-r border-gray-300 max-w-[1170px] mx-auto py-10 px-4 min-h-[500px]"
    >
      <div class="max-w-[600px] mx-auto text-center">
        <h1 class="text-4xl font-medium leading-tight mt-5 mb-5 text-gray-800">
          {{ t('error.notFoundHeader') }}
        </h1>

        <!-- Info Alert -->
        <div class="p-4 mb-5 border border-info-200 rounded bg-info-50 text-info-700 text-left">
          <Info :size="16" class="inline-block align-middle mr-2" />
          {{ t('error.notFound') }}
        </div>

        <!-- 404 Image -->
        <img src="/404.jpg" alt="404 Error" class="max-w-full h-auto my-5 mx-auto rounded block" />

        <!-- Go Home Button -->
        <button
          type="button"
          class="px-4 py-2.5 text-lg leading-normal rounded-md text-white bg-primary-600 border border-primary-700 cursor-pointer mt-5 hover:bg-primary-700 transition-colors"
          @click="goHome"
        >
          {{ t('error.goHome') }}
        </button>
      </div>
    </div>
  </MainLayout>
</template>
