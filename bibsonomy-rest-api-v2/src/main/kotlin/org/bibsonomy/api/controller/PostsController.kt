package org.bibsonomy.api.controller

import org.bibsonomy.api.dto.PaginatedPostList
import org.bibsonomy.api.service.PostService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for posts endpoints.
 *
 * Implements the /api/v2/posts endpoints as defined in the OpenAPI specification.
 */
@RestController
@RequestMapping("/api/v2/posts")
class PostsController(
    private val postService: PostService
) {

    /**
     * GET /api/v2/posts - List posts
     *
     * Returns a paginated list of posts (bookmarks and/or publications).
     * Supports filtering by resource type, tags, user, group, and search query.
     *
     * @param offset Number of items to skip (default: 0)
     * @param limit Number of items to return (default: 20, max: 100)
     * @param resourceType Filter by resource type: "bookmark", "bibtex", or "all" (default: "all")
     * @param tags Filter by tags (comma-separated, all tags must match)
     * @param user Filter by username
     * @param group Filter by group name
     * @param search Full-text search query
     * @return Paginated list of posts
     */
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun listPosts(
        @RequestParam(defaultValue = "0") offset: Int,
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(defaultValue = "all") resourceType: String,
        @RequestParam(value = "resourcetype", required = false) resourceTypeAlias: String?,
        @RequestParam(required = false) tags: List<String>?,
        @RequestParam(required = false) user: String?,
        @RequestParam(required = false) group: String?,
        @RequestParam(required = false) search: String?,
        @RequestParam(name = "sortBy", defaultValue = "date") sortBy: String,
        @RequestParam(name = "order", defaultValue = "desc") order: String,
        @RequestParam(name = "format", defaultValue = "json") format: String,
        @RequestParam(name = "includeTotal", defaultValue = "false") includeTotal: Boolean
    ): PaginatedPostList {
        // Validate and clamp limit to max 100
        val clampedLimit = limit.coerceIn(1, 100)
        val effectiveResourceType = resourceTypeAlias ?: resourceType

        return postService.getPosts(
            offset = offset,
            limit = clampedLimit,
            resourceType = effectiveResourceType,
            tags = tags,
            user = user,
            group = group,
            search = search,
            sortBy = sortBy,
            order = order,
            format = format,
            includeTotal = includeTotal
        )
    }

    /**
     * GET /api/v2/posts/{postId} - Get post details by resource hash
     *
     * @param postId Resource hash (intraHash/interHash) of the post
     * @param user Optional post owner (legacy API requires owner + hash to disambiguate)
     */
    @GetMapping("/{postId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPost(
        @PathVariable("postId") postId: String,
        @RequestParam(required = false) user: String?
    ): ResponseEntity<org.bibsonomy.api.dto.PostDto> {
        val dto = postService.getPostByHash(postId, user)
        return ResponseEntity.ok(dto)
    }
}
