/**
 * Core domain type definitions
 * Derived from OpenAPI specification and mock data
 */

import { z } from 'zod'

// =============================================================================
// User
// =============================================================================

export const UserSchema = z.object({
  id: z.string(),
  name: z.string(),
  firstName: z.string().optional(),
  lastName: z.string().optional(),
  email: z.string().email().optional(),
})

export type User = z.infer<typeof UserSchema>

// =============================================================================
// Group
// =============================================================================

export const GroupSchema = z.object({
  id: z.string(),
  name: z.string(),
})

export type Group = z.infer<typeof GroupSchema>

// =============================================================================
// Tag
// =============================================================================

export const TagSchema = z.object({
  name: z.string(),
  globalCount: z.number().optional(),
})

export type Tag = z.infer<typeof TagSchema>

// =============================================================================
// BibTeX Data
// =============================================================================

export const BibTexDataSchema = z.object({
  entryType: z.string(), // article, book, inproceedings, etc.
  bibTexKey: z.string(),
  author: z.string().optional(),
  title: z.string().optional(),
  journal: z.string().optional(),
  booktitle: z.string().optional(),
  year: z.string().optional(),
  volume: z.string().optional(),
  number: z.string().optional(),
  pages: z.string().optional(),
  publisher: z.string().optional(),
  isbn: z.string().optional(),
  abstract: z.string().optional(),
  // Additional fields can be added as needed
})

export type BibTexData = z.infer<typeof BibTexDataSchema>

// =============================================================================
// Post (Publication or Bookmark)
// =============================================================================

export const PostSchema = z.object({
  id: z.string(),
  resourceType: z.enum(['publication', 'bookmark']),
  title: z.string(),
  description: z.string().optional(),
  url: z.string().url().nullable(),
  bibTexData: BibTexDataSchema.nullable(),
  user: UserSchema,
  groups: z.array(GroupSchema),
  tags: z.array(TagSchema),
  createdAt: z.string().datetime(), // ISO 8601 datetime string
  updatedAt: z.string().datetime(),
})

export type Post = z.infer<typeof PostSchema>

// =============================================================================
// Post List Response (with pagination)
// =============================================================================

export const PaginationSchema = z.object({
  total: z.number(),
  offset: z.number(),
  limit: z.number(),
})

export type Pagination = z.infer<typeof PaginationSchema>

export const PostListResponseSchema = z.object({
  posts: z.array(PostSchema),
  pagination: PaginationSchema,
})

export type PostListResponse = z.infer<typeof PostListResponseSchema>

// =============================================================================
// Query Parameters
// =============================================================================

export interface GetPostsParams {
  user?: string
  tag?: string
  resourceType?: 'publication' | 'bookmark'
  limit?: number
  offset?: number
  search?: string
  sortBy?: 'date' | 'title' | 'relevance'
  sortOrder?: 'asc' | 'desc'
}

// =============================================================================
// Helper type guards
// =============================================================================

export function isPublication(post: Post): post is Post & { resourceType: 'publication' } {
  return post.resourceType === 'publication'
}

export function isBookmark(post: Post): post is Post & { resourceType: 'bookmark' } {
  return post.resourceType === 'bookmark'
}

// =============================================================================
// Display helpers
// =============================================================================

/**
 * Format authors for display (e.g., "Smith, John and Doe, Jane" -> "Smith & Doe")
 */
export function formatAuthors(authorString: string | undefined): string {
  if (!authorString) return 'Unknown'

  const authors = authorString
    .split(' and ')
    .map((author) => {
      // Handle "Last, First" format
      const parts = author.trim().split(',')
      return parts[0]?.trim() ?? author.trim()
    })
    .filter(Boolean)

  if (authors.length === 0) return 'Unknown'
  if (authors.length === 1) return authors[0] ?? 'Unknown'
  if (authors.length === 2) return authors.join(' & ')

  // For 3+ authors, show "First et al."
  return `${authors[0]} et al.`
}

/**
 * Get display title for a post (handles missing titles)
 */
export function getPostTitle(post: Post): string {
  if (post.title) return post.title
  if (post.bibTexData?.title) return post.bibTexData.title
  if (post.url) return post.url
  return 'Untitled'
}

/**
 * Get publication year from various sources
 */
export function getPublicationYear(post: Post): string | null {
  if (post.bibTexData?.year) return post.bibTexData.year

  // Fallback to createdAt year
  const createdYear = new Date(post.createdAt).getFullYear()
  return createdYear.toString()
}
