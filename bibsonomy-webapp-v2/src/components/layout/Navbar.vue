<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { Home, Menu, X } from 'lucide-vue-next'
import NavbarItem from './NavbarItem.vue'

const { t } = useI18n()
const mobileMenuOpen = ref(false)

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

const closeMobileMenu = () => {
  mobileMenuOpen.value = false
}
</script>

<template>
  <nav class="bg-primary-600 max-w-[1170px] mx-auto">
    <div class="border-x border-primary-700">
      <!-- Desktop menu -->
      <ul class="hidden md:flex list-none m-0 p-0 flex-wrap text-sm">
        <!-- Home -->
        <NavbarItem to="/" :icon="Home" />

        <!-- Posts -->
        <NavbarItem to="/error/not-implemented?feature=Posts">
          {{ t('post.posts') }}
        </NavbarItem>

        <!-- Search -->
        <NavbarItem to="/error/not-implemented?feature=Search">
          {{ t('search.search') }}
        </NavbarItem>

        <!-- Right side navigation -->
        <!-- Login -->
        <NavbarItem to="/error/not-implemented?feature=Login" class="ml-auto">
          {{ t('common.login') }}
        </NavbarItem>

        <!-- Register -->
        <NavbarItem to="/error/not-implemented?feature=Register">
          {{ t('home.register') }}
        </NavbarItem>
      </ul>

      <!-- Mobile menu button and header -->
      <div class="md:hidden flex items-center justify-between py-3 px-4">
        <a href="/" class="text-gray-100 font-medium flex items-center gap-2">
          <Home :size="20" />
          <span>{{ t('nav.home') }}</span>
        </a>
        <button
          @click="toggleMobileMenu"
          class="text-gray-100 p-2 hover:bg-primary-700 rounded transition-colors"
          :aria-label="mobileMenuOpen ? 'Close menu' : 'Open menu'"
        >
          <Menu v-if="!mobileMenuOpen" :size="24" />
          <X v-else :size="24" />
        </button>
      </div>

      <!-- Mobile menu (collapsible) -->
      <ul
        v-if="mobileMenuOpen"
        class="md:hidden list-none m-0 p-0 pb-2 text-sm border-t border-primary-700"
      >
        <NavbarItem to="/" @click="closeMobileMenu">
          {{ t('nav.home') }}
        </NavbarItem>

        <NavbarItem to="/error/not-implemented?feature=Posts" @click="closeMobileMenu">
          {{ t('post.posts') }}
        </NavbarItem>

        <NavbarItem to="/error/not-implemented?feature=Search" @click="closeMobileMenu">
          {{ t('search.search') }}
        </NavbarItem>

        <NavbarItem to="/error/not-implemented?feature=Login" @click="closeMobileMenu">
          {{ t('common.login') }}
        </NavbarItem>

        <NavbarItem to="/error/not-implemented?feature=Register" @click="closeMobileMenu">
          {{ t('home.register') }}
        </NavbarItem>
      </ul>
    </div>
  </nav>
</template>
