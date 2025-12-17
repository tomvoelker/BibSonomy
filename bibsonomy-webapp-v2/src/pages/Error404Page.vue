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
      style="
        background: white;
        border-left: 1px solid #cbcbcb;
        border-right: 1px solid #cbcbcb;
        max-width: 1170px;
        margin: 0 auto;
        padding: 40px 15px;
        min-height: 500px;
      "
    >
      <div style="max-width: 600px; margin: 0 auto; text-align: center">
        <h1
          style="
            font-size: 36px;
            font-weight: 500;
            line-height: 1.1;
            margin-top: 20px;
            margin-bottom: 20px;
            color: #333;
          "
        >
          {{ t('error.notFoundHeader') }}
        </h1>

        <!-- Info Alert -->
        <div
          style="
            padding: 15px;
            margin-bottom: 20px;
            border: 1px solid #bce8f1;
            border-radius: 4px;
            background-color: #d9edf7;
            color: #31708f;
            text-align: left;
          "
        >
          <Info
            :size="16"
            style="display: inline-block; vertical-align: middle; margin-right: 8px"
          />
          {{ t('error.notFound') }}
        </div>

        <!-- 404 Image -->
        <img
          src="/404.jpg"
          alt="404 Error"
          style="max-width: 100%; height: auto; margin: 20px auto; border-radius: 4px; display: block"
        />

        <!-- Go Home Button -->
        <button
          @click="goHome"
          type="button"
          style="
            padding: 10px 16px;
            font-size: 18px;
            line-height: 1.33;
            border-radius: 6px;
            color: #fff;
            background-color: #428bca;
            border: 1px solid #357ebd;
            cursor: pointer;
            margin-top: 20px;
          "
          @mouseover="($event.target as HTMLElement).style.backgroundColor = '#3071a9'"
          @mouseout="($event.target as HTMLElement).style.backgroundColor = '#428bca'"
        >
          {{ t('error.goHome') }}
        </button>
      </div>
    </div>
  </MainLayout>
</template>
