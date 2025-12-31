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
 * Allowed origins are configurable via the `cors.allowed-origins` property.
 * Defaults to localhost development servers if not specified.
 */
@Configuration
class ApiCorsConfig(
    @Value("\${cors.allowed-origins:http://localhost:5173,http://localhost:4173}")
    private val allowedOrigins: String
) {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            this.allowedOrigins = this@ApiCorsConfig.allowedOrigins
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("Authorization", "Content-Type", "Accept")
            allowCredentials = true
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/api/v2/**", config)
        }
    }
}
