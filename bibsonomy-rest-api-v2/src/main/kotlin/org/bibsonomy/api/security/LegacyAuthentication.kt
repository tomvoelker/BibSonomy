package org.bibsonomy.api.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.bibsonomy.common.exceptions.AccessDeniedException
import org.bibsonomy.model.logic.LogicInterface
import org.bibsonomy.model.logic.LogicInterfaceFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.web.context.annotation.RequestScope
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.filter.OncePerRequestFilter
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * Authentication token that carries the legacy LogicInterface for downstream services.
 */
class LogicAuthenticationToken(
    private val username: String,
    private val logic: LogicInterface,
    authorities: Collection<SimpleGrantedAuthority>
) : AbstractAuthenticationToken(authorities) {
    init {
        isAuthenticated = true
    }

    override fun getCredentials(): Any = ""
    override fun getPrincipal(): Any = username
    fun logic(): LogicInterface = logic
}

/**
 * Authentication provider that reuses the legacy DBLogic API-key authentication.
 */
class LegacyBasicAuthenticationProvider(
    private val logicInterfaceFactory: LogicInterfaceFactory
) : org.springframework.security.authentication.AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.name
        val apiKey = authentication.credentials?.toString() ?: ""
        if (username.isBlank() || apiKey.isBlank()) {
            throw BadCredentialsException("Missing credentials")
        }

        val logic = try {
            logicInterfaceFactory.getLogicAccess(username, apiKey)
        } catch (ex: AccessDeniedException) {
            throw BadCredentialsException("Invalid credentials", ex)
        }
        val user = logic.authenticatedUser
        val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))

        return LogicAuthenticationToken(username, logic, authorities).also {
            it.details = user
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}

/**
 * Minimal Basic auth filter that mirrors the legacy REST server behaviour (username + API key).
 */
class LegacyBasicAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
    private val entryPoint: AuthenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val existing = org.springframework.security.core.context.SecurityContextHolder.getContext().authentication
        if (existing?.isAuthenticated == true) {
            filterChain.doFilter(request, response)
            return
        }

        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (header == null || !header.startsWith(BASIC_PREFIX)) {
            // Allow anonymous access on public endpoints; otherwise fall through without auth.
            filterChain.doFilter(request, response)
            return
        }

        try {
            val (username, apiKey) = parseBasicHeader(header)
            val authRequest = UsernamePasswordAuthenticationToken(username, apiKey)
            val authResult = authenticationManager.authenticate(authRequest)
            org.springframework.security.core.context.SecurityContextHolder.getContext().authentication = authResult
            filterChain.doFilter(request, response)
        } catch (ex: AuthenticationException) {
            entryPoint.commence(request, response, ex)
        }
    }

    private fun parseBasicHeader(header: String): Pair<String, String> {
        val base64Token = header.removePrefix(BASIC_PREFIX).trim()
        val decoded = String(Base64.getDecoder().decode(base64Token), StandardCharsets.UTF_8)
        val delim = decoded.indexOf(':')
        if (delim < 0) throw BadCredentialsException("Invalid basic authentication token")
        val username = decoded.substring(0, delim)
        val apiKey = decoded.substring(delim + 1)
        return username to apiKey
    }

    companion object {
        internal const val BASIC_PREFIX = "Basic "
    }
}

private fun decodeBasic(header: String): Pair<String, String> {
    val base64Token = header.removePrefix(LegacyBasicAuthenticationFilter.BASIC_PREFIX).trim()
    val decoded = String(Base64.getDecoder().decode(base64Token), StandardCharsets.UTF_8)
    val delim = decoded.indexOf(':')
    if (delim < 0) throw BadCredentialsException("Invalid basic authentication token")
    val username = decoded.substring(0, delim)
    val apiKey = decoded.substring(delim + 1)
    return username to apiKey
}

/**
 * Bean definitions supporting the legacy auth flow.
 */
@Configuration
class LegacyAuthenticationConfiguration {

    @Bean
    fun legacyAuthenticationEntryPoint(): AuthenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)

    @Bean
    fun legacyBasicAuthenticationProvider(
        logicInterfaceFactory: LogicInterfaceFactory
    ): LegacyBasicAuthenticationProvider = LegacyBasicAuthenticationProvider(logicInterfaceFactory)

    /**
     * Request-scoped LogicInterface derived from the authenticated SecurityContext.
     * This replaces the dummy admin LogicInterface used for the MVP.
     */
    @Bean
    @RequestScope(proxyMode = ScopedProxyMode.INTERFACES)
    fun authenticatedLogicInterface(logicInterfaceFactory: LogicInterfaceFactory): LogicInterface {
        val auth = org.springframework.security.core.context.SecurityContextHolder.getContext().authentication
        val token = auth as? LogicAuthenticationToken
        if (token != null) return token.logic()

        // Allow optional auth on public GET /posts: derive credentials from the Basic header if present.
        val request = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
        val header = request?.getHeader(HttpHeaders.AUTHORIZATION)
        if (header != null && header.startsWith(LegacyBasicAuthenticationFilter.BASIC_PREFIX)) {
            val (username, apiKey) = decodeBasic(header)
            return logicInterfaceFactory.getLogicAccess(username, apiKey)
        }

        // Fallback to guest logic (public-only access).
        return logicInterfaceFactory.getLogicAccess(null, null)
    }
}
