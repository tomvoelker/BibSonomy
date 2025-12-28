package org.bibsonomy.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class ApiCorsConfig {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val defaultOrigins = listOf(
            "http://localhost:5173",
            "http://localhost:4173"
        )
        val extraOrigins = System.getenv("BIBSONOMY_CORS_ALLOWED_ORIGINS")
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
        val originPatterns = System.getenv("BIBSONOMY_CORS_ALLOWED_ORIGIN_PATTERNS")
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        val config = CorsConfiguration().apply {
            allowedOrigins = defaultOrigins + extraOrigins
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
