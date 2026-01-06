package org.bibsonomy.api.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.bibsonomy.api.dto.TagDto
import org.bibsonomy.api.dto.UserProfileDto
import org.bibsonomy.api.dto.UserRegistrationRequest
import org.bibsonomy.api.dto.UserRegistrationResponse
import org.bibsonomy.api.service.UserService
import org.bibsonomy.model.logic.LogicInterface
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * REST controller for user endpoints.
 *
 * Provides user profile retrieval, registration, and user-specific data.
 *
 * Public endpoints:
 * - GET /api/v2/users/{username} - Get user profile
 * - GET /api/v2/users/{username}/tags - Get user's tags
 *
 * Protected endpoints:
 * - POST /api/v2/users - Register new user (public but typically protected in production)
 */
@RestController
@RequestMapping("/api/v2/users")
@Validated
class UsersController(
    private val userService: UserService,
    private val logic: LogicInterface
) {

    /**
     * GET /api/v2/users/{username} - Get user's public profile.
     *
     * Returns the user's profile information. Email is only included
     * if the authenticated user is viewing their own profile.
     *
     * @param username The username to retrieve
     * @return UserProfileDto with profile information
     */
    @GetMapping("/{username}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUserProfile(@PathVariable username: String): ResponseEntity<UserProfileDto> {
        // Check if this is a self-request (authenticated user viewing their own profile)
        val authenticatedUser = logic.authenticatedUser
        val includeEmail = authenticatedUser?.name?.equals(username, ignoreCase = true) == true

        val profile = userService.getUserProfile(username, includeEmail)
        return ResponseEntity.ok(profile)
    }

    /**
     * GET /api/v2/users/{username}/tags - Get user's tags.
     *
     * Returns tags used by the specified user, sorted by frequency.
     *
     * @param username The username whose tags to retrieve
     * @param limit Maximum tags to return (default: 50, max: 500)
     * @param minFreq Optional minimum frequency filter
     * @return List of TagDto
     */
    @GetMapping("/{username}/tags", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUserTags(
        @PathVariable username: String,
        @RequestParam(defaultValue = "50") @Min(1) @Max(500) limit: Int,
        @RequestParam(required = false) minFreq: Int?
    ): ResponseEntity<List<TagDto>> {
        val tags = userService.getUserTags(username, limit, minFreq)
        return ResponseEntity.ok(tags)
    }

    /**
     * POST /api/v2/users - Register a new user.
     *
     * Creates a new user account with the provided credentials.
     *
     * @param request Registration details
     * @return UserRegistrationResponse with created user info
     */
    @PostMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun registerUser(@Valid @RequestBody request: UserRegistrationRequest): ResponseEntity<UserRegistrationResponse> {
        val response = userService.registerUser(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}
