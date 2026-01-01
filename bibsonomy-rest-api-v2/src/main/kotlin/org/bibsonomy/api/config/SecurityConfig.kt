package org.bibsonomy.api.config

import org.bibsonomy.api.security.LegacyBasicAuthenticationFilter
import org.bibsonomy.api.security.LegacyBasicAuthenticationProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
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
     * Completely bypass security filter chain for Swagger/OpenAPI static resources.
     * These paths will not go through any security filters at all.
     */
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring()
                .requestMatchers(AntPathRequestMatcher("/swagger-ui/**"))
                .requestMatchers(AntPathRequestMatcher("/swagger-ui.html"))
                .requestMatchers(AntPathRequestMatcher("/api/v2/swagger-ui/**"))
                .requestMatchers(AntPathRequestMatcher("/api/v2/swagger-ui.html"))
                .requestMatchers(AntPathRequestMatcher("/v3/api-docs/**"))
                .requestMatchers(AntPathRequestMatcher("/v3/api-docs"))
                .requestMatchers(AntPathRequestMatcher("/api/v2/api-docs/**"))
                .requestMatchers(AntPathRequestMatcher("/api/v2/api-docs"))
                .requestMatchers(AntPathRequestMatcher("/webjars/**"))
                .requestMatchers(AntPathRequestMatcher("/swagger-resources/**"))
                .requestMatchers(AntPathRequestMatcher("/swagger-config/**"))
                .requestMatchers(AntPathRequestMatcher("/api/v2/swagger-config/**"))
        }
    }

    @Bean
    fun authenticationManager(http: HttpSecurity): AuthenticationManager {
        val builder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        builder.authenticationProvider(legacyBasicAuthenticationProvider)
        return builder.build()
    }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager
    ): SecurityFilterChain {
        http
            .cors(Customizer.withDefaults())
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationManager(authenticationManager)
            .authorizeHttpRequests { authorize ->
                authorize
                    // Permit CORS preflight requests (OPTIONS) for all API endpoints
                    .requestMatchers(HttpMethod.OPTIONS, "/api/v2/**").permitAll()
                    // Permit Swagger/OpenAPI paths - use AntPathRequestMatcher for static resources
                    .requestMatchers(AntPathRequestMatcher("/api/v2")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/api/v2/")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/swagger-ui.html")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/v3/api-docs")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/api/v2/swagger-ui/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/api/v2/swagger-ui.html")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/api/v2/api-docs/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/api/v2/api-docs")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/webjars/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/swagger-config/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/api/v2/swagger-config/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/swagger-resources/**")).permitAll()
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
