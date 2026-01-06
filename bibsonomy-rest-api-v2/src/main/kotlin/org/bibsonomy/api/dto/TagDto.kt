package org.bibsonomy.api.dto

/**
 * DTO for a tag.
 */
data class TagDto(
    val name: String,
    val count: Int?,
    val countPublic: Int?
)

/**
 * DTO for tag details including related tags.
 *
 * Returned by GET /api/v2/tags/{tagName}.
 */
data class TagDetailsDto(
    val name: String,
    val count: Int,
    val relatedTags: List<RelatedTagDto>
)

/**
 * DTO for a related tag with co-occurrence information.
 */
data class RelatedTagDto(
    val name: String,
    val count: Int
)
