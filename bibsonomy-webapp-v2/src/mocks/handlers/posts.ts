/**
 * MSW handlers for /posts endpoints
 */

import { http, HttpResponse, delay } from 'msw'
import { getMockPosts, getMockPost } from '../data/posts'

const API_BASE = '/api/v2'

// Counter for generating unique IDs (more robust than Math.random)
let idCounter = 0
function generateId(): string {
  return `mock-${Date.now()}-${++idCounter}`
}

export const postsHandlers = [
  // GET /api/v2/posts - List posts with filtering
  http.get(`${API_BASE}/posts`, async ({ request }) => {
    await delay(300) // Simulate network latency

    const url = new URL(request.url)
    const userParam = url.searchParams.get('user')
    const tagParam = url.searchParams.get('tag')
    const resourceTypeParam = url.searchParams.get('resourceType')
    const limit = parseInt(url.searchParams.get('limit') ?? '10', 10)
    const offset = parseInt(url.searchParams.get('offset') ?? '0', 10)

    const result = getMockPosts({
      ...(userParam ? { user: userParam } : {}),
      ...(tagParam ? { tag: tagParam } : {}),
      ...(resourceTypeParam === 'publication' || resourceTypeParam === 'bookmark'
        ? { resourceType: resourceTypeParam }
        : {}),
      limit,
      offset,
    })

    return HttpResponse.json({
      posts: result.posts,
      pagination: {
        total: result.total,
        offset: result.offset,
        limit: result.limit,
      },
    })
  }),

  // GET /api/v2/posts/:id - Get single post
  http.get(`${API_BASE}/posts/:id`, async ({ params }) => {
    await delay(200)

    const id = params['id'] as string
    const post = getMockPost(id)

    if (!post) {
      return HttpResponse.json(
        {
          error: 'Not Found',
          message: `Post with id '${String(id)}' not found`,
          status: 404,
        },
        { status: 404 }
      )
    }

    return HttpResponse.json(post)
  }),

  // POST /api/v2/posts - Create new post
  http.post(`${API_BASE}/posts`, async ({ request }) => {
    await delay(400)

    const body = (await request.json()) as Record<string, unknown>

    // Validate required fields
    if (typeof body['title'] !== 'string' || !body['title'].trim()) {
      return HttpResponse.json(
        {
          error: 'Bad Request',
          message: 'Missing or invalid required field: title',
          status: 400,
        },
        { status: 400 }
      )
    }

    if (body['resourceType'] !== 'publication' && body['resourceType'] !== 'bookmark') {
      return HttpResponse.json(
        {
          error: 'Bad Request',
          message:
            'Missing or invalid required field: resourceType (must be "publication" or "bookmark")',
          status: 400,
        },
        { status: 400 }
      )
    }

    // Helper to validate bibTexData (must be object or null/undefined)
    const validateBibTexData = (data: unknown): Record<string, unknown> | null => {
      if (data === null || data === undefined) return null
      if (typeof data === 'object' && !Array.isArray(data)) {
        return data as Record<string, unknown>
      }
      return null // Invalid types are treated as null
    }

    // Simulate successful creation with validated fields
    const now = new Date().toISOString()
    const newPost = {
      id: generateId(),
      title: body['title'],
      resourceType: body['resourceType'],
      description: typeof body['description'] === 'string' ? body['description'] : undefined,
      url: typeof body['url'] === 'string' ? body['url'] : null,
      bibTexData: validateBibTexData(body['bibTexData']),
      tags: Array.isArray(body['tags']) ? body['tags'] : [],
      groups: Array.isArray(body['groups']) ? body['groups'] : [],
      user: { id: 'mock-user', name: 'Mock User' },
      createdAt: now,
      updatedAt: now,
    }

    return HttpResponse.json(newPost, { status: 201 })
  }),

  // PUT /api/v2/posts/:id - Update post
  http.put(`${API_BASE}/posts/:id`, async ({ params, request }) => {
    await delay(350)

    const id = params['id'] as string
    const post = getMockPost(id)

    if (!post) {
      return HttpResponse.json(
        {
          error: 'Not Found',
          message: `Post with id '${String(id)}' not found`,
          status: 404,
        },
        { status: 404 }
      )
    }

    const body = (await request.json()) as Record<string, unknown>

    const updatedPost = {
      ...post,
      ...body,
      id: post.id, // Keep original ID
      updatedAt: new Date().toISOString(),
    }

    return HttpResponse.json(updatedPost)
  }),

  // DELETE /api/v2/posts/:id - Delete post
  http.delete(`${API_BASE}/posts/:id`, async ({ params }) => {
    await delay(250)

    const id = params['id'] as string
    const post = getMockPost(id)

    if (!post) {
      return HttpResponse.json(
        {
          error: 'Not Found',
          message: `Post with id '${String(id)}' not found`,
          status: 404,
        },
        { status: 404 }
      )
    }

    return HttpResponse.json(
      {
        message: 'Post deleted successfully',
      },
      { status: 200 }
    )
  }),
]
