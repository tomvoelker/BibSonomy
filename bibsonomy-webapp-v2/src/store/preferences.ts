/**
 * User preferences store
 * Manages user settings like language, theme, display options
 */

import { defineStore } from 'pinia'
import { ref, onScopeDispose } from 'vue'

export type Locale = 'en' | 'de'
export type Theme = 'light' | 'dark' | 'system'

// Maximum posts per page to prevent performance issues
const MAX_POSTS_PER_PAGE = 100

// Helper for safe localStorage operations (can fail in private browsing or quota exceeded)
function safeSetItem(key: string, value: string): void {
  try {
    localStorage.setItem(key, value)
  } catch (error) {
    console.warn(`Failed to save preference '${key}' to localStorage:`, error)
  }
}

export const usePreferencesStore = defineStore('preferences', () => {
  // State
  const locale = ref<Locale>('de') // Default to German as per requirements
  const theme = ref<Theme>('system')
  const postsPerPage = ref(10)

  // Actions
  function setLocale(newLocale: Locale) {
    locale.value = newLocale
    safeSetItem('pref_locale', newLocale)
  }

  function setTheme(newTheme: Theme) {
    theme.value = newTheme
    safeSetItem('pref_theme', newTheme)
    applyTheme(newTheme)
  }

  function setPostsPerPage(count: number) {
    // Clamp value between 1 and MAX_POSTS_PER_PAGE
    const validCount = Math.max(1, Math.min(count, MAX_POSTS_PER_PAGE))
    postsPerPage.value = validCount
    safeSetItem('pref_posts_per_page', validCount.toString())
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
  const handleMediaChange = () => {
    if (theme.value === 'system') {
      applyTheme('system')
    }
  }
  mediaQuery.addEventListener('change', handleMediaChange)

  // Register cleanup with Vue's scope dispose (automatically called when store is disposed)
  onScopeDispose(() => {
    mediaQuery.removeEventListener('change', handleMediaChange)
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
