package org.bibsonomy.api.service

import org.bibsonomy.api.dto.*
import org.bibsonomy.api.mapper.toDto
import org.bibsonomy.api.mapper.toRefDto
import org.bibsonomy.api.security.BasicAuthUtils
import org.bibsonomy.common.enums.GroupingEntity
import org.bibsonomy.common.enums.QueryScope
import org.bibsonomy.common.enums.SortKey
import org.bibsonomy.model.Resource
import org.bibsonomy.model.logic.LogicInterface
import org.bibsonomy.model.logic.LogicInterfaceFactory
import org.bibsonomy.model.logic.query.PostQuery
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.server.ResponseStatusException

/**
 * Service for search operations.
 *
 * Provides full-text search across posts, users, and tags.
 */
@Service
class SearchService(
    private val logic: LogicInterface,
    private val logicFactory: LogicInterfaceFactory
) {
    private val log = LoggerFactory.getLogger(SearchService::class.java)

    /**
     * Perform a full-text search across posts, users, and tags.
     *
     * @param query Search query string
     * @param type Type of search: "posts", "users", "tags", or "all"
     * @param limit Maximum results per type (default: 20)
     * @param offset Starting offset for pagination (default: 0)
     * @return SearchResultDto with matching items and totals
     */
    fun search(
        query: String,
        type: String = "all",
        limit: Int = 20,
        offset: Int = 0
    ): SearchResultDto {
        if (query.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Search query is required")
        }

        val logic = resolveLogicFromRequest()
        val effectiveLimit = limit.coerceIn(1, 100)
        val effectiveOffset = offset.coerceAtLeast(0)

        val searchType = type.lowercase()

        // Search posts
        val (posts, totalPosts) = if (searchType == "all" || searchType == "posts") {
            searchPosts(logic, query, effectiveLimit, effectiveOffset)
        } else {
            emptyList<PostDto>() to 0
        }

        // Search users
        val (users, totalUsers) = if (searchType == "all" || searchType == "users") {
            searchUsers(logic, query, effectiveLimit, effectiveOffset)
        } else {
            emptyList<UserRefDto>() to 0
        }

        // Search tags
        val (tags, totalTags) = if (searchType == "all" || searchType == "tags") {
            searchTags(logic, query, effectiveLimit, effectiveOffset)
        } else {
            emptyList<TagDto>() to 0
        }

        return SearchResultDto(
            posts = posts,
            users = users,
            tags = tags,
            totalPosts = totalPosts,
            totalUsers = totalUsers,
            totalTags = totalTags
        )
    }

    private fun searchPosts(
        logic: LogicInterface,
        query: String,
        limit: Int,
        offset: Int
    ): Pair<List<PostDto>, Int> {
        // Search bookmarks
        val bookmarkQuery = PostQuery(org.bibsonomy.model.Bookmark::class.java).apply {
            grouping = GroupingEntity.ALL
            groupingName = ""
            search = query
            start = offset
            end = offset + limit
        }

        // Search publications
        val bibtexQuery = PostQuery(org.bibsonomy.model.BibTex::class.java).apply {
            grouping = GroupingEntity.ALL
            groupingName = ""
            search = query
            start = offset
            end = offset + limit
        }

        val bookmarks = try {
            logic.getPosts(bookmarkQuery) ?: emptyList()
        } catch (e: Exception) {
            log.warn("Error searching bookmarks: {}", e.message)
            emptyList()
        }

        val publications = try {
            logic.getPosts(bibtexQuery) ?: emptyList()
        } catch (e: Exception) {
            log.warn("Error searching publications: {}", e.message)
            emptyList()
        }

        val allPosts = (bookmarks + publications)
            .sortedByDescending { it.date }
            .take(limit)
            .mapNotNull { post ->
                try {
                    post.toDto()
                } catch (e: Exception) {
                    log.warn("Error mapping post: {}", e.message)
                    null
                }
            }

        // Get total count
        val bookmarkStats = try {
            logic.getPostStatistics(
                org.bibsonomy.model.Bookmark::class.java,
                GroupingEntity.ALL,
                "",
                null,
                null,
                query,
                null,
                SortKey.DATE,
                null,
                null,
                0,
                0
            )
        } catch (e: Exception) {
            null
        }

        val bibtexStats = try {
            logic.getPostStatistics(
                org.bibsonomy.model.BibTex::class.java,
                GroupingEntity.ALL,
                "",
                null,
                null,
                query,
                null,
                SortKey.DATE,
                null,
                null,
                0,
                0
            )
        } catch (e: Exception) {
            null
        }

        val totalPosts = (bookmarkStats?.count ?: 0) + (bibtexStats?.count ?: 0)

        return allPosts to totalPosts
    }

    private fun searchUsers(
        logic: LogicInterface,
        query: String,
        limit: Int,
        offset: Int
    ): Pair<List<UserRefDto>, Int> {
        try {
            val users = logic.getUsers(
                Resource::class.java,
                GroupingEntity.ALL,
                null,
                null,
                null,
                SortKey.NONE,
                null,
                query,
                offset,
                offset + limit
            )

            val userDtos = users?.mapNotNull { user ->
                try {
                    user.toRefDto()
                } catch (e: Exception) {
                    log.warn("Error mapping user: {}", e.message)
                    null
                }
            } ?: emptyList()

            // We don't have a separate count method for users, estimate from result
            val total = if (users != null && users.size == limit) {
                // If we got exactly limit results, there might be more
                limit + offset + 1
            } else {
                (users?.size ?: 0) + offset
            }

            return userDtos to total
        } catch (e: Exception) {
            log.warn("Error searching users: {}", e.message)
            return emptyList<UserRefDto>() to 0
        }
    }

    private fun searchTags(
        logic: LogicInterface,
        query: String,
        limit: Int,
        offset: Int
    ): Pair<List<TagDto>, Int> {
        try {
            val tags = logic.getTags(
                Resource::class.java,
                GroupingEntity.ALL,
                null,
                null,
                null,
                query,
                QueryScope.LOCAL,
                null,
                null,
                SortKey.FREQUENCY,
                null,
                null,
                offset,
                offset + limit
            )

            val tagDtos = tags?.map { it.toDto() } ?: emptyList()

            // Estimate total
            val total = if (tags != null && tags.size == limit) {
                limit + offset + 1
            } else {
                (tags?.size ?: 0) + offset
            }

            return tagDtos to total
        } catch (e: Exception) {
            log.warn("Error searching tags: {}", e.message)
            return emptyList<TagDto>() to 0
        }
    }

    /**
     * Resolve the LogicInterface for the current request, supporting optional auth.
     */
    private fun resolveLogicFromRequest(): LogicInterface {
        val current = logic
        val user = current.authenticatedUser
        val request = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
        val header = request?.getHeader(HttpHeaders.AUTHORIZATION)
        if (header != null && header.startsWith(BasicAuthUtils.BASIC_PREFIX)) {
            try {
                val (username, apiKey) = BasicAuthUtils.decode(header)
                if (user?.name.isNullOrBlank() || user?.name != username) {
                    return logicFactory.getLogicAccess(username, apiKey)
                }
            } catch (e: BadCredentialsException) {
                log.debug("Invalid Authorization header format, ignoring: {}", e.message)
            }
        }
        return current
    }
}
