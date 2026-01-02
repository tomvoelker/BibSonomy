/**
 * Axios client configuration
 */

import axios, { type AxiosError } from 'axios'
import { useAuthStore } from '@/store/auth'

// Get environment variables with proper typing
const enableMocks = String(import.meta.env['VITE_ENABLE_MOCKS']) === 'true'
const apiBaseUrl = String(import.meta.env['VITE_API_BASE_URL'] || '/api/v2')

export const apiClient = axios.create({
  // Use relative URL for MSW to work, or full URL in production
  baseURL: enableMocks ? '/api/v2' : apiBaseUrl,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
})

// Request interceptor (add auth credentials if available)
// Backend uses Basic auth with username:apikey format
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token')
    const userJson = localStorage.getItem('auth_user')
    if (token && userJson) {
      try {
        const user = JSON.parse(userJson) as { username: string }
        // Backend expects Basic auth with username:apikey
        const credentials = btoa(`${user.username}:${token}`)
        config.headers.Authorization = `Basic ${credentials}`
      } catch (e) {
        // Invalid stored user data, skip auth header
        console.warn('Failed to parse stored auth user data, skipping auth header:', e)
      }
    }
    return config
  },
  (error: unknown) => {
    const axiosError = error instanceof Error ? error : new Error('Request interceptor error')
    return Promise.reject(axiosError)
  }
)

// Response interceptor (handle common errors)
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    // Handle 401 Unauthorized
    if (error.response?.status === 401) {
      // Clear auth via store (centralizes state management)
      const authStore = useAuthStore()
      authStore.clearAuth()
      console.warn('Session expired. Please log in again.')
    }

    return Promise.reject(error)
  }
)
