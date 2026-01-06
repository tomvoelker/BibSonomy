package org.bibsonomy.api.dto

/**
 * Pagination metadata.
 */
data class Pagination(
    val total: Int,
    val offset: Int,
    val limit: Int
)

/**
 * Paginated response wrapper for posts.
 * Uses 'posts' field name to match frontend expectations.
 */
data class PaginatedPostList(
    val posts: List<PostDto>,
    val pagination: Pagination
) {
    companion object {
        fun of(items: List<PostDto>, totalCount: Int, offset: Int, limit: Int): PaginatedPostList {
            return PaginatedPostList(
                posts = items,
                pagination = Pagination(total = totalCount, offset = offset, limit = limit)
            )
        }
    }
}
