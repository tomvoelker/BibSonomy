<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { usePreferencesStore } from '@/store/preferences'

const { locale } = useI18n()
const preferences = usePreferencesStore()

const changeLanguage = (lang: 'de' | 'en') => {
  locale.value = lang
  preferences.setLocale(lang)
}
</script>

<template>
  <div class="text-right text-sm">
    <span class="text-gray-600">(&nbsp;</span>
    <button
      type="button"
      @click="changeLanguage('en')"
      class="text-primary-600 bg-transparent border-0 p-0 cursor-pointer hover:underline"
      :class="{ 'font-bold': locale === 'en' }"
    >
      en
    </button>
    <span class="text-gray-600">&nbsp;|&nbsp;</span>
    <button
      type="button"
      @click="changeLanguage('de')"
      class="text-primary-600 bg-transparent border-0 p-0 cursor-pointer hover:underline"
      :class="{ 'font-bold': locale === 'de' }"
    >
      de
    </button>
    <span class="text-gray-600">&nbsp;)</span>
  </div>
</template>
