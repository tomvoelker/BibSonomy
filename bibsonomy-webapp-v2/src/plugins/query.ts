/**
 * Vue Query (TanStack Query) configuration
 */

import { VueQueryPlugin, QueryClient } from '@tanstack/vue-query'
import type { App } from 'vue'

// Create a client
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5, // 5 minutes
      gcTime: 1000 * 60 * 30, // 30 minutes (formerly cacheTime)
      refetchOnWindowFocus: false,
      retry: 1,
    },
    mutations: {
      retry: 1,
    },
  },
})

export function installVueQuery(app: App) {
  app.use(VueQueryPlugin, {
    queryClient,
  })
}
