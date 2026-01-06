package org.bibsonomy.api.service

import org.bibsonomy.api.dto.AuthUserDto
import org.bibsonomy.api.dto.CurrentUserDto
import org.bibsonomy.api.dto.LoginRequest
import org.bibsonomy.api.dto.LoginResponse
import org.bibsonomy.common.exceptions.AccessDeniedException
import org.bibsonomy.model.logic.LogicInterface
import org.bibsonomy.model.logic.LogicInterfaceFactory
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

/**
 * Service for authentication operations.
 *
 * BibSonomy uses API keys for authentication rather than passwords.
 * Users authenticate with their username and API key via Basic Auth.
 * This service provides a login endpoint that validates credentials
 * and returns the API key as a token for subsequent requests.
 */
@Service
class AuthService(
    private val logicInterfaceFactory: LogicInterfaceFactory,
    private val logic: LogicInterface
) {
    private val log = LoggerFactory.getLogger(AuthService::class.java)

    /**
     * Authenticate user with username and API key (password field).
     *
     * BibSonomy's legacy system uses API keys stored in the database,
     * not traditional passwords. The login endpoint validates the
     * username/apiKey combination and returns a token for use in
     * subsequent authenticated requests.
     *
     * @param request Login credentials (username + API key as password)
     * @return LoginResponse with token and user details
     * @throws ResponseStatusException 401 if credentials are invalid
     */
    fun login(request: LoginRequest): LoginResponse {
        if (request.username.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required")
        }
        if (request.password.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Password/API key is required")
        }

        try {
            // Validate credentials by trying to get logic access
            val authenticatedLogic = logicInterfaceFactory.getLogicAccess(request.username, request.password)
            val user = authenticatedLogic.authenticatedUser
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")

            // Get user's groups
            val groups = user.groups?.mapNotNull { it.name } ?: emptyList()

            return LoginResponse(
                token = request.password, // Return API key as token for Basic Auth
                user = AuthUserDto(
                    name = user.name ?: request.username,
                    realName = user.realname,
                    email = user.email,
                    groups = groups,
                    apiKey = user.apiKey
                ),
                expiresAt = null // API keys don't expire
            )
        } catch (ex: AccessDeniedException) {
            log.warn("Login failed for user '{}': {}", request.username, ex.message)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
        }
    }

    /**
     * Get the currently authenticated user's information.
     *
     * @return CurrentUserDto with user details
     * @throws ResponseStatusException 401 if not authenticated
     */
    fun getCurrentUser(): CurrentUserDto {
        val user = logic.authenticatedUser
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated")

        // Check if this is a guest/anonymous user
        if (user.name.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated")
        }

        val groups = user.groups?.mapNotNull { it.name } ?: emptyList()

        return CurrentUserDto(
            name = user.name,
            realName = user.realname,
            email = user.email,
            groups = groups,
            apiKey = user.apiKey
        )
    }

    /**
     * Logout the current session.
     *
     * Since BibSonomy uses stateless Basic Auth with API keys,
     * there's no server-side session to invalidate. This endpoint
     * exists for API completeness and future token-based auth.
     * Currently it's a no-op that always succeeds.
     */
    fun logout() {
        // No-op for stateless Basic Auth
        // Future: invalidate JWT tokens or session tokens here
        log.debug("Logout called - no-op for stateless auth")
    }
}
