<script setup lang="ts">
import { RouterView } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { usePreferencesStore } from './store/preferences'

const { locale } = useI18n()
const preferencesStore = usePreferencesStore()

// Sync i18n locale with preferences store
locale.value = preferencesStore.locale
</script>

<template>
  <div id="app" class="min-h-screen bg-white dark:bg-slate-900 text-slate-900 dark:text-slate-50">
    <!-- Skip to main content link for accessibility -->
    <a href="#main-content" class="skip-to-main">
      {{ $t('common.skipToMain') || 'Skip to main content' }}
    </a>

    <!-- Router view - pages will be rendered here -->
    <RouterView id="main-content" />
  </div>
</template>

<style>
/* Global styles are in src/assets/main.css */
</style>
