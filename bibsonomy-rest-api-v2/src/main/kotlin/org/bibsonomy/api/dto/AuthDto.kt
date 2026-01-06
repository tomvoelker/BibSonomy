package org.bibsonomy.api.dto

import java.time.Instant

/**
 * Request body for login endpoint.
 */
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * Response for successful login.
 *
 * Since BibSonomy uses API keys (not passwords) for authentication,
 * users must use their API key as the password. The response returns
 * the same API key as the token for subsequent authenticated requests.
 */
data class LoginResponse(
    val token: String,
    val user: AuthUserDto,
    val expiresAt: Instant?
)

/**
 * User information returned in auth responses.
 *
 * Includes more details than UserRefDto since this is the authenticated user.
 */
data class AuthUserDto(
    val name: String,
    val realName: String?,
    val email: String?,
    val groups: List<String>,
    val apiKey: String? = null
)

/**
 * Response for GET /api/v2/auth/me endpoint.
 *
 * Returns the currently authenticated user's profile information.
 */
data class CurrentUserDto(
    val name: String,
    val realName: String?,
    val email: String?,
    val groups: List<String>,
    val apiKey: String?
)
