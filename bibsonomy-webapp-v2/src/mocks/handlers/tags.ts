/**
 * MSW handlers for /tags endpoints
 */

import { http, HttpResponse, delay } from 'msw'
import { mockTags } from '../data/tags'

const API_BASE = '/api/v2'

export const tagsHandlers = [
  // GET /api/v2/tags - List tags
  http.get(`${API_BASE}/tags`, async () => {
    await delay(200)
    return HttpResponse.json(mockTags)
  }),
]
