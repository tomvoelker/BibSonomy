package org.bibsonomy.api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * CORS configuration for the REST API.
 *
 * Configuration options:
 * - cors.allowed-origins: Comma-separated additional allowed origins
 * - cors.allowed-origin-patterns: Comma-separated origin patterns for wildcards
 *
 * Localhost development servers (5173, 4173) are always allowed.
 */
@Configuration
class ApiCorsConfig(
    @Value("\${cors.allowed-origins:}") private val extraOriginsConfig: String,
    @Value("\${cors.allowed-origin-patterns:}") private val originPatternsConfig: String
) {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val defaultOrigins = listOf("http://localhost:5173", "http://localhost:4173")
        val extraOrigins = extraOriginsConfig.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val originPatterns = originPatternsConfig.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        // Spring CORS precedence: allowedOriginPatterns are checked first. If any pattern
        // matches, that result is used. Only if no pattern matches does it fall back to
        // allowedOrigins. Both can be set safely - patterns take precedence.
        val config = CorsConfiguration().apply {
            allowedOrigins = (defaultOrigins + extraOrigins).distinct()
            if (originPatterns.isNotEmpty()) {
                allowedOriginPatterns = originPatterns
            }
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("Authorization", "Content-Type", "Accept")
            allowCredentials = true
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/api/v2/**", config)
        }
    }
}
