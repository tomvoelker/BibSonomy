package org.bibsonomy.api.controller

import org.bibsonomy.api.config.SecurityConfig
import org.bibsonomy.api.dto.CurrentUserDto
import org.bibsonomy.api.dto.LoginRequest
import org.bibsonomy.api.dto.LoginResponse
import org.bibsonomy.api.security.LegacyAuthenticationConfiguration
import org.bibsonomy.api.security.LegacyBasicAuthenticationProvider
import org.bibsonomy.api.service.AuthService
import org.bibsonomy.common.enums.Role
import org.bibsonomy.common.exceptions.AccessDeniedException
import org.bibsonomy.model.Group
import org.bibsonomy.model.User
import org.bibsonomy.model.logic.LogicInterface
import org.bibsonomy.model.logic.LogicInterfaceFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

/**
 * Integration tests for the AuthController.
 */
@SpringBootTest(
    classes = [AuthControllerTestApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.main.allow-bean-definition-overriding=true",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
    ]
)
class AuthControllerIntegrationTest(
    @Autowired private val restTemplate: TestRestTemplate
) {

    @Test
    fun `POST login returns 200 with valid credentials`() {
        val request = LoginRequest(username = "testuser", password = "valid-api-key")
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val entity = HttpEntity(request, headers)

        val response = restTemplate.postForEntity(
            "/api/v2/auth/login",
            entity,
            LoginResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals("testuser", response.body?.user?.name)
        assertEquals("valid-api-key", response.body?.token)
    }

    @Test
    fun `POST login returns 401 with invalid credentials`() {
        val request = LoginRequest(username = "testuser", password = "wrong-api-key")
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val entity = HttpEntity(request, headers)

        val response = restTemplate.postForEntity(
            "/api/v2/auth/login",
            entity,
            String::class.java
        )

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    // Note: Testing validation with empty username is complex due to the factory layer
    // This would need more sophisticated mocking to return proper validation errors

    @Test
    fun `POST logout returns 204`() {
        val response = restTemplate.postForEntity(
            "/api/v2/auth/logout",
            null,
            Void::class.java
        )

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `GET me returns 200 with valid authentication`() {
        val headers = HttpHeaders().apply {
            setBasicAuth("testuser", "valid-api-key")
        }
        val entity = HttpEntity<Void>(headers)

        val response = restTemplate.exchange(
            "/api/v2/auth/me",
            HttpMethod.GET,
            entity,
            CurrentUserDto::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals("testuser", response.body?.name)
    }

    @Test
    fun `GET me returns 401 without authentication`() {
        val response = restTemplate.getForEntity(
            "/api/v2/auth/me",
            String::class.java
        )

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }
}

/**
 * Test application configuration for AuthController tests.
 */
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration::class])
@Configuration
@Import(
    SecurityConfig::class,
    LegacyAuthenticationConfiguration::class,
    AuthController::class,
    AuthService::class,
    StubAuthBeans::class
)
class AuthControllerTestApplication

/**
 * Stub LogicInterfaceFactory for testing authentication.
 */
class StubAuthLogicFactory : LogicInterfaceFactory {
    override fun getLogicAccess(loginName: String?, apiKey: String?): LogicInterface {
        // Simulate authentication - only "valid-api-key" is accepted
        // But we need to throw exception for wrong credentials so the auth filter rejects them
        if (loginName != null && apiKey != null && apiKey != "valid-api-key") {
            throw AccessDeniedException("Invalid credentials")
        }

        val logic = Mockito.mock(LogicInterface::class.java)

        if (loginName == "testuser" && apiKey == "valid-api-key") {
            // Authenticated user
            val user = User().apply {
                name = loginName
                realname = "Test User"
                email = "test@example.com"
                this.apiKey = apiKey
                role = Role.DEFAULT
                groups = listOf(
                    Group().apply { name = "public" },
                    Group().apply { name = "test-group" }
                )
            }
            Mockito.`when`(logic.authenticatedUser).thenReturn(user)
        } else {
            // Guest/anonymous access
            val guestUser = User().apply {
                name = null
                role = Role.NOBODY
            }
            Mockito.`when`(logic.authenticatedUser).thenReturn(guestUser)
        }

        return logic
    }
}

@Configuration
class StubAuthBeans {
    @Bean
    fun stubLogicInterfaceFactory(): LogicInterfaceFactory = StubAuthLogicFactory()

    @Bean
    fun legacyBasicAuthenticationProvider(factory: LogicInterfaceFactory): LegacyBasicAuthenticationProvider =
        LegacyBasicAuthenticationProvider(factory)
}
