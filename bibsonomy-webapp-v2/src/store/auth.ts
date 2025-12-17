/**
 * Authentication store
 * Manages user authentication state and tokens
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface User {
  id: string
  name: string
  firstName: string
  lastName: string
  email: string
}

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)

  // Computed
  const isAuthenticated = computed(() => user.value !== null && token.value !== null)

  // Actions
  function setAuth(userData: User, authToken: string) {
    user.value = userData
    token.value = authToken
    // Persist to localStorage
    localStorage.setItem('auth_token', authToken)
    localStorage.setItem('auth_user', JSON.stringify(userData))
  }

  function clearAuth() {
    user.value = null
    token.value = null
    // Clear from localStorage
    localStorage.removeItem('auth_token')
    localStorage.removeItem('auth_user')
  }

  function loadAuthFromStorage() {
    const storedToken = localStorage.getItem('auth_token')
    const storedUser = localStorage.getItem('auth_user')

    if (storedToken && storedUser) {
      try {
        token.value = storedToken
        user.value = JSON.parse(storedUser) as User
      } catch (error) {
        console.error('Failed to parse stored user data:', error)
        clearAuth()
      }
    }
  }

  function login(_username: string, _password: string): Promise<void> {
    // TODO: Implement actual login API call
    // For now, this is a placeholder
    return Promise.reject(new Error('Login not implemented yet'))
  }

  function logout() {
    // TODO: Implement actual logout API call
    clearAuth()
  }

  // Initialize from localStorage on store creation
  loadAuthFromStorage()

  return {
    // State
    user,
    token,
    // Computed
    isAuthenticated,
    // Actions
    setAuth,
    clearAuth,
    login,
    logout,
  }
})
