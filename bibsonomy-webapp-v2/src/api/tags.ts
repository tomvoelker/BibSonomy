/**
 * Tags API endpoints
 */

import { z } from 'zod'
import { apiClient } from './client'
import { TagSchema, type Tag } from '@/types/models'

const TagListSchema = z.array(TagSchema)

export interface GetTagsParams {
  offset?: number
  limit?: number
  minFreq?: number
  maxCount?: number
}

/**
 * Fetch a list of tags with optional cloud data (frequencies).
 */
export async function getTags(params?: GetTagsParams): Promise<Tag[]> {
  const response = await apiClient.get('/tags', { params })
  return TagListSchema.parse(response.data)
}
