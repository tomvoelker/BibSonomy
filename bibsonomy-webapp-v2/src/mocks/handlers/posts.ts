/**
 * MSW handlers for /posts endpoints
 */

import { http, HttpResponse, delay } from 'msw'
import { getMockPosts, getMockPost } from '../data/posts'

const API_BASE = '/api/v2'

export const postsHandlers = [
  // GET /api/v2/posts - List posts with filtering
  http.get(`${API_BASE}/posts`, async ({ request }) => {
    await delay(300) // Simulate network latency

    const url = new URL(request.url)
    const user = url.searchParams.get('user') ?? undefined
    const tag = url.searchParams.get('tag') ?? undefined
    const resourceType = url.searchParams.get('resourceType') as
      | 'publication'
      | 'bookmark'
      | undefined
    const limit = parseInt(url.searchParams.get('limit') ?? '10', 10)
    const offset = parseInt(url.searchParams.get('offset') ?? '0', 10)

    const result = getMockPosts({
      user,
      tag,
      resourceType,
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

    const id = params.id as string
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

    // Simulate validation
    if (!body.title || !body.resourceType) {
      return HttpResponse.json(
        {
          error: 'Bad Request',
          message: 'Missing required fields: title, resourceType',
          status: 400,
        },
        { status: 400 }
      )
    }

    // Simulate successful creation
    const newPost = {
      id: Math.random().toString(36).substring(7),
      ...body,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    }

    return HttpResponse.json(newPost, { status: 201 })
  }),

  // PUT /api/v2/posts/:id - Update post
  http.put(`${API_BASE}/posts/:id`, async ({ params, request }) => {
    await delay(350)

    const id = params.id as string
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

    const id = params.id as string
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
