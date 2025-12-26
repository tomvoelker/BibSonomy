<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { useRouter, useRoute } from 'vue-router'
import MainLayout from '@/components/MainLayout.vue'
import { Construction } from 'lucide-vue-next'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()

const featureName = (route.query.feature as string) || 'This feature'

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.back()
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
        <!-- Construction Icon -->
        <div style="margin: 20px 0">
          <Construction :size="80" style="color: #f0ad4e" />
        </div>

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
          {{ t('error.notImplementedHeader') }}
        </h1>

        <!-- Warning Alert -->
        <div
          style="
            padding: 15px;
            margin-bottom: 20px;
            border: 1px solid #faebcc;
            border-radius: 4px;
            background-color: #fcf8e3;
            color: #8a6d3b;
            text-align: left;
          "
        >
          <strong style="display: block; margin-bottom: 10px">{{ featureName }}</strong>
          {{ t('error.notImplementedMessage') }}
        </div>

        <!-- Info Box -->
        <div
          style="
            padding: 20px;
            margin: 30px 0;
            border: 1px solid #bce8f1;
            border-radius: 4px;
            background-color: #d9edf7;
            color: #31708f;
            text-align: left;
          "
        >
          <h4 style="margin-top: 0; margin-bottom: 10px; font-size: 16px; font-weight: bold">
            Currently Implemented Features:
          </h4>
          <ul style="margin: 10px 0; padding-left: 20px">
            <li style="margin: 5px 0">Browse posts (bookmarks and publications)</li>
            <li style="margin: 5px 0">View post details</li>
            <li style="margin: 5px 0">Language switching (English/German)</li>
            <li style="margin: 5px 0">Responsive design matching original BibSonomy</li>
          </ul>
        </div>

        <!-- Action Buttons -->
        <div style="margin-top: 30px">
          <button
            @click="goBack"
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
              margin-right: 10px;
            "
            @mouseover="($event.target as HTMLElement).style.backgroundColor = '#3071a9'"
            @mouseout="($event.target as HTMLElement).style.backgroundColor = '#428bca'"
          >
            {{ t('common.back') }}
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
