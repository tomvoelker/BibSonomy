package org.bibsonomy.api.dto

/**
 * DTO for a tag.
 */
data class TagDto(
    val name: String,
    val count: Int?,
    val countPublic: Int?
)
