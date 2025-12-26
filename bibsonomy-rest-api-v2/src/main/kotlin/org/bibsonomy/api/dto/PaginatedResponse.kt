package org.bibsonomy.api.dto

/**
 * Generic paginated response wrapper.
 */
data class PaginatedPostList(
    val items: List<PostDto>,
    val totalCount: Int,
    val offset: Int,
    val limit: Int
)
