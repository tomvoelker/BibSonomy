package org.bibsonomy.api.dto

/**
 * Lightweight reference to a user.
 */
data class UserRefDto(
    val username: String,
    val realName: String?
)
