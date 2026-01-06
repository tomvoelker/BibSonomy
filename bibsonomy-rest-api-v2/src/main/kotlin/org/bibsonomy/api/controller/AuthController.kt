package org.bibsonomy.api.controller

import jakarta.validation.Valid
import org.bibsonomy.api.dto.CurrentUserDto
import org.bibsonomy.api.dto.LoginRequest
import org.bibsonomy.api.dto.LoginResponse
import org.bibsonomy.api.service.AuthService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for authentication endpoints.
 *
 * Provides login, logout, and current user endpoints.
 *
 * BibSonomy uses API keys for authentication:
 * - Login: POST /api/v2/auth/login with username + API key (in password field)
 * - Subsequent requests: Basic Auth header with username:apiKey
 *
 * Note: These endpoints supplement the existing Basic Auth filter.
 * The login endpoint is useful for SPAs that want to validate credentials
 * before storing them for use in subsequent requests.
 */
@RestController
@RequestMapping("/api/v2/auth")
class AuthController(
    private val authService: AuthService
) {

    /**
     * POST /api/v2/auth/login - Authenticate user with credentials.
     *
     * Validates username and API key, returning user info and token on success.
     * The token is the same API key, for use in Basic Auth headers.
     *
     * @param request Login credentials (username + password/apiKey)
     * @return LoginResponse with token and user details
     */
    @PostMapping("/login", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    /**
     * POST /api/v2/auth/logout - Invalidate the current session.
     *
     * For stateless Basic Auth, this is a no-op. It exists for API
     * completeness and will be used when token-based auth is implemented.
     *
     * @return 204 No Content on success
     */
    @PostMapping("/logout")
    fun logout(): ResponseEntity<Void> {
        authService.logout()
        return ResponseEntity.noContent().build()
    }

    /**
     * GET /api/v2/auth/me - Get the currently authenticated user.
     *
     * Requires authentication via Basic Auth header.
     * Returns 401 if not authenticated.
     *
     * @return CurrentUserDto with user details
     */
    @GetMapping("/me", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getCurrentUser(): ResponseEntity<CurrentUserDto> {
        val user = authService.getCurrentUser()
        return ResponseEntity.ok(user)
    }
}
