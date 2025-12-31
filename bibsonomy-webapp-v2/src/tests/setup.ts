/**
 * Vitest setup file
 * Runs before each test file
 */

import { afterEach, beforeAll, afterAll, vi } from 'vitest'
import { config } from '@vue/test-utils'
import { server } from '../mocks/server'

// Set up MSW server for tests
beforeAll(() => {
  server.listen({ onUnhandledRequest: 'error' })
})

afterAll(() => {
  server.close()
})

// Cleanup after each test
afterEach(() => {
  server.resetHandlers() // Reset MSW handlers between tests
})

// Extend Vitest matchers if needed
// expect.extend({})

// Mock window.matchMedia for components that use it
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: (query: string) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: () => {}, // deprecated
    removeListener: () => {}, // deprecated
    addEventListener: () => {},
    removeEventListener: () => {},
    dispatchEvent: () => true,
  }),
})

// Mock IntersectionObserver for components that use it
// eslint-disable-next-line @typescript-eslint/no-unsafe-assignment, @typescript-eslint/no-unsafe-member-access
;(globalThis as Record<string, unknown>).IntersectionObserver = class IntersectionObserver {
  constructor() {}
  disconnect() {}
  observe() {}
  takeRecords() {
    return []
  }
  unobserve() {}
}

// Mock ResizeObserver for components that use it
// eslint-disable-next-line @typescript-eslint/no-unsafe-assignment, @typescript-eslint/no-unsafe-member-access
;(globalThis as Record<string, unknown>).ResizeObserver = class ResizeObserver {
  constructor() {}
  disconnect() {}
  observe() {}
  unobserve() {}
}
