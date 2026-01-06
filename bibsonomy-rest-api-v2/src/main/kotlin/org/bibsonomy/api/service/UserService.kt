package org.bibsonomy.api.service

import org.bibsonomy.api.dto.TagDto
import org.bibsonomy.api.dto.UserProfileDto
import org.bibsonomy.api.dto.UserRegistrationRequest
import org.bibsonomy.api.dto.UserRegistrationResponse
import org.bibsonomy.api.mapper.toDto
import org.bibsonomy.common.enums.GroupingEntity
import org.bibsonomy.common.enums.QueryScope
import org.bibsonomy.common.enums.Role
import org.bibsonomy.common.enums.SortKey
import org.bibsonomy.model.Resource
import org.bibsonomy.model.User
import org.bibsonomy.model.logic.LogicInterface
import org.bibsonomy.model.util.UserUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

/**
 * Service for user operations.
 *
 * Provides user profile retrieval, registration, and user-specific data.
 */
@Service
class UserService(
    private val logic: LogicInterface
) {
    private val log = LoggerFactory.getLogger(UserService::class.java)

    /**
     * Get a user's public profile.
     *
     * @param username The username to retrieve
     * @param includeEmail Whether to include email (for self/admin requests)
     * @return UserProfileDto with profile information
     * @throws ResponseStatusException 404 if user not found
     */
    fun getUserProfile(username: String, includeEmail: Boolean = false): UserProfileDto {
        if (username.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required")
        }

        // Query for the user using LogicInterface
        val users = logic.getUsers(
            Resource::class.java,
            GroupingEntity.USER,
            username,
            null,   // tags
            null,   // hash
            SortKey.NONE,
            null,   // relation
            null,   // search
            0,      // start
            1       // end - just get one user
        )

        val user = users?.firstOrNull()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: $username")

        // Get post count
        val postStats = try {
            logic.getPostStatistics(
                Resource::class.java,
                GroupingEntity.USER,
                username,
                null, null, null, null,
                SortKey.NONE, null, null, 0, 0
            )
        } catch (e: Exception) {
            log.warn("Failed to get post stats for user {}: {}", username, e.message)
            null
        }
        val postCount = postStats?.count ?: 0

        // Get tag count for this user
        val userTags = try {
            logic.getTags(
                Resource::class.java,
                GroupingEntity.USER,
                username,
                null, null, null,
                QueryScope.LOCAL,
                null, null,
                SortKey.FREQUENCY,
                null, null,
                0, Int.MAX_VALUE
            )
        } catch (e: Exception) {
            log.warn("Failed to get tags for user {}: {}", username, e.message)
            emptyList()
        }
        val tagCount = userTags?.size ?: 0

        // Get user's groups
        val groups = user.groups?.mapNotNull { it.name } ?: emptyList()

        return UserProfileDto(
            name = user.name ?: username,
            realName = user.realname,
            email = if (includeEmail) user.email else null,
            postCount = postCount,
            tagCount = tagCount,
            groups = groups,
            registered = user.registrationDate?.toInstant(),
            homepage = user.homepage?.toString(),
            institution = user.institution,
            interests = user.interests
        )
    }

    /**
     * Get tags for a specific user.
     *
     * @param username The username whose tags to retrieve
     * @param limit Maximum number of tags to return
     * @param minFreq Optional minimum frequency filter
     * @return List of TagDto for the user
     */
    fun getUserTags(username: String, limit: Int = 50, minFreq: Int? = null): List<TagDto> {
        if (username.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required")
        }

        // First verify user exists
        val users = logic.getUsers(
            Resource::class.java,
            GroupingEntity.USER,
            username,
            null, null,
            SortKey.NONE,
            null, null,
            0, 1
        )

        if (users.isNullOrEmpty()) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: $username")
        }

        // Get tags for this user
        val effectiveLimit = limit.coerceIn(1, 500)
        val fetchEnd = if (minFreq != null) effectiveLimit * 3 else effectiveLimit

        val tags = logic.getTags(
            Resource::class.java,
            GroupingEntity.USER,
            username,
            null,   // tags
            null,   // hash
            null,   // search
            QueryScope.LOCAL,
            null,   // regex
            null,   // relation
            SortKey.FREQUENCY,  // Sort by frequency descending
            null,   // startDate
            null,   // endDate
            0,
            fetchEnd
        )

        val resultTags = if (minFreq != null) {
            tags?.filter { (it.usercount ?: 0) >= minFreq }
                ?.take(effectiveLimit) ?: emptyList()
        } else {
            tags?.take(effectiveLimit) ?: emptyList()
        }

        return resultTags.map { it.toDto() }
    }

    /**
     * Register a new user.
     *
     * @param request Registration details
     * @return UserRegistrationResponse with created user info
     * @throws ResponseStatusException 400 for validation errors, 409 for conflicts
     */
    fun registerUser(request: UserRegistrationRequest): UserRegistrationResponse {
        // Validate request
        if (request.username.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required")
        }
        if (request.username.length < 3) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username must be at least 3 characters")
        }
        if (request.username.length > 30) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username must not exceed 30 characters")
        }
        if (!request.username.matches(Regex("^[a-zA-Z0-9_-]+$"))) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username can only contain letters, numbers, underscores, and hyphens")
        }
        if (request.password.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required")
        }
        if (request.password.length < 8) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters")
        }
        if (request.email.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required")
        }
        if (!request.email.contains("@")) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email address")
        }

        // Check if username already exists
        val existingUsers = logic.getUsers(
            Resource::class.java,
            GroupingEntity.USER,
            request.username.lowercase(),
            null, null,
            SortKey.NONE,
            null, null,
            0, 1
        )
        if (!existingUsers.isNullOrEmpty()) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Username already exists")
        }

        // Create the user with properly hashed password
        val user = User().apply {
            name = request.username.lowercase()
            email = request.email
            realname = request.realName
            role = Role.DEFAULT
        }
        // Hash the password with salt using legacy utility
        UserUtils.setupPassword(user, request.password)

        try {
            logic.createUser(user)
            log.info("Created new user: {}", request.username)
        } catch (e: Exception) {
            log.error("Failed to create user {}: {}", request.username, e.message, e)
            if (e.message?.contains("duplicate", ignoreCase = true) == true ||
                e.message?.contains("already exists", ignoreCase = true) == true) {
                throw ResponseStatusException(HttpStatus.CONFLICT, "Username or email already exists")
            }
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user")
        }

        return UserRegistrationResponse(
            name = request.username.lowercase(),
            email = request.email,
            created = Instant.now()
        )
    }
}
