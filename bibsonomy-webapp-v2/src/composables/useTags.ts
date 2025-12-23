/**
 * Composable for fetching tags.
 */

import { useQuery } from '@tanstack/vue-query'
import { getTags, type GetTagsParams } from '@/api/tags'

export function useTags(params?: GetTagsParams) {
  return useQuery({
    queryKey: ['tags', params],
    queryFn: () => getTags(params),
  })
}
