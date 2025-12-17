/**
 * MSW browser worker setup
 * Used in development mode to intercept requests
 */

import { setupWorker } from 'msw/browser'
import { handlers } from './handlers'

export const worker = setupWorker(...handlers)

/**
 * Start the MSW worker in development mode
 */
export async function startMockWorker() {
  if (import.meta.env.DEV && import.meta.env.VITE_ENABLE_MOCKS === 'true') {
    await worker.start({
      onUnhandledRequest: 'bypass', // Don't warn about unhandled requests
    })
    console.log('[MSW] Mocking enabled - API requests will be intercepted')
  }
}
