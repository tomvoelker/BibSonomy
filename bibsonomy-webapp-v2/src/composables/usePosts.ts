/**
 * Composables for fetching and managing posts
 * Uses TanStack Query for server state management
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import { isRef } from 'vue'
import { getPosts, getPost, createPost, updatePost, deletePost } from '@/api/posts'
import type { GetPostsParams, CreatePostRequest, UpdatePostRequest } from '@/types/models'
import type { Ref } from 'vue'

/**
 * Fetch a list of posts with optional filters
 */
export function usePosts(params?: Ref<GetPostsParams> | GetPostsParams) {
  return useQuery({
    queryKey: ['posts', params],
    queryFn: () => {
      const queryParams = isRef(params) ? params.value : params
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
      const postId = isRef(id) ? id.value : id
      return getPost(postId)
    },
    enabled: isRef(id) ? () => !!id.value : !!id,
  })
}

/**
 * Create a new post
 */
export function useCreatePost() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (post: CreatePostRequest) => createPost(post),
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
    mutationFn: ({ id, post }: { id: string; post: UpdatePostRequest }) => updatePost(id, post),
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
