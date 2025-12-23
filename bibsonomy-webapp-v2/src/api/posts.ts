/**
 * Posts API endpoints
 */

import { apiClient } from './client'
import {
  PostSchema,
  PostListResponseSchema,
  type Post,
  type PostListResponse,
  type GetPostsParams,
} from '@/types/models'

/**
 * Fetch a list of posts with optional filtering and pagination
 */
export async function getPosts(params?: GetPostsParams): Promise<PostListResponse> {
  const queryParams = params
    ? {
        ...params,
        tags: params.tags?.join(','),
      }
    : undefined

  const response = await apiClient.get('/posts', { params: queryParams })

  // Validate response with Zod schema
  const validated = PostListResponseSchema.parse(response.data)

  return validated
}

/**
 * Fetch a single post by ID
 */
export async function getPost(id: string): Promise<Post> {
  const response = await apiClient.get(`/posts/${id}`)

  // Validate response with Zod schema
  const validated = PostSchema.parse(response.data)

  return validated
}

/**
 * Create a new post
 */
export async function createPost(post: Partial<Post>): Promise<Post> {
  const response = await apiClient.post('/posts', post)

  const validated = PostSchema.parse(response.data)

  return validated
}

/**
 * Update an existing post
 */
export async function updatePost(id: string, post: Partial<Post>): Promise<Post> {
  const response = await apiClient.put(`/posts/${id}`, post)

  const validated = PostSchema.parse(response.data)

  return validated
}

/**
 * Delete a post
 */
export async function deletePost(id: string): Promise<void> {
  await apiClient.delete(`/posts/${id}`)
}
