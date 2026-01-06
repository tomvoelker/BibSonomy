package org.bibsonomy.api.dto

import java.time.Instant

/**
 * Lightweight reference to a user.
 */
data class UserRefDto(
    val username: String,
    val realName: String?
)

/**
 * Full user profile DTO.
 *
 * Returned by GET /api/v2/users/{username}.
 * Email is only included for self-requests or admin users.
 */
data class UserProfileDto(
    val name: String,
    val realName: String?,
    val email: String?, // Only for self/admin requests
    val postCount: Int,
    val tagCount: Int,
    val groups: List<String>,
    val registered: Instant?,
    val homepage: String?,
    val institution: String?,
    val interests: String?
)

/**
 * Request body for user registration.
 */
data class UserRegistrationRequest(
    val username: String,
    val password: String,
    val email: String,
    val realName: String?
)

/**
 * Response for successful user registration.
 */
data class UserRegistrationResponse(
    val name: String,
    val email: String,
    val created: Instant
)
