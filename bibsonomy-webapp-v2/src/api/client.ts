/**
 * Axios client configuration
 */

import axios from 'axios'
import { useAuthStore } from '@/store/auth'

export const apiClient = axios.create({
  // Use relative URL for MSW to work, or full URL in production
  baseURL:
    import.meta.env['VITE_ENABLE_MOCKS'] === 'true'
      ? '/api/v2'
      : ((import.meta.env['VITE_API_BASE_URL'] as string | undefined) ?? '/api/v2'),
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
        const user = JSON.parse(userJson) as { name: string }
        // Backend expects Basic auth with username:apikey
        const credentials = btoa(`${user.name}:${token}`)
        config.headers.Authorization = `Basic ${credentials}`
      } catch {
        // Invalid stored user data, skip auth header
      }
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor (handle common errors)
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
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
