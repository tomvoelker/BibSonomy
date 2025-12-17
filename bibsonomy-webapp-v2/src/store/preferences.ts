/**
 * User preferences store
 * Manages user settings like language, theme, display options
 */

import { defineStore } from 'pinia'
import { ref } from 'vue'

export type Locale = 'en' | 'de'
export type Theme = 'light' | 'dark' | 'system'

export const usePreferencesStore = defineStore('preferences', () => {
  // State
  const locale = ref<Locale>('de') // Default to German as per requirements
  const theme = ref<Theme>('system')
  const postsPerPage = ref(10)

  // Actions
  function setLocale(newLocale: Locale) {
    locale.value = newLocale
    localStorage.setItem('pref_locale', newLocale)
  }

  function setTheme(newTheme: Theme) {
    theme.value = newTheme
    localStorage.setItem('pref_theme', newTheme)
    applyTheme(newTheme)
  }

  function setPostsPerPage(count: number) {
    postsPerPage.value = count
    localStorage.setItem('pref_posts_per_page', count.toString())
  }

  function applyTheme(themeValue: Theme) {
    const root = document.documentElement

    if (themeValue === 'dark') {
      root.classList.add('dark')
    } else if (themeValue === 'light') {
      root.classList.remove('dark')
    } else {
      // System preference
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
      if (prefersDark) {
        root.classList.add('dark')
      } else {
        root.classList.remove('dark')
      }
    }
  }

  function loadPreferencesFromStorage() {
    // Load locale
    const storedLocale = localStorage.getItem('pref_locale') as Locale | null
    if (storedLocale && (storedLocale === 'en' || storedLocale === 'de')) {
      locale.value = storedLocale
    }

    // Load theme
    const storedTheme = localStorage.getItem('pref_theme') as Theme | null
    if (storedTheme && ['light', 'dark', 'system'].includes(storedTheme)) {
      theme.value = storedTheme
      applyTheme(storedTheme)
    } else {
      applyTheme('system')
    }

    // Load posts per page
    const storedPostsPerPage = localStorage.getItem('pref_posts_per_page')
    if (storedPostsPerPage) {
      const count = parseInt(storedPostsPerPage, 10)
      if (!isNaN(count) && count > 0) {
        postsPerPage.value = count
      }
    }
  }

  // Watch for system theme changes
  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
  mediaQuery.addEventListener('change', () => {
    if (theme.value === 'system') {
      applyTheme('system')
    }
  })

  // Initialize from localStorage
  loadPreferencesFromStorage()

  return {
    // State
    locale,
    theme,
    postsPerPage,
    // Actions
    setLocale,
    setTheme,
    setPostsPerPage,
  }
})
