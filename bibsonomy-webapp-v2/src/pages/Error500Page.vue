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
          {{ t('error.serverErrorHeader') }}
        </h1>

        <!-- Danger Alert -->
        <div
          style="
            padding: 15px;
            margin-bottom: 20px;
            border: 1px solid #ebccd1;
            border-radius: 4px;
            background-color: #f2dede;
            color: #a94442;
            text-align: left;
          "
        >
          <AlertTriangle
            :size="16"
            style="display: inline-block; vertical-align: middle; margin-right: 8px"
          />
          {{ t('error.serverError') }}
        </div>

        <!-- Action Buttons -->
        <div style="margin-top: 30px">
          <button
            @click="tryAgain"
            type="button"
            style="
              padding: 10px 16px;
              font-size: 18px;
              line-height: 1.33;
              border-radius: 6px;
              color: #fff;
              background-color: #5cb85c;
              border: 1px solid #4cae4c;
              cursor: pointer;
              margin-right: 10px;
            "
            @mouseover="($event.target as HTMLElement).style.backgroundColor = '#449d44'"
            @mouseout="($event.target as HTMLElement).style.backgroundColor = '#5cb85c'"
          >
            {{ t('error.tryAgain') }}
          </button>

          <button
            @click="goHome"
            type="button"
            style="
              padding: 10px 16px;
              font-size: 18px;
              line-height: 1.33;
              border-radius: 6px;
              color: #333;
              background-color: #fff;
              border: 1px solid #ccc;
              cursor: pointer;
            "
            @mouseover="($event.target as HTMLElement).style.backgroundColor = '#e6e6e6'"
            @mouseout="($event.target as HTMLElement).style.backgroundColor = '#fff'"
          >
            {{ t('error.goHome') }}
          </button>
        </div>
      </div>
    </div>
  </MainLayout>
</template>
