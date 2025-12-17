<script setup lang="ts">
import { useBranding } from '@/composables/useBranding'
import LanguageSwitcher from './LanguageSwitcher.vue'
import SearchBar from './SearchBar.vue'

const { branding } = useBranding()
</script>

<template>
  <div class="bg-white border-x border-gray-300 max-w-[1170px] mx-auto mt-4 md:mt-6">
    <div class="px-4 py-5 md:px-6 md:py-6">
      <div class="flex flex-col gap-4 md:flex-row md:items-center md:gap-6">
        <!-- Left: Logo (full width on mobile, half on desktop) -->
        <div class="w-full md:w-1/2">
          <h1 class="text-3xl md:text-4xl font-medium leading-tight mb-1 text-primary-600">
            <a href="/" class="text-primary-600 no-underline hover:underline">
              {{ branding.projectName }}
            </a>
          </h1>
          <p class="text-gray-600 text-sm m-0">
            {{ branding.tagline }}
          </p>
        </div>

        <!-- Right: Language + Search (full width on mobile, half on desktop) -->
        <div class="w-full md:w-1/2 flex flex-col gap-2.5">
          <!-- Language Switcher -->
          <LanguageSwitcher />

          <!-- Search Bar -->
          <SearchBar />
        </div>
      </div>
    </div>
  </div>
</template>
