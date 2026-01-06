/**
 * MSW handlers index
 * Combines all handler modules
 */

import { postsHandlers } from './posts'

export const handlers = [
  ...postsHandlers,
  // Add more handlers as they are created:
  // ...usersHandlers,
  // ...tagsHandlers,
  // ...authHandlers,
]
