package org.bibsonomy.api.security

import org.bibsonomy.api.config.SecurityConfig
import org.bibsonomy.common.enums.Role
import org.bibsonomy.common.exceptions.AccessDeniedException
import org.bibsonomy.model.User
import org.bibsonomy.model.logic.LogicInterface
import org.bibsonomy.model.logic.LogicInterfaceFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootTest(
    classes = [TestSecurityApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.main.allow-bean-definition-overriding=true"]
)
class SecurityIntegrationTest(
    @Autowired private val restTemplate: TestRestTemplate
) {

    @Test
    fun `no auth is permitted`() {
        val response = restTemplate.getForEntity("/api/v2/posts", String::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `invalid basic auth still permitted for public endpoint`() {
        val headers = HttpHeaders()
        headers.setBasicAuth("wrong", "creds")
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/api/v2/posts",
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `valid basic auth returns 200`() {
        val headers = HttpHeaders()
        headers.setBasicAuth(StubLogicInterfaceFactory.VALID_USER, StubLogicInterfaceFactory.VALID_API_KEY)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/api/v2/posts",
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `protected POST endpoint requires authentication`() {
        val requestBody = mapOf("title" to "Test Post", "url" to "https://example.com")

        // No auth should return UNAUTHORIZED
        val noAuthResponse: ResponseEntity<String> = restTemplate.postForEntity(
            "/api/v2/posts",
            requestBody,
            String::class.java
        )
        assertEquals(HttpStatus.UNAUTHORIZED, noAuthResponse.statusCode)

        // Invalid auth should return UNAUTHORIZED
        val invalidHeaders = HttpHeaders()
        invalidHeaders.setBasicAuth("invalid", "credentials")
        val invalidAuthResponse: ResponseEntity<String> = restTemplate.exchange(
            "/api/v2/posts",
            HttpMethod.POST,
            HttpEntity(requestBody, invalidHeaders),
            String::class.java
        )
        assertEquals(HttpStatus.UNAUTHORIZED, invalidAuthResponse.statusCode)

        // Valid auth should succeed
        val validHeaders = HttpHeaders()
        validHeaders.setBasicAuth(StubLogicInterfaceFactory.VALID_USER, StubLogicInterfaceFactory.VALID_API_KEY)
        val validAuthResponse: ResponseEntity<String> = restTemplate.exchange(
            "/api/v2/posts",
            HttpMethod.POST,
            HttpEntity(requestBody, validHeaders),
            String::class.java
        )
        assertEquals(HttpStatus.CREATED, validAuthResponse.statusCode)
    }
}

/**
 * Minimal application wiring security only, with a dummy posts endpoint.
 */
@SpringBootApplication
@Import(SecurityConfig::class, LegacyAuthenticationConfiguration::class, StubBeans::class)
class TestSecurityApplication

@RestController
@RequestMapping("/api/v2/posts")
class DummyPostsController {
    @GetMapping
    fun list(): ResponseEntity<String> = ResponseEntity.ok("ok")

    @PostMapping
    fun create(@RequestBody body: Map<String, Any>): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.CREATED).body("created")
}

class StubLogicInterfaceFactory : LogicInterfaceFactory {
    override fun getLogicAccess(loginName: String?, apiKey: String?): LogicInterface {
        // Validate credentials: only accept the valid test credentials or guest access
        if (loginName != null && apiKey != null) {
            if (loginName != VALID_USER || apiKey != VALID_API_KEY) {
                throw AccessDeniedException("Invalid credentials: $loginName")
            }
        }

        val logic = Mockito.mock(LogicInterface::class.java)
        val user = User().apply {
            name = loginName ?: "guest"
            role = if (loginName == VALID_USER) Role.ADMIN else Role.DEFAULT
        }
        Mockito.`when`(logic.authenticatedUser).thenReturn(user)
        return logic
    }

    companion object {
        // Use test database credentials from bibsonomy-database/src/test/resources/database/insert-test-data.sql
        const val VALID_USER = "testuser1"
        const val VALID_API_KEY = "11111111111111111111111111111111"
    }
}

/**
 * Provides stubbed beans for tests.
 */
@Configuration
class StubBeans {
    @Bean
    fun stubLogicInterfaceFactory(): LogicInterfaceFactory = StubLogicInterfaceFactory()

    @Bean
    @Primary
    fun logicInterface(factory: LogicInterfaceFactory): LogicInterface = factory.getLogicAccess(null, null)
}
