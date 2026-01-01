package org.bibsonomy.api.config

import org.bibsonomy.api.security.LegacyBasicAuthenticationFilter
import org.bibsonomy.api.security.LegacyBasicAuthenticationProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.http.HttpMethod

/**
 * Security configuration for REST API v2.
 *
 * Reintroduces legacy Basic+API-key authentication (compatibility with v1) and
 * removes the permit-all dummy admin shortcut.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val legacyBasicAuthenticationProvider: LegacyBasicAuthenticationProvider,
    private val legacyAuthenticationEntryPoint: AuthenticationEntryPoint
) {

    /**
     * Security filter chain for public paths that don't require authentication.
     * This includes Swagger UI, API docs, and API root redirect.
     */
    @Bean
    @Order(1)
    fun publicSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/v3/api-docs",
                "/webjars/**",
                "/swagger-resources/**",
                "/api/v2",
                "/api/v2/"
            )
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .csrf { it.disable() }
        return http.build()
    }

    /**
     * Main security filter chain for API endpoints.
     */
    @Bean
    @Order(2)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        // Build authentication manager for this chain
        val authBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authBuilder.authenticationProvider(legacyBasicAuthenticationProvider)
        val authenticationManager = authBuilder.build()

        http
            .cors(Customizer.withDefaults())
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationManager(authenticationManager)
            .authorizeHttpRequests { authorize ->
                authorize
                    // Permit CORS preflight requests (OPTIONS) for all API endpoints
                    .requestMatchers(HttpMethod.OPTIONS, "/api/v2/**").permitAll()
                    // Permit auth endpoints
                    .requestMatchers("/api/v2/auth/**").permitAll()
                    // Permit all GET requests to posts endpoints (list and single-post)
                    .requestMatchers(HttpMethod.GET, "/api/v2/posts").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v2/posts/").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v2/posts/*").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v2/posts/**").permitAll()
                    // Permit GET for tags endpoint too (public listing)
                    .requestMatchers(HttpMethod.GET, "/api/v2/tags").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v2/tags/**").permitAll()
                    .anyRequest().authenticated()
            }
            .exceptionHandling { it.authenticationEntryPoint(legacyAuthenticationEntryPoint) }
            .addFilterBefore(
                LegacyBasicAuthenticationFilter(authenticationManager, legacyAuthenticationEntryPoint),
                UsernamePasswordAuthenticationFilter::class.java
            )
        return http.build()
    }
}
