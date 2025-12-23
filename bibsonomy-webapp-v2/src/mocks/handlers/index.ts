/**
 * MSW handlers index
 * Combines all handler modules
 */

import { postsHandlers } from './posts'
import { tagsHandlers } from './tags'

export const handlers = [
  ...postsHandlers,
  ...tagsHandlers,
  // Add more handlers as they are created:
  // ...usersHandlers,
  // ...tagsHandlers,
  // ...authHandlers,
]
