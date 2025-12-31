package org.bibsonomy.api.controller

import org.bibsonomy.api.dto.TagDto
import org.bibsonomy.api.service.TagService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for tags endpoints.
 *
 * Implements the /api/v2/tags endpoint as defined in the OpenAPI specification.
 */
@RestController
@RequestMapping("/api/v2/tags")
class TagsController(
    private val tagService: TagService
) {

    /**
     * GET /api/v2/tags - List tags
     *
     * Returns a list of tags with optional cloud data (frequencies).
     *
     * @param offset Number of items to skip (default: 0)
     * @param limit Number of items to return (default: 20)
     * @param minFreq Minimum frequency for tag cloud filtering
     * @param maxCount Maximum number of tags to return
     */
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun listTags(
        @RequestParam(defaultValue = "0") offset: Int,
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(required = false) minFreq: Int?,
        @RequestParam(required = false) maxCount: Int?
    ): List<TagDto> {
        return tagService.listTags(
            offset = offset,
            limit = limit,
            minFreq = minFreq,
            maxCount = maxCount
        )
    }
}
