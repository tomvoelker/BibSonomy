package org.bibsonomy.api.service

import org.bibsonomy.api.dto.*
import org.bibsonomy.api.mapper.toDto
import org.bibsonomy.api.security.BasicAuthUtils
import org.bibsonomy.common.enums.GroupingEntity
import org.bibsonomy.common.enums.PostUpdateOperation
import org.bibsonomy.common.enums.SortOrder
import org.bibsonomy.common.enums.SortKey
import org.bibsonomy.common.SortCriteria
import org.bibsonomy.common.exceptions.AccessDeniedException
import org.bibsonomy.common.exceptions.ObjectMovedException
import org.bibsonomy.common.exceptions.ObjectNotFoundException
import org.bibsonomy.bibtex.parser.PostBibTeXParser
import org.bibsonomy.common.enums.Status
import org.bibsonomy.model.*
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
import java.util.Date

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

        return PaginatedPostList.of(
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

    /**
     * Create a new post (bookmark or publication).
     *
     * @param request The create request with resource details
     * @return The created post as a DTO
     * @throws ResponseStatusException 401 if not authenticated, 400 for validation errors
     */
    fun createPost(request: CreatePostRequest): PostDto {
        val logic = resolveLogicFromRequest()
        val user = logic.authenticatedUser
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required")

        if (user.name.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required")
        }

        // Build the post from the request
        val post = when (request) {
            is CreateBookmarkRequest -> buildBookmarkPost(request, user)
            is CreateBibTexRequest -> buildBibTexPost(request, user)
        }

        // Set visibility/groups
        applyVisibility(post, request.visibility, request.groups, user)

        try {
            val results = logic.createPosts(listOf(post))
            if (results.isNullOrEmpty()) {
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create post")
            }

            val result = results.first()
            if (result.status != Status.OK) {
                val errorMsg = result.errors?.firstOrNull()?.defaultMessage ?: "Unknown error"
                log.error("Failed to create post for user {}: {}", user.name, errorMsg)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, errorMsg)
            }

            // Fetch the created post to return full DTO
            val createdHash = result.id
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No resource hash returned")

            return getPostByHash(createdHash, user.name)
        } catch (e: ResponseStatusException) {
            throw e
        } catch (e: Exception) {
            log.error("Error creating post for user {}: {}", user.name, e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create post: ${e.message}")
        }
    }

    /**
     * Update an existing post.
     *
     * @param resourceHash The resource hash of the post to update
     * @param request The update request with fields to modify
     * @return The updated post as a DTO
     * @throws ResponseStatusException 401 if not authenticated, 403 if not owner, 404 if not found
     */
    fun updatePost(resourceHash: String, request: UpdatePostRequest): PostDto {
        val logic = resolveLogicFromRequest()
        val user = logic.authenticatedUser
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required")

        if (user.name.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required")
        }

        // Fetch existing post
        val existingPost = try {
            logic.getPostDetails(resourceHash, user.name)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found")
        } catch (e: ObjectNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found")
        } catch (e: ObjectMovedException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Post has been moved")
        }

        // Verify ownership
        if (existingPost.user?.name != user.name) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to update this post")
        }

        // Apply updates
        if (request.tags != null) {
            existingPost.tags = request.tags.map { Tag().apply { name = it } }.toMutableSet()
        }

        if (request.description != null) {
            existingPost.description = request.description
        }

        if (request.visibility != null || request.groups != null) {
            applyVisibility(
                existingPost,
                request.visibility ?: determineVisibilityFromPost(existingPost),
                request.groups,
                user
            )
        }

        // Update resource fields if provided
        val resource = existingPost.resource
        if (request.title != null) {
            when (resource) {
                is Bookmark -> resource.title = request.title
                is BibTex -> resource.title = request.title
            }
        }
        if (request.url != null && resource is Bookmark) {
            resource.url = request.url
        }

        try {
            val results = logic.updatePosts(listOf(existingPost), PostUpdateOperation.UPDATE_ALL)
            if (results.isNullOrEmpty() || results.first().status != Status.OK) {
                val errorMsg = results?.firstOrNull()?.errors?.firstOrNull()?.defaultMessage ?: "Unknown error"
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, errorMsg)
            }

            // Return updated post
            val newHash = results.first().id ?: resourceHash
            return getPostByHash(newHash, user.name)
        } catch (e: ResponseStatusException) {
            throw e
        } catch (e: Exception) {
            log.error("Error updating post {} for user {}: {}", resourceHash, user.name, e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update post: ${e.message}")
        }
    }

    /**
     * Delete a post.
     *
     * @param resourceHash The resource hash of the post to delete
     * @throws ResponseStatusException 401 if not authenticated, 403 if not owner, 404 if not found
     */
    fun deletePost(resourceHash: String) {
        val logic = resolveLogicFromRequest()
        val user = logic.authenticatedUser
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required")

        if (user.name.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required")
        }

        // Verify post exists and user owns it
        val existingPost = try {
            logic.getPostDetails(resourceHash, user.name)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found")
        } catch (e: ObjectNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found")
        } catch (e: ObjectMovedException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Post has been moved")
        }

        if (existingPost.user?.name != user.name) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete this post")
        }

        try {
            logic.deletePosts(user.name, listOf(resourceHash))
            log.info("Deleted post {} for user {}", resourceHash, user.name)
        } catch (e: AccessDeniedException) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete this post")
        } catch (e: Exception) {
            log.error("Error deleting post {} for user {}: {}", resourceHash, user.name, e.message, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete post: ${e.message}")
        }
    }

    private fun buildBookmarkPost(request: CreateBookmarkRequest, user: User): Post<Bookmark> {
        if (request.url.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "URL is required")
        }
        if (request.title.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required")
        }

        val bookmark = Bookmark().apply {
            url = request.url
            title = request.title
        }

        return Post<Bookmark>().apply {
            resource = bookmark
            this.user = user
            description = request.description
            tags = request.tags.map { Tag().apply { name = it } }.toMutableSet()
            date = Date()
        }
    }

    private fun buildBibTexPost(request: CreateBibTexRequest, user: User): Post<BibTex> {
        val bibtex = if (!request.bibtex.isNullOrBlank()) {
            // Parse raw BibTeX string using PostBibTeXParser
            try {
                val parser = PostBibTeXParser()
                val parsedPost = parser.parseBibTeXPost(request.bibtex)
                if (parsedPost?.resource == null) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to parse BibTeX string")
                }
                parsedPost.resource
            } catch (e: ResponseStatusException) {
                throw e
            } catch (e: Exception) {
                log.warn("Failed to parse BibTeX: {}", e.message)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid BibTeX format: ${e.message}")
            }
        } else {
            // Build from structured fields
            if (request.title.isNullOrBlank()) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required when not providing raw BibTeX")
            }
            BibTex().apply {
                entrytype = request.entryType ?: "misc"
                title = request.title
                year = request.year?.toString()
                journal = request.journal
                booktitle = request.booktitle
                publisher = request.publisher
                if (!request.doi.isNullOrBlank()) {
                    addMiscField("doi", request.doi)
                }
                request.authors?.let { authorNames ->
                    author = authorNames.map { name ->
                        PersonName().apply {
                            // Simple parsing: assume "First Last" or "Last, First" format
                            if (name.contains(",")) {
                                val parts = name.split(",", limit = 2)
                                lastName = parts[0].trim()
                                firstName = parts.getOrNull(1)?.trim()
                            } else {
                                val parts = name.trim().split("\\s+".toRegex())
                                if (parts.size > 1) {
                                    firstName = parts.dropLast(1).joinToString(" ")
                                    lastName = parts.last()
                                } else {
                                    lastName = name
                                }
                            }
                        }
                    }
                }
            }
        }

        return Post<BibTex>().apply {
            resource = bibtex
            this.user = user
            description = request.description
            tags = request.tags.map { Tag().apply { name = it } }.toMutableSet()
            date = Date()
        }
    }

    private fun applyVisibility(post: Post<out Resource>, visibility: Visibility, groupNames: List<String>?, user: User) {
        val groups = mutableSetOf<Group>()

        when (visibility) {
            Visibility.PUBLIC -> {
                groups.add(Group().apply {
                    groupId = 0
                    name = "public"
                })
            }
            Visibility.PRIVATE -> {
                groups.add(Group().apply {
                    groupId = 1
                    name = "private"
                })
            }
            Visibility.GROUPS -> {
                if (groupNames.isNullOrEmpty()) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Groups must be specified when visibility is 'groups'")
                }
                groupNames.forEach { groupName ->
                    groups.add(Group().apply {
                        name = groupName
                    })
                }
            }
        }

        post.groups = groups
    }

    private fun determineVisibilityFromPost(post: Post<out Resource>): Visibility {
        val groupIds = post.groups?.mapNotNull { it.groupId }?.toSet() ?: emptySet()
        return when {
            groupIds.contains(0) -> Visibility.PUBLIC
            groupIds.contains(1) && groupIds.size == 1 -> Visibility.PRIVATE
            else -> Visibility.GROUPS
        }
    }
}
