package org.bibsonomy.api.config

import org.bibsonomy.api.security.LegacyBasicAuthenticationFilter
import org.bibsonomy.api.security.LegacyBasicAuthenticationProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.beans.factory.annotation.Value

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
    private val legacyAuthenticationEntryPoint: AuthenticationEntryPoint,
    @Value("\${security.permitDocs:false}") private val permitDocs: Boolean
) {

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
            .csrf { it.disable() }
            .cors(Customizer.withDefaults())
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationManager(authenticationManager)
            .authorizeHttpRequests { authorize ->
                val docMatchers = if (permitDocs) {
                    arrayOf("/api/v2/api-docs/**", "/api/v2/swagger-ui.html", "/api/v2/swagger-ui/**")
                } else {
                    emptyArray()
                }
                authorize
                    .requestMatchers(
                        "/api/v2/auth/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        *docMatchers
                    ).permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v2/posts").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v2/posts/**").permitAll()
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
