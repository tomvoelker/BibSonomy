/**
 * Core domain type definitions
 * Derived from OpenAPI specification and mock data
 */

import { z } from 'zod'

// =============================================================================
// User (ref)
// =============================================================================

export const UserSchema = z.object({
  username: z.string(),
  realName: z.string().optional(),
})

export type User = z.infer<typeof UserSchema>

// =============================================================================
// Group
// =============================================================================

export const GroupSchema = z.object({
  name: z.string(),
  displayName: z.string().optional(),
})

export type Group = z.infer<typeof GroupSchema>

// =============================================================================
// Tag
// =============================================================================

export const TagSchema = z.object({
  name: z.string(),
  count: z.number().optional(),
  countPublic: z.number().optional(),
})

export type Tag = z.infer<typeof TagSchema>

// =============================================================================
// Resource
// =============================================================================

export const PersonNameSchema = z.object({
  name: z.string(),
  firstName: z.string().optional(),
  lastName: z.string().optional(),
})

export type PersonName = z.infer<typeof PersonNameSchema>

export const BookmarkResourceSchema = z.object({
  resourceType: z.literal('bookmark'),
  url: z.string(),
  title: z.string(),
  urlHash: z.string().nullable().optional(),
})

export type BookmarkResource = z.infer<typeof BookmarkResourceSchema>

export const BibTexResourceSchema = z.object({
  resourceType: z.literal('bibtex'),
  bibtexKey: z.string().nullable().optional(),
  entryType: z.string(),
  title: z.string(),
  authors: z.array(PersonNameSchema).optional().nullable(),
  editors: z.array(PersonNameSchema).optional().nullable(),
  year: z.number().optional().nullable(),
  month: z.string().optional().nullable(),
  journal: z.string().optional().nullable(),
  booktitle: z.string().optional().nullable(),
  publisher: z.string().optional().nullable(),
  volume: z.string().optional().nullable(),
  number: z.string().optional().nullable(),
  pages: z.string().optional().nullable(),
  doi: z.string().optional().nullable(),
  url: z.string().optional().nullable(),
  abstract: z.string().optional().nullable(),
})

export type BibTexResource = z.infer<typeof BibTexResourceSchema>

export const ResourceSchema = z.discriminatedUnion('resourceType', [
  BookmarkResourceSchema,
  BibTexResourceSchema,
])

export type Resource = z.infer<typeof ResourceSchema>

// =============================================================================
// Post (Publication or Bookmark)
// =============================================================================

export const PostSchema = z.object({
  id: z.number(),
  resource: ResourceSchema,
  description: z.string().optional(),
  user: UserSchema,
  groups: z.array(GroupSchema),
  tags: z.array(TagSchema),
  createdAt: z.string().datetime(), // ISO 8601 datetime string
  updatedAt: z.string().datetime().optional().nullable(),
  visibility: z.enum(['public', 'private', 'groups']),
})

export type Post = z.infer<typeof PostSchema>

// =============================================================================
// Post List Response (with pagination)
// =============================================================================

export const PaginationSchema = z.object({
  totalCount: z.number(),
  offset: z.number(),
  limit: z.number(),
})

export type Pagination = z.infer<typeof PaginationSchema>

export const PostListResponseSchema = z.object({
  items: z.array(PostSchema),
  totalCount: PaginationSchema.shape.totalCount,
  offset: PaginationSchema.shape.offset,
  limit: PaginationSchema.shape.limit,
})

export type PostListResponse = z.infer<typeof PostListResponseSchema>

// =============================================================================
// Query Parameters
// =============================================================================

export interface GetPostsParams {
  user?: string
  tags?: string[]
  resourceType?: 'bibtex' | 'bookmark' | 'all'
  limit?: number
  offset?: number
  search?: string
  sortBy?: 'date' | 'title' | 'author' | 'relevance'
  sortOrder?: 'asc' | 'desc'
}

// =============================================================================
// Helper type guards
// =============================================================================

export function isPublication(post: Post): post is Post & { resource: BibTexResource } {
  return post.resource.resourceType === 'bibtex'
}

export function isBookmark(post: Post): post is Post & { resource: BookmarkResource } {
  return post.resource.resourceType === 'bookmark'
}

// =============================================================================
// Display helpers
// =============================================================================

/**
 * Format authors for display (e.g., "Smith, John and Doe, Jane" -> "Smith & Doe")
 */
export function formatAuthors(authors: PersonName[] | null | undefined): string {
  if (!authors || authors.length === 0) return 'Unknown'

  const displayNames = authors
    .map((author) => {
      if (author.lastName) return author.lastName
      const parts = author.name.trim().split(',')
      return parts[0]?.trim() ?? author.name.trim()
    })
    .filter(Boolean)

  if (displayNames.length === 0) return 'Unknown'
  if (displayNames.length === 1) return displayNames[0] ?? 'Unknown'
  if (displayNames.length === 2) return displayNames.join(' & ')

  // For 3+ authors, show "First et al."
  return `${displayNames[0]} et al.`
}

/**
 * Get display title for a post (handles missing titles)
 */
export function getPostTitle(post: Post): string {
  if (isBookmark(post)) {
    if (post.resource.title) return post.resource.title
    return post.resource.url
  }

  if (post.resource.title) return post.resource.title
  if (post.resource.url) return post.resource.url
  return 'Untitled'
}

/**
 * Get publication year from various sources
 */
export function getPublicationYear(post: Post): string | null {
  if (isPublication(post) && post.resource.year) return post.resource.year.toString()

  // Fallback to createdAt year
  const createdYear = new Date(post.createdAt).getFullYear()
  return createdYear.toString()
}
