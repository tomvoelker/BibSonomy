/**
 * UI state store
 * Manages global UI state like modals, toasts, sidebar, etc.
 */

import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface Toast {
  id: string
  type: 'success' | 'error' | 'warning' | 'info'
  message: string
  duration?: number
}

export const useUIStore = defineStore('ui', () => {
  // State
  const sidebarOpen = ref(false)
  const toasts = ref<Toast[]>([])
  // Track timer IDs for cleanup
  const toastTimers = new Map<string, ReturnType<typeof setTimeout>>()

  // Actions
  function toggleSidebar() {
    sidebarOpen.value = !sidebarOpen.value
  }

  function openSidebar() {
    sidebarOpen.value = true
  }

  function closeSidebar() {
    sidebarOpen.value = false
  }

  function showToast(toast: Omit<Toast, 'id'>) {
    const id = Math.random().toString(36).substring(7)
    const newToast: Toast = {
      id,
      ...toast,
      duration: toast.duration ?? 5000,
    }

    toasts.value.push(newToast)

    // Auto-remove after duration
    if (newToast.duration && newToast.duration > 0) {
      const timerId = setTimeout(() => {
        toastTimers.delete(id)
        removeToast(id)
      }, newToast.duration)
      toastTimers.set(id, timerId)
    }

    return id
  }

  function removeToast(id: string) {
    // Cancel pending timer if exists
    const timerId = toastTimers.get(id)
    if (timerId) {
      clearTimeout(timerId)
      toastTimers.delete(id)
    }
    toasts.value = toasts.value.filter((t) => t.id !== id)
  }

  function clearToasts() {
    // Cancel all pending timers
    toastTimers.forEach((timerId) => clearTimeout(timerId))
    toastTimers.clear()
    toasts.value = []
  }

  return {
    // State
    sidebarOpen,
    toasts,
    // Actions
    toggleSidebar,
    openSidebar,
    closeSidebar,
    showToast,
    removeToast,
    clearToasts,
  }
})
