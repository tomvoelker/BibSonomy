package org.bibsonomy.api.service

import org.bibsonomy.api.dto.PaginatedPostList
import org.bibsonomy.api.dto.PostDto
import org.bibsonomy.api.mapper.toDto
import org.bibsonomy.api.security.BasicAuthUtils
import org.bibsonomy.common.enums.GroupingEntity
import org.bibsonomy.common.enums.SortOrder
import org.bibsonomy.common.enums.SortKey
import org.bibsonomy.common.SortCriteria
import org.bibsonomy.common.exceptions.ObjectMovedException
import org.bibsonomy.common.exceptions.ObjectNotFoundException
import org.bibsonomy.model.Resource
import org.bibsonomy.model.logic.LogicInterface
import org.bibsonomy.model.logic.LogicInterfaceFactory
import org.bibsonomy.model.logic.query.PostQuery
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.http.HttpHeaders
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * Service layer for posts API.
 *
 * Wraps the legacy LogicInterface and converts domain models to DTOs.
 * Handles nullability and provides a clean API for the controller layer.
 */
@Service
class PostService(
    private val logic: LogicInterface,
    private val logicFactory: LogicInterfaceFactory
) {
    private val log = LoggerFactory.getLogger(PostService::class.java)

    companion object {
        /**
         * Maximum allowed offset for resourceType="all" merged pagination.
         *
         * For merged pagination, we must fetch [0, offset+limit) from BOTH resource types,
         * merge/sort them, then slice. At offset=500, limit=20, this fetches 2×520 = 1040 items.
         * Beyond this threshold, clients should use a specific resourceType or cursor pagination.
         */
        const val MAX_OFFSET_FOR_MERGED_PAGINATION = 500

        /**
         * Offset threshold above which a warning header is recommended.
         * Clients should consider switching to specific resourceType or cursor pagination.
         */
        const val MERGED_PAGINATION_WARNING_THRESHOLD = 200
    }

    fun getPostByHash(resourceHash: String, user: String?): PostDto {
        val logic = resolveLogicFromRequest()
        return try {
            val post = logic.getPostDetails(resourceHash, user ?: "")
            post?.toDto() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        } catch (_: ObjectNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        } catch (_: ObjectMovedException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Get a paginated list of posts.
     *
     * @param offset starting index (default: 0)
     * @param limit number of items to return (default: 20)
     * @param resourceType filter by resource type: "bookmark", "bibtex", or "all"
     * @param tags filter by tags (all must match)
     * @param user filter by username
     * @param group filter by group name
     * @param search full-text search query
     * @return paginated list of posts
     */
    fun getPosts(
        offset: Int = 0,
        limit: Int = 20,
        resourceType: String = "all",
        tags: List<String>? = null,
        user: String? = null,
        group: String? = null,
        search: String? = null,
        sortBy: String = "date",
        order: String = "desc",
        format: String = "json", // currently ignored; JSON only
        includeTotal: Boolean = false
    ): PaginatedPostList {
        val logic = resolveLogicFromRequest()
        val sortKey = sortKeyFromParam(sortBy)
        val sortOrder = if (order.equals("asc", ignoreCase = true)) SortOrder.ASC else SortOrder.DESC
        val normalizedTags = tags
            ?.flatMap { it.split(',') }
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.takeIf { it.isNotEmpty() }

        val baseQuery = { resourceClass: Class<out Resource>, start: Int, end: Int ->
            @Suppress("UNCHECKED_CAST")
            PostQuery(resourceClass as Class<Resource>).apply {
                grouping = when {
                    user != null -> GroupingEntity.USER
                    group != null -> GroupingEntity.GROUP
                    else -> GroupingEntity.ALL
                }
                groupingName = user ?: group ?: ""
                if (normalizedTags != null) this.tags = normalizedTags
                if (!search.isNullOrBlank()) this.search = search
                this.start = start
                this.end = end
                sortCriteria = listOf(SortCriteria(sortKey, sortOrder))
            }
        }

        val posts: List<org.bibsonomy.model.Post<Resource>> = when (resourceType.lowercase()) {
            "bookmark" -> logic.getPosts(baseQuery(org.bibsonomy.model.Bookmark::class.java, offset, offset + limit))
            "bibtex" -> logic.getPosts(baseQuery(org.bibsonomy.model.BibTex::class.java, offset, offset + limit))
            "all" -> {
                // Validate offset for merged pagination to prevent excessive data fetching.
                // For offset=N, we fetch 2×(N+limit) items before slicing.
                if (offset > MAX_OFFSET_FOR_MERGED_PAGINATION) {
                    throw ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Offset $offset exceeds maximum ($MAX_OFFSET_FOR_MERGED_PAGINATION) for resourceType='all'. " +
                            "Use resourceType='bookmark' or 'bibtex' for deep pagination, or use cursor-based pagination."
                    )
                }
                val fetchEnd = offset + limit
                val bookmarks = logic.getPosts(baseQuery(org.bibsonomy.model.Bookmark::class.java, 0, fetchEnd))
                val publications = logic.getPosts(baseQuery(org.bibsonomy.model.BibTex::class.java, 0, fetchEnd))
                val merged = (bookmarks + publications).sortedWith(buildComparator(sortKey, sortOrder))
                merged.drop(offset).take(limit)
            }
            else -> logic.getPosts(baseQuery(org.bibsonomy.model.BibTex::class.java, offset, offset + limit))
        }

        // Convert domain models to DTOs
        val postDtos = posts.mapNotNull { post ->
            try {
                post.toDto()
            } catch (e: IllegalStateException) {
                // Skip posts with invalid data (e.g., null contentId, date, user, or resource)
                val postId = post.contentId ?: "unknown"
                val userName = post.user?.name ?: "unknown"
                val resourceTitle = post.resource?.let {
                    when (it) {
                        is org.bibsonomy.model.Bookmark -> it.title
                        is org.bibsonomy.model.BibTex -> it.title
                        else -> null
                    }
                } ?: "unknown"

                log.warn(
                    "Skipping post with invalid data - postId: {}, user: {}, title: {}, error: {}",
                    postId,
                    userName,
                    resourceTitle,
                    e.message,
                    e
                )
                null
            }
        }

        val totalCount = if (includeTotal) {
            when (resourceType.lowercase()) {
                "bookmark" -> getCount(logic, org.bibsonomy.model.Bookmark::class.java, normalizedTags, user, group, search, sortKey)
                "bibtex" -> getCount(logic, org.bibsonomy.model.BibTex::class.java, normalizedTags, user, group, search, sortKey)
                "all" -> getCount(logic, org.bibsonomy.model.Bookmark::class.java, normalizedTags, user, group, search, sortKey) +
                    getCount(logic, org.bibsonomy.model.BibTex::class.java, normalizedTags, user, group, search, sortKey)
                // Unknown types fall back to bibtex query (see posts fetch above), so count should match
                else -> getCount(logic, org.bibsonomy.model.BibTex::class.java, normalizedTags, user, group, search, sortKey)
            }
        } else {
            // Per OpenAPI spec: when includeTotal=false, return -1 to indicate not computed
            -1
        }

        return PaginatedPostList(
            items = postDtos,
            totalCount = totalCount,
            offset = offset,
            limit = limit
        )
    }

    private fun sortKeyFromParam(sortBy: String): SortKey {
        return when (sortBy.lowercase()) {
            "date" -> SortKey.DATE
            "title" -> SortKey.TITLE
            "author" -> SortKey.AUTHOR
            "relevance" -> SortKey.RANK
            else -> SortKey.NONE
        }
    }

    /**
     * Resolve the LogicInterface for the current request, supporting optional auth.
     *
     * The injected `logic` bean is request-scoped and already authenticated for protected
     * endpoints. However, public endpoints (GET /posts) allow optional authentication where
     * the SecurityContext may be unauthenticated/guest even when Basic Auth header is present.
     * This method re-parses the header only when the current logic is guest but credentials
     * are available, enabling authenticated users to see their private posts on public endpoints.
     */
    private fun resolveLogicFromRequest(): LogicInterface {
        val current = logic
        val user = current.authenticatedUser
        val request = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
        val header = request?.getHeader(HttpHeaders.AUTHORIZATION)
        if (header != null && header.startsWith(BasicAuthUtils.BASIC_PREFIX)) {
            val (username, apiKey) = BasicAuthUtils.decode(header)
            // Only rebuild logic if the current proxy is unauthenticated/guest or mismatched.
            if (user?.name.isNullOrBlank() || user?.name != username) {
                return logicFactory.getLogicAccess(username, apiKey)
            }
        }
        return current
    }

    private fun buildComparator(sortKey: SortKey, sortOrder: SortOrder): Comparator<org.bibsonomy.model.Post<Resource>> {
        val base = when (sortKey) {
            SortKey.TITLE -> compareBy<org.bibsonomy.model.Post<Resource>> { post ->
                when (val res = post.resource) {
                    is org.bibsonomy.model.BibTex -> res.title ?: ""
                    is org.bibsonomy.model.Bookmark -> res.title ?: ""
                    else -> ""
                }.lowercase()
            }
            SortKey.AUTHOR -> compareBy { post ->
                (post.resource as? org.bibsonomy.model.BibTex)?.author?.firstOrNull()?.lastName ?: ""
            }
            else -> compareBy { it.changeDate ?: it.date }
        }
        return if (sortOrder == SortOrder.ASC) base else base.reversed()
    }

    private fun getCount(
        logic: LogicInterface,
        resourceClass: Class<out Resource>,
        tags: List<String>?,
        user: String?,
        group: String?,
        search: String?,
        sortKey: SortKey
    ): Int {
        val grouping = when {
            user != null -> GroupingEntity.USER
            group != null -> GroupingEntity.GROUP
            else -> GroupingEntity.ALL
        }
        val groupingName = user ?: group ?: ""
        val stats = logic.getPostStatistics(
            resourceClass,
            grouping,
            groupingName,
            tags,
            null,
            search,
            null,
            sortKey,
            null,
            null,
            0,
            0
        )
        return stats?.count ?: 0
    }
}
