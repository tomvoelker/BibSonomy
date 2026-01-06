package org.bibsonomy.api.dto

/**
 * Lightweight reference to a group.
 */
data class GroupRefDto(
    val name: String,
    val displayName: String?
)

/**
 * DTO for group list item.
 *
 * Returned by GET /api/v2/groups.
 */
data class GroupDto(
    val name: String,
    val description: String?,
    val memberCount: Int,
    val isPrivate: Boolean
)

/**
 * DTO for group details.
 *
 * Returned by GET /api/v2/groups/{groupName}.
 */
data class GroupDetailsDto(
    val name: String,
    val description: String?,
    val memberCount: Int,
    val isPrivate: Boolean,
    val postCount: Int
)
