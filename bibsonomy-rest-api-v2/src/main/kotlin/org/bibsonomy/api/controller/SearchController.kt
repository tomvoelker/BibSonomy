package org.bibsonomy.api.controller

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.bibsonomy.api.dto.SearchResultDto
import org.bibsonomy.api.service.SearchService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for search endpoints.
 *
 * Provides full-text search across posts, users, and tags.
 */
@RestController
@RequestMapping("/api/v2/search")
@Validated
class SearchController(
    private val searchService: SearchService
) {

    /**
     * GET /api/v2/search - Full-text search
     *
     * Searches across posts, users, and tags based on the query.
     *
     * @param q Search query (required)
     * @param type Type of search: "posts", "users", "tags", or "all" (default: "all")
     * @param limit Maximum results per type (default: 20, max: 100)
     * @param offset Starting offset for pagination (default: 0)
     * @return SearchResultDto with matching items
     */
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun search(
        @RequestParam q: String,
        @RequestParam(defaultValue = "all") type: String,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) limit: Int,
        @RequestParam(defaultValue = "0") @Min(0) offset: Int
    ): ResponseEntity<SearchResultDto> {
        val result = searchService.search(q, type, limit, offset)
        return ResponseEntity.ok(result)
    }
}
