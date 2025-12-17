<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { locale } = useI18n()

const changeLanguage = (lang: 'de' | 'en') => {
  locale.value = lang
}
</script>

<template>
  <div class="text-right text-sm">
    <span class="text-gray-600">(&nbsp;</span>
    <a
      href="#"
      @click.prevent="changeLanguage('en')"
      class="text-primary-600 no-underline hover:underline"
      :class="{ 'font-bold': locale === 'en' }"
    >
      en
    </a>
    <span class="text-gray-600">&nbsp;|&nbsp;</span>
    <a
      href="#"
      @click.prevent="changeLanguage('de')"
      class="text-primary-600 no-underline hover:underline"
      :class="{ 'font-bold': locale === 'de' }"
    >
      de
    </a>
    <span class="text-gray-600">&nbsp;)</span>
  </div>
</template>
