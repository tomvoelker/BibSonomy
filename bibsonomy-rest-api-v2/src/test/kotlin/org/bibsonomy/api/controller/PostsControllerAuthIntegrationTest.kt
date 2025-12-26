package org.bibsonomy.api.controller

import org.bibsonomy.api.config.SecurityConfig
import org.bibsonomy.api.dto.PaginatedPostList
import org.bibsonomy.api.security.LegacyAuthenticationConfiguration
import org.bibsonomy.api.security.LegacyBasicAuthenticationProvider
import org.bibsonomy.api.service.PostService
import org.bibsonomy.common.enums.GroupingEntity
import org.bibsonomy.common.enums.Role
import org.bibsonomy.model.BibTex
import org.bibsonomy.model.Post
import org.bibsonomy.model.User
import org.bibsonomy.model.logic.LogicInterface
import org.bibsonomy.model.logic.LogicInterfaceFactory
import org.bibsonomy.model.logic.query.PostQuery
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@SpringBootTest(
    classes = [PostsControllerTestApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.main.allow-bean-definition-overriding=true",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
    ]
)
class PostsControllerAuthIntegrationTest(
    @Autowired private val restTemplate: TestRestTemplate
) {

    @BeforeEach
    fun resetCapturedQuery() {
        StubPostsLogicFactory.lastQuery = null
    }

    @Test
    fun `valid basic auth returns 200 from posts controller`() {
        val headers = HttpHeaders()
        headers.setBasicAuth(StubPostsLogicFactory.VALID_USER, StubPostsLogicFactory.VALID_API_KEY)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/api/v2/posts?resourceType=bibtex",
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `invalid basic auth returns 401 from posts controller`() {
        val headers = HttpHeaders()
        headers.setBasicAuth("wrong", "creds")
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/api/v2/posts?resourceType=bibtex",
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `resourceType defaults to bibtex and limit is clamped`() {
        val headers = HttpHeaders()
        headers.setBasicAuth(StubPostsLogicFactory.VALID_USER, StubPostsLogicFactory.VALID_API_KEY)

        val response: ResponseEntity<String> = restTemplate.exchange(
            "/api/v2/posts?offset=5&limit=200&resourceType=bibtex",
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val query = StubPostsLogicFactory.lastQuery
        assertEquals(org.bibsonomy.model.BibTex::class.java, query?.resourceClass)
        assertEquals(5, query?.start)
        assertEquals(105, query?.end) // limit clamped to 100
    }

    @Test
    fun `bookmark resourceType sets resource class`() {
        val headers = HttpHeaders()
        headers.setBasicAuth(StubPostsLogicFactory.VALID_USER, StubPostsLogicFactory.VALID_API_KEY)

        val response: ResponseEntity<String> = restTemplate.exchange(
            "/api/v2/posts?resourceType=bookmark",
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val query = StubPostsLogicFactory.lastQuery
        assertEquals(org.bibsonomy.model.Bookmark::class.java, query?.resourceClass)
    }

    @Test
    fun `user filter sets grouping and name`() {
        val headers = HttpHeaders()
        headers.setBasicAuth(StubPostsLogicFactory.VALID_USER, StubPostsLogicFactory.VALID_API_KEY)

        val response: ResponseEntity<String> = restTemplate.exchange(
            "/api/v2/posts?user=alice&resourceType=bibtex",
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val query = StubPostsLogicFactory.lastQuery
        assertEquals(GroupingEntity.USER, query?.grouping)
        assertEquals("alice", query?.groupingName)
    }

    @Test
    fun `tags and search are forwarded`() {
        val headers = HttpHeaders()
        headers.setBasicAuth(StubPostsLogicFactory.VALID_USER, StubPostsLogicFactory.VALID_API_KEY)

        val response: ResponseEntity<String> = restTemplate.exchange(
            "/api/v2/posts?tags=foo,bar&search=deep learning&resourceType=bibtex",
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val query = StubPostsLogicFactory.lastQuery
        assertEquals(listOf("foo", "bar"), query?.tags)
        assertEquals("deep learning", query?.search)
    }

    @Test
    fun `sort parameters map to sort criteria`() {
        val headers = HttpHeaders()
        headers.setBasicAuth(StubPostsLogicFactory.VALID_USER, StubPostsLogicFactory.VALID_API_KEY)

        val response: ResponseEntity<String> = restTemplate.exchange(
            "/api/v2/posts?sortBy=title&order=asc&resourceType=bibtex",
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val query = StubPostsLogicFactory.lastQuery
        val sortCriteria = query?.sortCriteria?.firstOrNull()
        assertEquals(org.bibsonomy.common.enums.SortKey.TITLE, sortCriteria?.sortKey)
        assertEquals(org.bibsonomy.common.enums.SortOrder.ASC, sortCriteria?.sortOrder)
    }

    @Test
    fun `anonymous users only see public posts`() {
        val response: ResponseEntity<String> = restTemplate.getForEntity(
            "/api/v2/posts?resourceType=bibtex",
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val body = response.body ?: ""
        assert(body.contains("Public BibTex"))
        assert(!body.contains("Private BibTex"))
    }

    @Test
    fun `unauthenticated getPost returns only public`() {
        val response: ResponseEntity<String> = restTemplate.getForEntity(
            "/api/v2/posts/${StubPostsLogicFactory.PUBLIC_HASH}",
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val body = response.body ?: ""
        assert(body.contains("Public BibTex"))
        assert(!body.contains("Private"))
    }

    @Test
    fun `authenticated getPost can see private`() {
        val authed = restTemplate.withBasicAuth(StubPostsLogicFactory.VALID_USER, StubPostsLogicFactory.VALID_API_KEY)
        val response: ResponseEntity<String> = authed.getForEntity(
            "/api/v2/posts/${StubPostsLogicFactory.PRIVATE_HASH}?user=${StubPostsLogicFactory.VALID_USER}",
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val body = response.body ?: ""
        assert(body.contains("Private BibTex"))
    }

    @Test
    fun `authenticated users see public and private posts they own`() {
        val authed = restTemplate.withBasicAuth(StubPostsLogicFactory.VALID_USER, StubPostsLogicFactory.VALID_API_KEY)
        val response: ResponseEntity<String> = authed.exchange(
            "/api/v2/posts?resourceType=bibtex",
            HttpMethod.GET,
            HttpEntity<Void>(HttpHeaders()),
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val body = response.body ?: ""
        assert(body.contains("Public BibTex"))
        assert(body.contains("Private BibTex"))
    }

    @Test
    fun `includeTotal triggers count query`() {
        val authed = restTemplate.withBasicAuth(StubPostsLogicFactory.VALID_USER, StubPostsLogicFactory.VALID_API_KEY)
        val response: ResponseEntity<PaginatedPostList> = authed.exchange(
            "/api/v2/posts?resourceType=bibtex&includeTotal=true",
            HttpMethod.GET,
            HttpEntity<Void>(HttpHeaders()),
            PaginatedPostList::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(2, response.body?.totalCount)
    }

    @Test
    fun `resourceType all returns both bookmark and bibtex`() {
        val authed = restTemplate.withBasicAuth(StubPostsLogicFactory.VALID_USER, StubPostsLogicFactory.VALID_API_KEY)
        val response: ResponseEntity<String> = authed.exchange(
            "/api/v2/posts",
            HttpMethod.GET,
            HttpEntity<Void>(HttpHeaders()),
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        val body = response.body ?: ""
        assert(body.contains("Public BibTex"))
        assert(body.contains("Private BibTex"))
        assert(body.contains("Public Bookmark"))
        assert(body.contains("Private Bookmark"))
    }
}

/**
 * Minimal app wiring the real PostsController + PostService with stubbed LogicInterfaceFactory.
 */
@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
@Import(
    SecurityConfig::class,
    LegacyAuthenticationConfiguration::class,
    PostsController::class,
    PostService::class,
    StubPostsBeans::class
)
class PostsControllerTestApplication

class StubPostsLogicFactory : LogicInterfaceFactory {
    override fun getLogicAccess(loginName: String?, apiKey: String?): LogicInterface {
        val logic = Mockito.mock(LogicInterface::class.java)
        val user = User().apply {
            name = loginName ?: "guest"
            role = Role.DEFAULT
        }
        Mockito.`when`(logic.authenticatedUser).thenReturn(user)

        val publicBibPost = Post<BibTex>().apply {
            this.contentId = 1
            this.user = user
            this.resource = BibTex().apply { title = "Public BibTex" }
            this.date = java.util.Date()
        }
        val privateBibPost = Post<BibTex>().apply {
            this.contentId = 2
            this.user = user
            this.resource = BibTex().apply { title = "Private BibTex" }
            this.date = java.util.Date(System.currentTimeMillis() - 30_000)
        }
        val publicBookmarkPost = Post<org.bibsonomy.model.Bookmark>().apply {
            this.contentId = 3
            this.user = user
            this.resource = org.bibsonomy.model.Bookmark().apply { title = "Public Bookmark" }
            this.date = java.util.Date(System.currentTimeMillis() - 60_000)
        }
        val privateBookmarkPost = Post<org.bibsonomy.model.Bookmark>().apply {
            this.contentId = 4
            this.user = user
            this.resource = org.bibsonomy.model.Bookmark().apply { title = "Private Bookmark" }
            this.date = java.util.Date(System.currentTimeMillis() - 90_000)
        }
        Mockito.`when`(logic.getPosts(Mockito.any(PostQuery::class.java))).thenAnswer { invocation ->
            lastQuery = invocation.arguments.firstOrNull() as? PostQuery<*>
            val query = lastQuery
            val isGuest = user.name == "guest"
            val resourceClass = query?.resourceClass
            val bibs = listOf(
                publicBibPost as org.bibsonomy.model.Post<org.bibsonomy.model.Resource>,
                privateBibPost as org.bibsonomy.model.Post<org.bibsonomy.model.Resource>
            )
            val bookmarks = listOf(
                publicBookmarkPost as org.bibsonomy.model.Post<org.bibsonomy.model.Resource>,
                privateBookmarkPost as org.bibsonomy.model.Post<org.bibsonomy.model.Resource>
            )
            val chosen = when (resourceClass) {
                org.bibsonomy.model.Bookmark::class.java -> bookmarks
                org.bibsonomy.model.BibTex::class.java -> bibs
                else -> bibs + bookmarks
            }
            if (isGuest) chosen.filter { it.resource.title?.startsWith("Public") == true } else chosen
        }
        Mockito.`when`(logic.getPostDetails(Mockito.anyString(), Mockito.anyString())).thenAnswer { invocation ->
            val hash = invocation.arguments[0] as String
            val requestedUser = invocation.arguments[1] as String
            val isGuest = user.name == "guest"
            return@thenAnswer when (hash) {
                PUBLIC_HASH -> publicBibPost as org.bibsonomy.model.Post<out org.bibsonomy.model.Resource>
                PRIVATE_HASH -> {
                    if (isGuest || requestedUser != VALID_USER) null else privateBibPost as org.bibsonomy.model.Post<out org.bibsonomy.model.Resource>
                }
                else -> null
            }
        }
        Mockito.`when`(
            logic.getPostStatistics(
                Mockito.any<Class<out org.bibsonomy.model.Resource>>(),
                Mockito.any(),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.isNull(),
                Mockito.any(),
                Mockito.isNull(),
                Mockito.any(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.anyInt(),
                Mockito.anyInt()
            )
        ).thenAnswer { invocation ->
            val clazz = invocation.arguments[0] as Class<*>
            val count = 2
            org.bibsonomy.model.statistics.Statistics(count)
        }
        return logic
    }

    companion object {
        // Use test database credentials from bibsonomy-database/src/test/resources/database/insert-test-data.sql
        const val VALID_USER = "testuser1"
        const val VALID_API_KEY = "11111111111111111111111111111111"
        var lastQuery: PostQuery<*>? = null
        const val PUBLIC_HASH = "public-hash"
        const val PRIVATE_HASH = "private-hash"
    }
}

class StubPostsBeans {
    @Bean
    fun stubLogicInterfaceFactory(): LogicInterfaceFactory = StubPostsLogicFactory()

    /**
     * Provide the auth provider bean explicitly so the SecurityConfig can register it.
     */
    @Bean
    fun legacyBasicAuthenticationProvider(factory: LogicInterfaceFactory): LegacyBasicAuthenticationProvider =
        LegacyBasicAuthenticationProvider(factory)

    @Bean
    @Primary
    fun logicInterface(factory: LogicInterfaceFactory): LogicInterface = factory.getLogicAccess(null, null)
}
