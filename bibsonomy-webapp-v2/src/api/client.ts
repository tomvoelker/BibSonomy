/**
 * Axios client configuration
 */

import axios from 'axios'

export const apiClient = axios.create({
  // Use relative URL for MSW to work, or full URL in production
  baseURL:
    import.meta.env.VITE_ENABLE_MOCKS === 'true'
      ? '/api/v2'
      : import.meta.env.VITE_API_BASE_URL || '/api/v2',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
})

// Request interceptor (add auth token if available)
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
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
      // Clear auth and redirect to login
      localStorage.removeItem('auth_token')
      localStorage.removeItem('auth_user')
      // You could dispatch a Pinia action here or emit an event
      console.warn('Session expired. Please log in again.')
    }

    return Promise.reject(error)
  }
)
