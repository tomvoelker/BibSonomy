/**
 * MSW server setup
 * Used in Node.js environment (Vitest tests)
 */

import { setupServer } from 'msw/node'
import { handlers } from './handlers'

export const server = setupServer(...handlers)
