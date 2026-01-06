package org.bibsonomy.api.security

import org.springframework.security.authentication.BadCredentialsException
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * Utility for decoding HTTP Basic Authentication headers.
 */
object BasicAuthUtils {

    const val BASIC_PREFIX = "Basic "

    /**
     * Decode a Basic Authentication header value.
     *
     * @param header The Authorization header value (e.g., "Basic dXNlcjpwYXNz")
     * @return Pair of (username, password/apiKey)
     * @throws BadCredentialsException if the header is malformed
     */
    fun decode(header: String): Pair<String, String> {
        val base64Token = header.removePrefix(BASIC_PREFIX).trim()
        val decoded = try {
            String(Base64.getDecoder().decode(base64Token), StandardCharsets.UTF_8)
        } catch (e: IllegalArgumentException) {
            throw BadCredentialsException("Invalid basic authentication token: malformed Base64", e)
        }

        val delim = decoded.indexOf(':')
        if (delim < 0) {
            throw BadCredentialsException("Invalid basic authentication token: missing ':' delimiter")
        }

        val username = decoded.substring(0, delim)
        val apiKey = decoded.substring(delim + 1)
        return username to apiKey
    }
}
