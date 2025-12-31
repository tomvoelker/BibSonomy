<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import MainLayout from '@/components/MainLayout.vue'
import { AlertTriangle } from 'lucide-vue-next'

const { t } = useI18n()
const router = useRouter()

const goHome = () => {
  router.push('/')
}

const tryAgain = () => {
  window.location.reload()
}
</script>

<template>
  <MainLayout>
    <div
      class="bg-white border-l border-r border-gray-300 max-w-[1170px] mx-auto py-10 px-4 min-h-[500px]"
    >
      <div class="max-w-[600px] mx-auto text-center">
        <h1 class="text-4xl font-medium leading-tight mt-5 mb-5 text-gray-800">
          {{ t('error.serverErrorHeader') }}
        </h1>

        <!-- Danger Alert -->
        <div
          class="p-4 mb-5 border border-danger-200 rounded bg-danger-50 text-danger-700 text-left"
        >
          <AlertTriangle :size="16" class="inline-block align-middle mr-2" />
          {{ t('error.serverError') }}
        </div>

        <!-- Action Buttons -->
        <div class="mt-8">
          <button
            type="button"
            class="px-4 py-2.5 text-lg leading-normal rounded-md text-white bg-success-600 border border-success-700 cursor-pointer mr-2.5 hover:bg-success-700 transition-colors"
            @click="tryAgain"
          >
            {{ t('error.tryAgain') }}
          </button>

          <button
            type="button"
            class="px-4 py-2.5 text-lg leading-normal rounded-md text-gray-800 bg-white border border-gray-300 cursor-pointer hover:bg-gray-100 transition-colors"
            @click="goHome"
          >
            {{ t('error.goHome') }}
          </button>
        </div>
      </div>
    </div>
  </MainLayout>
</template>
