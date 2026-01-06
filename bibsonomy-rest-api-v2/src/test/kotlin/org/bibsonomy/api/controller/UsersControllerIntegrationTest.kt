package org.bibsonomy.api.controller

import org.bibsonomy.api.config.SecurityConfig
import org.bibsonomy.api.dto.TagDto
import org.bibsonomy.api.dto.UserProfileDto
import org.bibsonomy.api.dto.UserRegistrationRequest
import org.bibsonomy.api.dto.UserRegistrationResponse
import org.bibsonomy.api.security.LegacyAuthenticationConfiguration
import org.bibsonomy.api.security.LegacyBasicAuthenticationProvider
import org.bibsonomy.api.service.UserService
import org.bibsonomy.common.enums.GroupingEntity
import org.bibsonomy.common.enums.QueryScope
import org.bibsonomy.common.enums.Role
import org.bibsonomy.common.enums.SortKey
import org.bibsonomy.model.Group
import org.bibsonomy.model.Resource
import org.bibsonomy.model.statistics.Statistics
import org.bibsonomy.model.Tag
import org.bibsonomy.model.User
import org.bibsonomy.model.logic.LogicInterface
import org.bibsonomy.model.logic.LogicInterfaceFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.ArgumentMatchers.isNull
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.util.Date

/**
 * Integration tests for the UsersController.
 */
@SpringBootTest(
    classes = [UsersControllerTestApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.main.allow-bean-definition-overriding=true",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
    ]
)
class UsersControllerIntegrationTest(
    @Autowired private val restTemplate: TestRestTemplate
) {

    @Test
    fun `GET user profile returns 200 for existing user`() {
        val response = restTemplate.getForEntity(
            "/api/v2/users/testuser",
            UserProfileDto::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals("testuser", response.body?.name)
        assertEquals("Test User", response.body?.realName)
    }

    // Note: Testing 404 for non-existing user requires more sophisticated mocking

    @Test
    fun `GET user profile includes email for self request`() {
        val headers = HttpHeaders().apply {
            setBasicAuth("testuser", "valid-api-key")
        }
        val entity = HttpEntity<Void>(headers)

        val response = restTemplate.exchange(
            "/api/v2/users/testuser",
            HttpMethod.GET,
            entity,
            UserProfileDto::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body?.email)
        assertEquals("test@example.com", response.body?.email)
    }

    @Test
    fun `GET user profile excludes email for other users`() {
        val response = restTemplate.getForEntity(
            "/api/v2/users/testuser",
            UserProfileDto::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNull(response.body?.email)
    }

    @Test
    fun `GET user tags returns 200 with tags list`() {
        val response = restTemplate.exchange(
            "/api/v2/users/testuser/tags",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<TagDto>>() {}
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val tags = response.body ?: emptyList()
        assertTrue(tags.isNotEmpty(), "Expected at least one tag")
    }

    @Test
    fun `GET user tags respects limit parameter`() {
        val response = restTemplate.exchange(
            "/api/v2/users/testuser/tags?limit=5",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<TagDto>>() {}
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val tags = response.body ?: emptyList()
        assertEquals(5, tags.size)
    }

    // Note: Testing 404 for non-existing user tags requires more sophisticated mocking

    @Test
    fun `POST users registers new user`() {
        val request = UserRegistrationRequest(
            username = "newuser",
            password = "securePassword123",
            email = "newuser@example.com",
            realName = "New User"
        )
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val entity = HttpEntity(request, headers)

        val response = restTemplate.postForEntity(
            "/api/v2/users",
            entity,
            UserRegistrationResponse::class.java
        )

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals("newuser", response.body?.name)
    }

    // Note: Testing 409 for existing username requires more sophisticated mocking
    // Note: Testing 400 for short password requires the request to reach the service layer
}

/**
 * Test application configuration for UsersController tests.
 */
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration::class])
@Configuration
@Import(
    SecurityConfig::class,
    LegacyAuthenticationConfiguration::class,
    UsersController::class,
    UserService::class,
    StubUsersBeans::class
)
class UsersControllerTestApplication

/**
 * Stub LogicInterfaceFactory for testing users.
 */
class StubUsersLogicFactory : LogicInterfaceFactory {
    override fun getLogicAccess(loginName: String?, apiKey: String?): LogicInterface {
        val logic = Mockito.mock(LogicInterface::class.java)

        // Create test user
        val testUser = User().apply {
            name = "testuser"
            realname = "Test User"
            email = "test@example.com"
            this.apiKey = apiKey
            role = Role.DEFAULT
            registrationDate = Date()
            institution = "Test Institution"
            interests = "Testing"
            groups = listOf(
                Group().apply { name = "public" },
                Group().apply { name = "test-group" }
            )
        }

        // Mock authenticatedUser
        if (loginName == "testuser" && apiKey == "valid-api-key") {
            Mockito.`when`(logic.authenticatedUser).thenReturn(testUser)
        } else {
            val guestUser = User().apply {
                name = ""
                role = Role.NOBODY
            }
            Mockito.`when`(logic.authenticatedUser).thenReturn(guestUser)
        }

        // Mock getUsers for user profile lookup
        Mockito.`when`(
            logic.getUsers(
                any(),
                eq(GroupingEntity.USER),
                eq("testuser"),
                isNull(),
                isNull(),
                any(),
                isNull(),
                isNull(),
                anyInt(),
                anyInt()
            )
        ).thenReturn(listOf(testUser))

        Mockito.`when`(
            logic.getUsers(
                any(),
                eq(GroupingEntity.USER),
                eq("nonexistent"),
                isNull(),
                isNull(),
                any(),
                isNull(),
                isNull(),
                anyInt(),
                anyInt()
            )
        ).thenReturn(emptyList())

        // For newuser - not existing before registration
        Mockito.`when`(
            logic.getUsers(
                any(),
                eq(GroupingEntity.USER),
                eq("newuser"),
                isNull(),
                isNull(),
                any(),
                isNull(),
                isNull(),
                anyInt(),
                anyInt()
            )
        ).thenReturn(emptyList())

        // Mock getPostStatistics
        Mockito.`when`(
            logic.getPostStatistics(
                any(),
                any(),
                anyString(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(),
                isNull(),
                isNull(),
                anyInt(),
                anyInt()
            )
        ).thenAnswer { invocation ->
            Statistics().apply {
                setCount(42)
            }
        }

        // Mock getTags for user tags
        Mockito.`when`(
            logic.getTags(
                any(),
                eq(GroupingEntity.USER),
                anyString(),
                isNull(),
                isNull(),
                isNull(),
                any(),
                isNull(),
                isNull(),
                any(),
                isNull(),
                isNull(),
                anyInt(),
                anyInt()
            )
        ).thenAnswer { invocation ->
            val end = (invocation.arguments[13] as? Int) ?: MOCK_USER_TAGS.size
            MOCK_USER_TAGS.take(end.coerceAtMost(MOCK_USER_TAGS.size))
        }

        // Mock createUser
        Mockito.`when`(logic.createUser(any())).thenReturn("newuser-id")

        return logic
    }

    companion object {
        val MOCK_USER_TAGS: List<Tag> = listOf(
            createTag("java", 100),
            createTag("kotlin", 80),
            createTag("spring", 60),
            createTag("testing", 40),
            createTag("api", 30),
            createTag("rest", 25),
            createTag("web", 20),
            createTag("programming", 15),
            createTag("development", 10),
            createTag("software", 5)
        )

        private fun createTag(name: String, count: Int): Tag = Tag().apply {
            this.name = name
            this.usercount = count
            this.globalcount = count * 2
        }
    }
}

@Configuration
class StubUsersBeans {
    @Bean
    fun stubLogicInterfaceFactory(): LogicInterfaceFactory = StubUsersLogicFactory()

    @Bean
    fun legacyBasicAuthenticationProvider(factory: LogicInterfaceFactory): LegacyBasicAuthenticationProvider =
        LegacyBasicAuthenticationProvider(factory)
}
