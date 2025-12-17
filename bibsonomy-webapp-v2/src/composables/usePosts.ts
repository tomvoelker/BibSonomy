/**
 * Composables for fetching and managing posts
 * Uses TanStack Query for server state management
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import { getPosts, getPost, createPost, updatePost, deletePost } from '@/api/posts'
import type { GetPostsParams, Post } from '@/types/models'
import type { Ref } from 'vue'

/**
 * Fetch a list of posts with optional filters
 */
export function usePosts(params?: Ref<GetPostsParams> | GetPostsParams) {
  return useQuery({
    queryKey: ['posts', params],
    queryFn: () => {
      const queryParams = 'value' in params ? params.value : params
      return getPosts(queryParams)
    },
  })
}

/**
 * Fetch a single post by ID
 */
export function usePost(id: Ref<string> | string) {
  return useQuery({
    queryKey: ['posts', id],
    queryFn: () => {
      const postId = typeof id === 'string' ? id : id.value
      return getPost(postId)
    },
    enabled: typeof id === 'string' ? !!id : () => !!id.value,
  })
}

/**
 * Create a new post
 */
export function useCreatePost() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (post: Partial<Post>) => createPost(post),
    onSuccess: () => {
      // Invalidate posts list to refetch
      void queryClient.invalidateQueries({ queryKey: ['posts'] })
    },
  })
}

/**
 * Update an existing post
 */
export function useUpdatePost() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, post }: { id: string; post: Partial<Post> }) => updatePost(id, post),
    onSuccess: (_data, variables) => {
      // Invalidate both the list and the specific post
      void queryClient.invalidateQueries({ queryKey: ['posts'] })
      void queryClient.invalidateQueries({ queryKey: ['posts', variables.id] })
    },
  })
}

/**
 * Delete a post
 */
export function useDeletePost() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: string) => deletePost(id),
    onSuccess: () => {
      // Invalidate posts list
      void queryClient.invalidateQueries({ queryKey: ['posts'] })
    },
  })
}
